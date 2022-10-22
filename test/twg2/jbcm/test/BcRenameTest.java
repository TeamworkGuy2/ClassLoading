package twg2.jbcm.test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.runtime.ClassLoaders;
import twg2.jbcm.runtime.CompileSource;
import twg2.jbcm.runtime.ClassLoaders.ThreadMethodResult;
import twg2.jbcm.runtime.ClassLoaders.ThreadMethodResultFuture;
import twg2.jbcm.testParser.CompileJava;

/** Bytecode class and method renaming tests
 * @author TeamworkGuy2
 * @since 2022-07-31
 */
public class BcRenameTest {

	@ExtendWith({BeforeAllCompileTestData.class})
	@Test
	public void renameClassTest() throws Exception {
		String className = "examples.ClassRename1";
		ThreadMethodResult<Object> res = compileAndRunClass(className, "System.out.println(\" >> examples.ClassRename1 run!\");");
		Assert.assertEquals(className, res.method.getDeclaringClass().getName());
	}


	public static <T extends Object> ThreadMethodResult<T> compileAndRunClass(String className, String code) throws IOException, URISyntaxException, InterruptedException, ExecutionException, ClassNotFoundException {
		ClassFile classFile = CompileJava.compileToClass(className, Collections.emptyList(), code);
		ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream(1024);
		classFile.writeData(new DataOutputStream(byteArrayOut));
		byte[] bc = byteArrayOut.toByteArray();

		if(CompileSource.debug) {
			classFile.print(System.out);
		}

		URI rootLocation = BcRenameTest.class.getProtectionDomain().getCodeSource().getLocation().toURI();
		Path workingRoot = Paths.get(rootLocation).getParent().resolve("res/tmp/");
		Path other = Paths.get(className.replace('.', '/') + ".class");
		Path classBinaryDir = workingRoot.resolve("bin");
		Path classBinaryPath = classBinaryDir.resolve(other);
		Files.createDirectories(classBinaryPath.getParent());
		Files.write(classBinaryPath, bc, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
		URL classpathRoot = classBinaryDir.toUri().toURL();

		//MemoryClassLoader cl = new MemoryClassLoader();
		//List<Class<?>> classes = ResourceToClass.loadClasses(new InputStream[] { new ByteArrayInputStream(bc) }, new String[] { className }, cl);

		// load the class and call the method
		ThreadMethodResultFuture<Object> res = ClassLoaders.callClassMethod(classpathRoot, className, "run", null, new Object[0]);

		// wait for the result
		try {
			res.thread.join();
		} catch (InterruptedException e) {
			System.err.println("Error waiting for thread: " + e.getLocalizedMessage());
		}
		return new ThreadMethodResult<T>(res.thread, res.method.get(), (T)res.result.get());
	}
}
