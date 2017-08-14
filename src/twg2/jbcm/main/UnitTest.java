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
import java.util.Scanner;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.dynamicModification.SimpleInterface;


public class UnitTest {
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
		ClassFile classFile = null;
		int errorCount = 0;
		int errorMax = 10;
		String str = null;
		do {
			try {
				classFile = displayMenuGetUserInput(in, classFile);
			} catch (IOException e) {
				errorCount++;
				if(errorCount > errorMax) { break; }
				e.printStackTrace();
			}
		} while(str == null || !str.equals("exit"));
	}


	private static ClassFile displayMenuGetUserInput(Scanner in, ClassFile classFile) throws IOException {
		FileSystem fs = FileSystems.getDefault();
		String userDir = System.getProperty("user.dir");
		if(!userDir.endsWith("\\") && !userDir.endsWith("/")) {
			userDir += "/";
		}

		System.out.print("Class file manipulator, options: 'printInfo', 'load', 'modify': ");
		String input = in.nextLine();
		Path path = null;
		switch(input) {
		case "printInfo":
			System.out.print("enter file name (relative to '" + userDir + "' or absolute) to print: ");
			input = in.nextLine();
			path = getPath(fs, userDir, input);
			printInfo(path);
			return null;
		case "load":
			System.out.print("enter file name (relative to '" + userDir + "' or absolute) to load: ");
			input = in.nextLine();
			path = getPath(fs, userDir, input);
			classFile = ClassFile.load(path.toFile());
			return classFile;
		case "modify":
			System.out.print("enter modification (currently not implemented): ");
			input = in.nextLine();
			modify(classFile, input);
			return classFile;
		default:
			return null;
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


	private static void printInfo(Path path) throws IOException {
		ClassFile.load(path.toFile()).print(System.out);
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
