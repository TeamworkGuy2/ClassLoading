package twg2.jbcm.main;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.constantPool.CONSTANT_CP_Info;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Class;
import twg2.jbcm.dynamicModification.SimpleInterface;


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
			timeBase += (System.nanoTime()-temp);
			temp = System.nanoTime();
			for(int a = 0; a < innerLoops; a++) {
				test.callTest();
			}
			timeTest += (System.nanoTime()-temp);
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
		String userDir = System.getProperty("user.dir");
		if(!userDir.endsWith("\\") && !userDir.endsWith("/")) {
			userDir += "/";
		}

		System.out.print("Class file manipulator " + (classFiles.size() > 0 ? "(" + classFiles.size() + " loaded)" : "") +
				", options ('printInfo', 'dependencies', 'load', 'modify', 'clear'): ");
		String input = in.nextLine();
		switch(input) {
		case "printInfo":
			if(classFiles.size() < 1) {
				System.out.print("enter file name (relative to '" + userDir + "' or absolute) to print: ");
				input = in.nextLine();
				File file = getPath(fs, userDir, input).toFile();
				ClassFile.load(file).print(System.out);
			}
			else {
				classFiles.get(0).print(System.out);
			}
			break;
		case "dependencies":
			System.out.print("enter dependency name to search for (optional) or nothing to print all dependencies: ");
			input = in.nextLine();
			for(int i = 0, sz = classFiles.size(); i < sz; i++) {
				printDeps(classFiles.get(i), input, System.out);
			}
			break;
		case "load":
			System.out.print("enter file/path name (relative to '" + userDir + "' or absolute) to load: ");
			input = in.nextLine();
			File file = getPath(fs, userDir, input).toFile();
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
		case "modify":
			System.out.print("enter modification (currently not implemented): ");
			input = in.nextLine();
			modify(classFiles.get(0), input);
			break;
		case "clear":
			classFiles.clear();
			break;
		default:
			break;
		}
	}


	private static Path getPath(FileSystem fs, String dir, String file) {
		Path tmp = null;
		if(Files.exists((tmp = fs.getPath(file)), LinkOption.NOFOLLOW_LINKS)) {
			return tmp;
		}
		else if(Files.exists((tmp = fs.getPath(file)), LinkOption.NOFOLLOW_LINKS)) {
			return tmp;
		}
		else {
			System.out.println("unknown path: " + file + ", or " + dir + file);
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


	private static void modify(ClassFile classFile, String inputCommand) {
		
	}


	public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		interactiveClassLoad();

		//File file = new File("bin/twg2.jbcm.classFormat/test/UnitTest.class");
		//File file = new File("C:/Users/TeamworkGuy2/Downloads/apache-tomcat-7.0.42-windows-x64/apache-tomcat-7.0.42-windows-x64/webapps/Project5/WEB-INF/classes/threeTierWebApplication/ProxyTable.class");
		//File file = new File("C:/Users/TeamworkGuy2/Documents/Java/Projects/ClassLoading/bin/twg2.jbcm.classFormat/Field_Info.class");
		//File file = new File("C:/Users/TeamworkGuy2/Documents/Java/Projects/FileIO/bin/utilities/vlcConverter/VLCUtility.class");
		//File file = new File("C:/Users/TeamworkGuy2/Documents/Java/Projects/Miscellaneous/bin/classTests/Thing.class");
		//File file = new File("C:/Users/TeamworkGuy2/Documents/Java/Projects/Miscellaneous/bin/classTests/Thing.class");

		//UnitTest.loadPrintClassInfo(System.out/*new PrintStream(new File("output-parsed.txt"))*/, file);

		/*
		URL path = new File("res/compile").toURI().toURL();
		URL sourceClassPath = new File("res/compile/source_files").toURI().toURL();
		URL destinationClassPath = new File("res/compile/class_files").toURI().toURL();
		URL[] urls = new URL[] {
			new File("res/compile/source_files/compile/Hello.java").toURI().toURL(),
		};
		CompileSource c = new CompileSource();
		Class<?>[] classes = c.compile(path, sourceClassPath, destinationClassPath, urls);
		//Runnable thing = (Runnable)classes[0].newInstance();
		//thing.run();
		*/
	}

}
