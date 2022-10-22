package twg2.jbcm.test;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.junit.Assert;
import org.junit.Test;

import twg2.jbcm.main.RuntimeReloadMain;
import twg2.jbcm.runtime.CompileSource;

/**
 * @author TeamworkGuy2
 * @since 2022-07-31
 */
public class RuntimeReloadTest {


	/** Compile a Java source file and load it
	 * @throws ClassNotFoundException
	 * @throws IOException 
	 * @throws InterruptedException 
	 * @throws ExecutionException 
	 */
	@Test
	public void compileSourceFileToClass() throws ClassNotFoundException, IOException, InterruptedException, ExecutionException {
		File entryFile = new File("examples/ExampleMain.java");
		File projectRoot = new File("examples");
		String classPath = new File("examples").getAbsolutePath();
		File destinationDir = new File("res/tmp/bin");

		CompileSource.compile(CompileSource.JAVA_1_8_COMPILE, entryFile, projectRoot, classPath, destinationDir);

		var res = RuntimeReloadMain.loadRun("res/tmp/bin", "examples.ExampleMain", "run", (String[])null);

		//Runnable thing = (Runnable)classes[0].newInstance();
		//thing.run();

		res.thread.join(1000);

		Assert.assertNotNull(res.method.get()); // success!
	}

}
