package twg2.jbcm.test;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;

import twg2.jbcm.runtime.CompileSource;

/**
 * @author TeamworkGuy2
 * @since 2022-09-05
 */
public class BeforeAllCompileTestData implements Extension, BeforeAllCallback, ExtensionContext.Store.CloseableResource {
	/** Gate keeper to prevent multiple Threads within the same routine */
	private static final Lock lock = new ReentrantLock();
	/** volatile boolean to tell other threads, when unblocked, whether they should try attempt start-up.  Alternatively, could use AtomicBoolean. */
	private static volatile boolean started = false;

	@Override
	public void beforeAll(final ExtensionContext context) throws Exception {
		// for parallel JUnit tests, lock the access
		lock.lock();
		try {
			if (!started) {
				started = true;
				// Register a callback hook when the root test context is shut down
				context.getRoot().getStore(ExtensionContext.Namespace.GLOBAL).put(this.getClass().getCanonicalName(), this);

				compileJava("res/source", "res/destination");
			}
		}
		finally {
			lock.unlock();
		}
	}

	@Override
	public void close() {
		// "after all tests" logic goes here
	}


	private void compileJava(String sourceDir, String destinationDir) throws ClassNotFoundException, IOException {
		File tmpFile = new File("res/tmp/src/CompilerTemp.java");
		File sourceDirFile = new File(sourceDir);
		String classPath = sourceDirFile.getAbsolutePath();
		File destinationDirFile = new File(destinationDir);

		try {
			CompileSource.compile(CompileSource.JAVA_1_8_COMPILE, tmpFile, sourceDirFile, classPath, destinationDirFile);
		} catch(Exception ex) {
			throw ex;
		}
	}
}