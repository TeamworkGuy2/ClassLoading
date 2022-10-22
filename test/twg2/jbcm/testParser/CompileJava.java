package twg2.jbcm.testParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.runtime.CompileSource;
import twg2.jbcm.toSource.ClassFileToSource;
import twg2.jbcm.toSource.SourceWriter;

/** Compile Java source code to byte code
 * @author TeamworkGuy2
 * @since 2020-07-19
 */
public class CompileJava {

	/** Compile Java source code and return the byte code result
	 * @param srcCode the source code to compile
	 * @param printCode whether to print the source and compiled byte-code
	 * @return the byte code result
	 */
	public static byte[] compileCode(String srcCode) {
		return compileCode(CompileSource.defaultCompileClassName, Collections.emptyList(), srcCode, CompileSource.printCode);
	}


	/** Compile Java source code and return the byte code result
	 * @param imports the imports required to compile the source code, full class names or package names ends with wildcard,
	 * not prefixed by {@code "import "}
	 * @param srcCode the source code to compile
	 * @param printCode whether to print the source and compiled byte-code
	 * @return the byte code result
	 */
	public static byte[] compileCode(Iterable<String> imports, String srcCode, boolean printCode) {
		return compileCode(CompileSource.defaultCompileClassName, imports, srcCode, printCode);
	}


	/** Compile Java source code and return the byte code result
	 * @param className the simple name of the class (i.e. {@code MathUtils})
	 * @param imports the imports required to compile the source code, full class names or package names ends with wildcard,
	 * not prefixed by {@code "import "}
	 * @param srcCode the source code to compile
	 * @param printCode whether to print the source and compiled byte-code
	 * @return the byte code result
	 */
	public static byte[] compileCode(String className, Iterable<String> imports, String srcCode, boolean printCode) {
		ClassFile javaClass = compileToClass(className, imports, srcCode);
		String binaryClassName = javaClass.getClassIndex().getCpObject().getName().getString();
		String fullClassName = binaryClassName.replace('/', '.');
		String simpleClassName = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);

		var runMethod = javaClass.getMethod(1); // skip constructor and get 'run()' method

		if(printCode) {
			SourceWriter writer = new SourceWriter("\t", "\n");
			ClassFileToSource.methodToSource(javaClass, fullClassName, simpleClassName, runMethod, writer, false);
			System.out.println("Source:\n" + srcCode +
					"\nCompiled:\n" + writer.toString());
		}

		return runMethod.getCode().getCode();
	}


	/** Compile Java source code into a {@code run()} method in a class named {@link CompileSource#defaultCompileClassName}
	 * @param className the simple name of the class (i.e. {@code com.app.utils.MathUtils})
	 * @param imports the imports required to compile the source code, full class names or package names ends with wildcard,
	 * not prefixed by {@code "import "}
	 * @param srcCode the source {@code run()} method source code
	 * @return the compiled class file
	 */
	public static ClassFile compileToClass(String fullClassName, Iterable<String> imports, String srcCode) {
		String simpleClassName = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
		String packageName = simpleClassName.length() < fullClassName.length() ? fullClassName.substring(0, fullClassName.length() - simpleClassName.length() - 1) : "";
		String packagePath = packageName.length() > 0 ? '/' + packageName.replace('.', '/') : "";
		String importsSrc = String.join(";\nimport ", imports);
		var javaRes = CompileSource.compileSourceToBytecode(
			"res/tmp/src",
			"res/tmp/bin",
			packagePath,
			simpleClassName,
			(packageName.length() > 0 ? "package " + packageName + ";\n\n" : "") +
			(importsSrc.length() > 0 ? "import " + importsSrc + ";\n\n" : "") +
			wrapInClass(srcCode, simpleClassName)
		);

		try {
			return ClassFile.load(new ByteArrayInputStream(javaRes), fullClassName);
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}


	private static String wrapInClass(String srcCode, String className) {
		return
			"public class " + className + " {\n" +
			"    public void run() {\n" +
			"        " + srcCode + "\n" +
			"    }\n" +
			"}\n";
	}

}
