package twg2.jbcm.main;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.Settings;
import twg2.jbcm.classFormat.constantPool.CONSTANT_CP_Info;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Class;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Utf8;
import twg2.jbcm.dynamicModification.SimpleInterface;
import twg2.jbcm.modify.FindCpIndexUsage;
import twg2.jbcm.runtime.ClassLoaders;
import twg2.jbcm.runtime.CompileSource;
import twg2.jbcm.toSource.ClassFileToSource;
import twg2.jbcm.toSource.SourceWriter;

public class UsageCliMain {

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
		long start = 0;
		for(int i = 0; i < loops; i++) {
			start = System.nanoTime();
			for(int a = 0; a < innerLoops; a++) {
				base.callTest();
			}
			timeBase += (System.nanoTime() - start);

			start = System.nanoTime();
			for(int a = 0; a < innerLoops; a++) {
				test.callTest();
			}
			timeTest += (System.nanoTime() - start);
		}
		System.out.println("Time base: " + timeBase + ", base=" + base.getCount());
		System.out.println("Time test: " + timeTest + ", test=" + test.getCount());
	}


	public static void interactiveClassLoad(String... args) {
		var nextLineGetter = createLineGetter(args, System.in);
		List<ClassFile> classFiles = new ArrayList<>();
		int errorCount = 0;
		int errorMax = 10;
		String str = null;
		System.out.println("Class file manipulator:");
		do {
			try {
				displayMenuGetUserInput(nextLineGetter, classFiles);
			} catch (IOException e) {
				errorCount++;
				if(errorCount > errorMax) { break; }
				e.printStackTrace();
			}
		} while(str == null || !str.equals("exit"));
	}


	private static void displayMenuGetUserInput(Supplier<String> userInputNextLine, List<ClassFile> classFiles) throws IOException {
		var nextLine = userInputNextLine;
		FileSystem fs = FileSystems.getDefault();
		String appPath = ClassLoaders.getApplicationPathString();
		String path = new File(appPath).getParent() + "/res/destination";
		String classpath = System.getProperty("user.dir");
		if(!classpath.endsWith("\\") && !classpath.endsWith("/")) {
			classpath += "/";
		}

		System.out.print((classFiles.size() > 0 ? "(" + classFiles.size() + " loaded), " : "") +
				"options ('classpath', 'printInfo', 'printClass', 'decompile', 'dependencies', 'load', 'modify', 'run', 'clear', 'save'): ");
		String input = nextLine.get();
		ClassFile cls = null;
		SourceWriter writer = null;
		Object[] args = new String[0];

		switch(input) {
		// classpath
		case "classpath":
			System.out.print("enter classpath to use for 'load' commands: ");
			input = nextLine.get();
			classpath = input;
			break;
		// decompile
		case "decompile":
			String methodFilter = null;
			if(classFiles.size() < 1) {
				System.out.print("enter file name (relative to '" + classpath + "' or absolute) to print source: ");
				input = nextLine.get();
				// allow file names like "\bin\twg2\jbcm\modify\TypeUtility.class isPrimitive()
				if(input.endsWith("()")) {
					int spaceIdx = input.lastIndexOf(' ');
					methodFilter = input.substring(spaceIdx + 1, input.length() - 2);
					input = input.substring(0, spaceIdx);
				}
				File file = getClassPath(fs, classpath, input).toFile();
				cls = ClassFile.load(file);
			}
			else {
				cls = classFiles.get(0);
			}
			writer = new SourceWriter("\t", "\n");
			if(methodFilter == null) {
				ClassFileToSource.toSource(cls, writer, true);
			}
			else {
				ClassFileToSource.methodsToSource(cls, writer, true, methodFilter);
			}
			System.out.println(writer.toString());
			break;
		// printClass
		case "printClass":
			if(classFiles.size() < 1) {
				System.out.print("enter file name (relative to '" + classpath + "' or absolute) to print class: ");
				input = nextLine.get();
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
		// printInfo
		case "printInfo":
			if(classFiles.size() < 1) {
				System.out.print("enter file name (relative to '" + classpath + "' or absolute) to print: ");
				input = nextLine.get();
				File file = getClassPath(fs, classpath, input).toFile();
				cls = ClassFile.load(file);
			}
			else {
				cls = classFiles.get(0);
			}
			cls.print(System.out);
			break;
		// load
		case "load":
			System.out.print("enter file/path name (relative to '" + classpath + "' or absolute) to load: ");
			input = nextLine.get();
			File file = getClassPath(fs, classpath, input).toFile();
			List<File> files;
			if(file.isDirectory()) {
				files = Arrays.asList(file.listFiles((f) -> f.getName().endsWith(".class")));
			}
			else {
				files = Arrays.asList(file);
			}
			for(int i = 0, sz = files.size(); i < sz; i++) {
				cls = ClassFile.load(files.get(i));
				classFiles.add(cls);
			}
			System.out.println(files.size() + " class files loaded");
			break;
		// dependencies
		case "dependencies":
			System.out.print("enter dependency name to search for (optional) or nothing to print all dependencies: ");
			input = nextLine.get();
			for(int i = 0, sz = classFiles.size(); i < sz; i++) {
				printDeps(classFiles.get(i), input, System.out);
			}
			break;
		// modify
		case "modify":
			System.out.println("Modification Options: ");
			System.out.println("1. 'removeUnusedCPs' - remove constant pool entries");
			System.out.println("2. 'replaceCP [# | \"string\"] \"replacement\"' - replace constant pool string");
			System.out.print("Choice: ");
			input = nextLine.get();
			cls = classFiles.size() > 0 ? classFiles.get(0) : null;
			if(input.equals("removeUnusedCPs")) {
				var unusedIndexes = FindCpIndexUsage.findUnusedIndexes(cls);
				System.out.println("unused indexes: " + unusedIndexes);
				//var removedIndexes = cls.removeUnusedCpIndexes();
				//System.out.println("Removed unused constant pool entries: " + removedIndexes.toString());
			}
			else if(input.startsWith("replaceCP")) {
				var strs = input.split(" \"");
				CpIndex<CONSTANT_Utf8> strCp;
				if(strs.length == 3) {
					strCp = cls.findConstantPoolString(strs[1]);
				}
				else {
					int idx = Integer.parseInt(strs[0].split(" ")[1]);
					strCp = cls.getCheckCpIndex(idx, CONSTANT_Utf8.class);
				}
				if(strCp != null) {
					var utfStr = new CONSTANT_Utf8(cls);
					utfStr.setString(strs[1].endsWith("\"") ? strs[1].substring(0, strs[1].length() - 1) : strs[1]);
					strCp.setCpObject(utfStr);
					cls.setConstantPool(strCp.getIndex(), strCp.getCpObject());
				}
				else {
					System.err.println("Could not find constant pool string \"" + strs[1] + "\"");
				}
			}
			else {
				System.err.println("ERROR: unknown modification option '" + input + "'");
			}
			break;
		// run
		case "run":
			System.out.print("Load a class, example 'classLoading.base.Test.main()' or 'classLoading.Reload.main()': ");
			input = nextLine.get();
			String[] classAndMethod = splitClassAndMethod(input);
			System.out.println("running '" + classAndMethod[0] + "." + classAndMethod[1] + "()...");
			var t = ClassLoaders.callClassMethod(path, classAndMethod[0], classAndMethod[1], null, (Object)args);
			try {
				t.thread.join();
			} catch (InterruptedException e) {
				System.err.println("Error waiting for thread: " + e.getLocalizedMessage());
			}
			break;
		// save
		case "save":
			cls = classFiles.get(0);
			DataOutputStream out = new DataOutputStream(new FileOutputStream(cls.getSource().replace(".class", "-modified.class")));
			cls.writeData(out);
			out.close();
			break;
		// clear
		case "clear":
			classFiles.clear();
			break;
		default:
			break;
		}
	}


	private static Supplier<String> createLineGetter(String[] lines, InputStream in) {
		if(lines != null && lines.length > 0) {
			var idx = new AtomicInteger(0);
			return () -> lines.length > idx.get() ? lines[idx.getAndIncrement()] : null;
		}
		else {
			var inScanner = new Scanner(in);
			return () -> inScanner.nextLine();
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


	/** Splits a string in the format 'class.path.and.Name.methodName()' into
	 * two strings, the full class name and the method name,
	 * like ['class.path.and.Name', 'methodName']
	 */
	private static String[] splitClassAndMethod(String input) {
		int lastDot = input.lastIndexOf('.');
		String method = input.substring(lastDot + 1, input.length() - 2);
		String fullClass = input.substring(0, lastDot);
		return new String[] { fullClass, method };
	}


	private static ClassFile compileAndLoad(String javaSrc) throws MalformedURLException, ClassNotFoundException {
		try {
			File tmpFile = new File("res/tmp/src/CompilerTemp.java");
			Files.write(tmpFile.toPath(), javaSrc.getBytes("UTF-8"), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
			File sourceDir = new File("res/tmp/src");
			String classPath = sourceDir.getAbsolutePath();
			File destinationDir = new File("res/tmp/bin");

			//CompileSource.compile(CompileSource.JAVA_1_8_COMPILE, tmpFile, sourceDir, classPath, destinationDir);
			CompileSource.compileUsingEclipseEcj(tmpFile, sourceDir, classPath, destinationDir);

			var bytecode = ClassFile.load(new File("res/tmp/bin/CompilerTemp.class"));

			tmpFile.delete();

			return bytecode;
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}


	public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		Settings.debug = true;

		interactiveClassLoad(new String[] { "decompile", "res/destination/classLoading/RunnableThing.class" });

		// compileAndLoad();

		//File file = new File("bin/twg2.jbcm.classFormat/test/UnitTest.class");
		//File file = new File("C:/Users/TeamworkGuy2/Documents/Java/Projects/ClassLoading/bin/twg2.jbcm.classFormat/Field_Info.class");
		//File file = new File("C:/Users/TeamworkGuy2/Documents/Java/Projects/FileIO/bin/utilities/vlcConverter/VLCUtility.class");

		//UnitTest.loadPrintClassInfo(System.out/*new PrintStream(new File("output-parsed.txt"))*/, file);
	}

}
