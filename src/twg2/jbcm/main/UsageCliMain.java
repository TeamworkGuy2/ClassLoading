package twg2.jbcm.main;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.constantPool.CONSTANT_CP_Info;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Class;
import twg2.jbcm.dynamicModification.SimpleInterface;
import twg2.jbcm.runtimeLoading.CompileSource;
import twg2.jbcm.toSource.ClassFileToSource;
import twg2.jbcm.toSource.SourceWriter;


public class UsageCliMain {
	static final String a = "twg2/jbcm/main/test/UnitTest";
	static final String b = "UnitTest-Other";


	/** Print the class file format data about the specified class
	 * @param file the class file to print the internal data of
	 * @throws IOException if there is an error reading the file
	 */
	public static ClassFile loadPrintClassInfo(PrintStream stream, File file) throws IOException {
		ClassFile classFile = ClassFile.load(file);
		DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));

		long time = System.nanoTime();
		classFile.readData(in);
		long timeEnd = System.nanoTime();

		in.close();

		// Print the class file's information
		classFile.print(stream);

		stream.println("Load time (ns): " + (timeEnd - time));
		stream.println("-end-SourceFile");

		return classFile;
	}


	public static void testPerformance(SimpleInterface test, SimpleInterface base) {
		int loops = 100;
		int innerLoops = 100;
		long timeTest = 0;
		long timeBase = 0;
		long temp = 0;
		for(int i = 0; i < loops; i++) {
			temp = System.nanoTime();
			for(int a = 0; a < innerLoops; a++) {
				base.callTest();
			}
			timeBase += (System.nanoTime() - temp);

			temp = System.nanoTime();
			for(int a = 0; a < innerLoops; a++) {
				test.callTest();
			}
			timeTest += (System.nanoTime() - temp);
		}
		System.out.println("Time base: " + timeBase + ", base=" + base.getCount());
		System.out.println("Time test: " + timeTest + ", test=" + test.getCount());
	}


	public static void interactiveClassLoad() {
		Scanner in = new Scanner(System.in);
		List<ClassFile> classFiles = new ArrayList<>();
		int errorCount = 0;
		int errorMax = 10;
		String str = null;
		do {
			try {
				displayMenuGetUserInput(in, classFiles);
			} catch (IOException e) {
				errorCount++;
				if(errorCount > errorMax) { break; }
				e.printStackTrace();
			}
		} while(str == null || !str.equals("exit"));
	}


	private static void displayMenuGetUserInput(Scanner in, List<ClassFile> classFiles) throws IOException {
		FileSystem fs = FileSystems.getDefault();
		String classpath = System.getProperty("user.dir");
		if(!classpath.endsWith("\\") && !classpath.endsWith("/")) {
			classpath += "/";
		}

		System.out.print("Class file manipulator " + (classFiles.size() > 0 ? "(" + classFiles.size() + " loaded)" : "") +
				", options ('classpath', 'printInfo', 'printClass', 'decompile', 'dependencies', 'load', 'modify', 'clear'): ");
		String input = in.nextLine();
		ClassFile cls = null;
		SourceWriter writer = null;

		switch(input) {
		case "classpath":
			System.out.print("enter classpath to use for 'load' commands: ");
			input = in.nextLine();
			classpath = input;
		case "decompile":
			if(classFiles.size() < 1) {
				System.out.print("enter file name (relative to '" + classpath + "' or absolute) to print source: ");
				input = in.nextLine();
				File file = getClassPath(fs, classpath, input).toFile();
				cls = ClassFile.load(file);
			}
			else {
				cls = classFiles.get(0);
			}
			writer = new SourceWriter("\t", "\n");
			ClassFileToSource.toSource(cls, writer, true);
			System.out.println(writer.toString());
			break;
		case "printClass":
			if(classFiles.size() < 1) {
				System.out.print("enter file name (relative to '" + classpath + "' or absolute) to print class: ");
				input = in.nextLine();
				File file = getClassPath(fs, classpath, input).toFile();
				cls = ClassFile.load(file);
			}
			else {
				cls = classFiles.get(0);
			}
			writer = new SourceWriter("  ", "\n");
			ClassFileToSource.toSource(cls, writer, false);
			System.out.println(writer.toString());
			break;
		case "printInfo":
			if(classFiles.size() < 1) {
				System.out.print("enter file name (relative to '" + classpath + "' or absolute) to print: ");
				input = in.nextLine();
				File file = getClassPath(fs, classpath, input).toFile();
				cls = ClassFile.load(file);
			}
			else {
				cls = classFiles.get(0);
			}
			cls.print(System.out);
			break;
		case "load":
			System.out.print("enter file/path name (relative to '" + classpath + "' or absolute) to load: ");
			input = in.nextLine();
			File file = getClassPath(fs, classpath, input).toFile();
			List<File> files;
			if(file.isDirectory()) {
				files = Arrays.asList(file.listFiles((f) -> f.getName().endsWith(".class")));
			}
			else {
				files = Arrays.asList(file);
			}
			for(int i = 0, sz = files.size(); i < sz; i++) {
				classFiles.add(ClassFile.load(files.get(i)));
			}
			System.out.println(files.size() + " class files loaded");
			break;
		case "dependencies":
			System.out.print("enter dependency name to search for (optional) or nothing to print all dependencies: ");
			input = in.nextLine();
			for(int i = 0, sz = classFiles.size(); i < sz; i++) {
				printDeps(classFiles.get(i), input, System.out);
			}
			break;
		case "modify":
			System.out.print("enter modification (currently not implemented): ");
			input = in.nextLine();
			// TODO modify(classFiles.get(0), input);
			System.out.println("ERROR: 'modify' is not yet implemented");
			break;
		case "clear":
			classFiles.clear();
			break;
		default:
			break;
		}
	}


	private static Path getClassPath(FileSystem fs, String dir, String file) {
		String fileNorm = file.endsWith(".class") ? file : file.replace('.', '/') + ".class";
		Path tmp = null;
		if(Files.exists((tmp = fs.getPath(fileNorm)), LinkOption.NOFOLLOW_LINKS)) {
			return tmp;
		}
		else if(Files.exists((tmp = fs.getPath(dir, fileNorm)), LinkOption.NOFOLLOW_LINKS)) {
			return tmp;
		}
		else {
			System.out.println("unknown path: " + fileNorm + ", or " + dir + fileNorm);
			return null;
		}
	}


	private static void printDeps(ClassFile classFile, String dependency, PrintStream out) {
		String depCleaned = (dependency != null && (dependency = dependency.trim()).length() > 0 ? dependency.replace('.', '.') : null);
		out.println(classFile.getClassIndex().getCpObject() + " dependencies:");
		int cfIdx = classFile.getClassIndex().getIndex();
		for(int i = 1, sz = classFile.getConstantPoolCount(); i < sz; i++) { // starting from 1
			CONSTANT_CP_Info cpInf = classFile.getCpIndex(i).getCpObject();
			if(i != cfIdx && cpInf instanceof CONSTANT_Class) {
				CONSTANT_Class cpCls = (CONSTANT_Class)cpInf;
				if(depCleaned != null) {
					if(cpCls.getName().getString().contains(depCleaned)) {
						out.println("  " + cpCls.getName());
					}
				}
				else {
					out.println("  " + cpCls.getName());
				}
			}
		}
	}


	private static byte[] compileSourceToBytecode(String javaSrc) throws MalformedURLException, ClassNotFoundException {
		try {
			File tmpFile = new File("res/tmp/src/CompilerTemp.java");
			Files.write(tmpFile.toPath(), javaSrc.getBytes("UTF-8"), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
			File sourceFilesPath = new File("res/tmp/src");
			String classPath = new File("res/tmp/src").getAbsolutePath();
			File destinationClassPath = new File("res/tmp/bin");

			CompileSource.compile(true, tmpFile, sourceFilesPath, classPath, destinationClassPath);

			var bytecode = Files.readAllBytes(new File("res/tmp/bin/CompilerTemp.class").toPath());

			tmpFile.delete();

			return bytecode;
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}


	private static void compileSourceFileToClass(File srcJavaFile, File dstClassFile) throws ClassNotFoundException, IOException {
		File entryFile = new File("res/tmp/src/twg2/compileTest/Hello.java");
		File sourceFilesPath = new File("res/tmp/src");
		String classPath = new File("res/tmp/src/twg2/compileTest").getAbsolutePath();
		File destinationClassPath = new File("res/tmp/bin");

		CompileSource.compile(true, entryFile, sourceFilesPath, classPath, destinationClassPath);

		RuntimeReloadMain reload = new RuntimeReloadMain();
		reload.loadRun("res/tmp/bin", "twg2.compileTest.Hello", "run", (String[])null);

		//Runnable thing = (Runnable)classes[0].newInstance();
		//thing.run();
	}


	public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		interactiveClassLoad();

		// compileSourceToClass();

		//File file = new File("bin/twg2.jbcm.classFormat/test/UnitTest.class");

		//UnitTest.loadPrintClassInfo(System.out/*new PrintStream(new File("output-parsed.txt"))*/, file);
	}

}
