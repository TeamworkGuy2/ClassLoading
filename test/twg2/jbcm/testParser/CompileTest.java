package twg2.jbcm.testParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.runtimeLoading.CompileSource;
import twg2.jbcm.toSource.ClassFileToSource;
import twg2.jbcm.toSource.SourceWriter;

/** Compile Java source code to byte code
 * @author TeamworkGuy2
 * @since 2020-07-19
 */
public class CompileTest {

	/** Compile Java source code and return the byte code result
	 * @param srcCode the source code to compile
	 * @return the byte code result
	 */
	public static byte[] compileCode(String srcCode) {
		return compileCode(java.util.Collections.emptyList(), srcCode);
	}


	/** Compile Java source code and return the byte code result
	 * @param imports the imports required to compile the source code
	 * @param srcCode the source code to compile
	 * @return the byte code result
	 */
	public static byte[] compileCode(Iterable<String> imports, String srcCode) {
		ClassFile javaClass = compileToClass(imports, srcCode);
		String binaryClassName = javaClass.getClassIndex().getCpObject().getName().getString();
		String fullClassName = binaryClassName.replace('/', '.');
		String className = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);

		var runMethod = javaClass.getMethod(1); // skip constructor and get 'run()' method

		SourceWriter writer = new SourceWriter("\t", "\n");
		ClassFileToSource.methodToSource(javaClass, fullClassName, className, runMethod, writer, false);
		System.out.println("Source:\n" + srcCode +
				"\nCompiled:\n" + writer.toString());

		return runMethod.getCode().getCode();
	}


	/** Compile Java source code into a {@code run()} method in a class named {@link CompileSource#defaultCompileClassName}
	 * @param imports the imports required to compile the source code
	 * @param srcCode the source {@code run()} method source code
	 * @return the compiled class file
	 */
	public static ClassFile compileToClass(Iterable<String> imports, String srcCode) {
		var importsSrc = String.join(";\nimport ", imports);
		var javaRes = CompileSource.compileSourceToBytecode(
			(importsSrc.length() > 0 ? "import " + importsSrc + ";\n\n" : "") +
			wrapInClass(srcCode, CompileSource.defaultCompileClassName)
		);

		try {
			return ClassFile.load(new ByteArrayInputStream(javaRes));
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
