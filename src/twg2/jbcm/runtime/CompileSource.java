package twg2.jbcm.runtime;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;

import org.eclipse.jdt.core.compiler.CompilationProgress;

import twg2.jbcm.classFormat.ClassFile;

/** Compile a Java source file
 * @author TeamworkGuy2
 * @since 2013-10-7
 */
public class CompileSource {
	/** Default class name for dynamically compiled snippets of code */
	public static String defaultCompileClassName = "CompilerTemp";
	/** Compiler configuration for Java 1.8 source and target */
	public static CompilerConfig JAVA_1_8_COMPILE = new CompilerConfig("\"C:/Program Files/Java/jdk1.8.0_25/bin/javac\"", "1.8", "1.8");
	/** Whether to print code by default when calling {@code compileCode()} without the last boolean parameter */
	public static boolean printCode = false;
	/** Whether to print additional debug diagnostic information */
	public static boolean debug = false;


	public static class CompilerConfig {
		public final String compilerPath;
		public final String sourceVersion;
		public final String targetCompileVersion;

		public CompilerConfig(String compilerPath, String sourceVersion, String targetCompileVersion) {
			this.compilerPath = compilerPath;
			this.sourceVersion = sourceVersion;
			this.targetCompileVersion = targetCompileVersion;
		}
	}


	private CompileSource() {
	}


	/** Compile a Java source code using jdk1.8.0_25.
	 * TODO: make JDK path/version a variable in future.
	 * @param useEclipse whether to compile using {@link org.eclipse.jdt.core.compiler.batch.BatchCompiler}
	 * @param entrySourceFile the source file to compile
	 * @param sourceDir the directory containing all source files required for compiling {@code entrySourceFile}
	 * @param classPaths 'classpath' semicolon separated list of JDK/JRE and dependency libraries
	 * @param destinationDir the directory to write compiled {@code .class} files to
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static boolean compile(CompilerConfig config, File entrySourceFile, File sourceDir, String classPaths, File destinationDir) throws ClassNotFoundException, IOException {
		String sourceFilesPathStr = sourceDir.getAbsoluteFile().getPath();
		String dstPathStr = destinationDir.getAbsoluteFile().getPath();
		String clArg = "-source " + config.sourceVersion + " -target " + config.targetCompileVersion +
				" -classpath rt.jar;" + classPaths +
				" -sourcepath " + sourceFilesPathStr +
				" -d " + dstPathStr +
				" " + (entrySourceFile != null ? entrySourceFile.getAbsolutePath() : "");

		if(debug) {
			System.out.println("Compiler args: " + clArg);
		}

		int res = runCommand(Runtime.getRuntime(), config.compilerPath + " " + clArg, System.out, System.err);
		return res == 0;
	}


	public static boolean compileUsingEclipseEcj(File entrySourceFile, File sourceDir, String classPaths, File destinationDir) throws ClassNotFoundException, IOException {
		String sourceFilesPathStr = sourceDir.getAbsoluteFile().getPath();
		String dstPathStr = destinationDir.getAbsoluteFile().getPath();
		String clArg = "-1.6" +
				" -classpath rt.jar;" + classPaths +
				" -sourcepath " + sourceFilesPathStr +
				" -d " + dstPathStr +
				" " + (entrySourceFile != null ? entrySourceFile.getAbsolutePath() : "");

		if(debug) {
			System.out.println("Compiler args: " + clArg);
		}

		// http://help.eclipse.org/indigo/index.jsp?topic=%2Forg.eclipse.jdt.doc.user%2Ftasks%2Ftask-using_batch_compiler.htm
		Charset charset = Charset.forName("UTF-8");
		CompilationProgress compileProgress = new CompilationProgressImpl();
		ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
		PrintWriter outWriter = new PrintWriter(new OutputStreamWriter(out, charset));
		PrintWriter errWriter = new PrintWriter(System.err);
		boolean res = org.eclipse.jdt.core.compiler.batch.BatchCompiler.compile(clArg, outWriter, errWriter, compileProgress);

		System.out.println(out.toString(charset));
		return res;
	}


	/** Compile a Java source string to bytecode
	 * @param sourceRootDir the root folder containing the source files
	 * @param destinationRootDir the root folder to write compiled class files into
	 * @param packageDir the relative package directory within the source directory the find the file and
	 * within the destination directory to write the compiled class file. Expected format for.
	 * Example: when compiling {@code com.app.utils.MathUtils}, the {@code packageDir} should be {@code com/app/utils} 
	 * @param className the simple class name of the {@code javaSrc}, does not include the package name nor file extension.
	 * Example: when compiling {@code com.app.utils.MathUtils}, the {@code className} should be {@code MathUtils}
	 * @param javaSrc the Java source code string
	 * @return the compiled byte code loaded as a byte[] (which can be loaded to {@link ClassFile} via {@link ClassFile#readData(java.io.DataInput)}
	 * @throws RuntimeException wrapping possible {@link IOException} or {@link ClassNotFoundException}
	 */
	public static byte[] compileSourceToBytecode(String sourceRootDir, String destinationRootDir, String packageDir, String className, String javaSrc) {
		File tmpSrcFile = null;
		File tmpDstFile = null;
		try {
			File srcDir = new File(sourceRootDir);
			File dstDir = new File(destinationRootDir);

			new File(srcDir, packageDir).mkdir();
			dstDir.mkdir();

			tmpSrcFile = new File(sourceRootDir + "/" + (packageDir != null && packageDir.length() > 0 ? packageDir + "/" : "") + className + ".java");
			Files.write(tmpSrcFile.toPath(), javaSrc.getBytes("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
			String classPath = srcDir.getAbsolutePath();

			boolean success = compile(JAVA_1_8_COMPILE, tmpSrcFile, srcDir, classPath, dstDir);
			if(!success) {
				throw new RuntimeException("Error compiling source");
			}

			tmpDstFile = new File(destinationRootDir + "/" + (packageDir != null && packageDir.length() > 0 ? packageDir + "/" : "") + className + ".class");
			var bytecode = Files.readAllBytes(tmpDstFile.toPath());

			tmpSrcFile.delete();
			tmpDstFile.delete();

			return bytecode;
		} catch (IOException | ClassNotFoundException ioe) {
			if(tmpSrcFile != null) {
				try {
					tmpSrcFile.delete();
				} catch(Exception e) {
					// do nothing
				}
			}
			if(tmpDstFile != null) {
				try {
					tmpDstFile.delete();
				} catch(Exception e) {
					// do nothing
				}
			}
			throw new RuntimeException(ioe);
		}
	}


	protected static int runCommand(Runtime runtime, String command, OutputStream outStream, OutputStream errStream) throws IOException {
		Level level = Level.INFO;
		PrintStream log = System.err;

		Process proc = runtime.exec(command, null/*envp*/, null/*dir*/);

		var inputReader = new ReadInputStream(proc.getInputStream(), outStream, level, log);
		var errorReader = new ReadInputStream(proc.getErrorStream(), errStream, level, log);
		var inputReaderThread = new Thread(inputReader, "ReadInput");
		var errorReaderThread = new Thread(errorReader, "ReadError");
		inputReaderThread.start();
		errorReaderThread.start();

		return finishSync(proc, inputReader, errorReader);
	}


	/** Pause the current thread until the command finishes
	 * @param exeCmd the command to execute
	 * @return 0 normally indicates success, other values normally indicate failure
	 */
	protected static int finishSync(Process process, ReadInputStream inputReader, ReadInputStream errorReader) {
		int res = -1;
		try {
			res = process.waitFor();

			inputReader.stop();
			errorReader.stop();
			// threads stop when the readers are stopped
		} catch (InterruptedException e) {
			System.err.println("Error waiting for process to finish");
			e.printStackTrace();
		}

		return res;
	}



	protected static class CompilationProgressImpl extends CompilationProgress {
		//private boolean done;

		@Override
		public void worked(int arg0, int arg1) {
			;
		}

		@Override
		public void setTaskName(String arg0) {
			;
		}

		@Override
		public boolean isCanceled() {
			return false;
		}

		@Override
		public void done() {
			//done = true;
			System.out.println("DONE");
		}

		@Override
		public void begin(int arg0) {
			;
		}
	}



	public static class ReadInputStream implements Runnable {
		private InputStream in;
		private OutputStream out;
		private Level logLevel;
		private PrintStream log;
		private Object waitObj = new Object();
		private int readCount = 0;
		private volatile boolean keepReading = true;


		/**
		 * @param inputStream the input stream to read from
		 * @param outputStream the output stream to write the input
		 * stream's contents to, or null to discard the contents.
		 */
		public ReadInputStream(InputStream inputStream, OutputStream outputStream, Level logLevel, PrintStream log) {
			this.logLevel = logLevel;
			this.log = log;
			this.in = inputStream;
			this.out = outputStream;
		}


		public int getReadCount() {
			return readCount;
		}


		/** Resets this input stream reader
		 * @param inputStream
		 * @param outputStream
		 */
		public void resetWithNewStreams(InputStream inputStream, OutputStream outputStream) {
			stop();
			this.keepReading = true;
			this.in = inputStream;
			this.out = outputStream;
			synchronized(waitObj) {
				waitObj.notify();
			}
		}


		public void stop() {
			keepReading = false;
			if(this.in != null) {
				try {
					this.in.close();
					this.in = null;
				} catch (IOException e) {
					this.in = null;
					e.printStackTrace();
				}
			}
			synchronized(waitObj) {
				waitObj.notify();
			}
		}


		@Override
		public void run() {
			int size = 1024;
			byte[] buf = new byte[size];
			int waitCount = 0;
			final int maxWaitCount = 20;
			final int waitIntervalMs = 500;
			boolean throwWaitCountError = false;

			synchronized(waitObj) {
				while(keepReading) {
					int readLength = 0;
					boolean waitingForInNotNull = in == null;
					waitCount = 0;

					while(keepReading && waitCount < maxWaitCount) {
						// read from the input stream while keepReading is true or until an error occurs
						// (which is sometimes purposely generated by closing the stream)
						try {
							while(keepReading && in != null && (readLength = in.read(buf, 0, size)) != -1) {
								this.readCount += readLength;
								if(out != null) {
									out.write(buf, 0, readLength);
								}
							}
						} catch(IOException ioe) {
							in = null;
							if(logLevel.intValue() <= Level.SEVERE.intValue()) {
								log.append("[" + Level.SEVERE.getName() + "] " + this.getClass().getSimpleName() + ": Error reading input stream or writing output stream: " + ioe + "\n");
							}
						}

						// once the input stream read ends, wait for the wait object to be notified
						// the loop checking for the inputStream to become not-null in case of spurious wait/notify calls
						try {
							if(logLevel.intValue() <= Level.FINER.intValue()) {
								log.append("[" + Level.FINER.getName() + "] " + this.getClass().getSimpleName() + ": thread '" + Thread.currentThread().getName() + (waitingForInNotNull ? "' start waiting for input stream" : "' start waiting") + ", (byteCount=" + readLength + ", inputStream=" + in + ")" + "\n");
							}

							waitObj.wait(waitIntervalMs);

							if(logLevel.intValue() <= Level.FINER.intValue()) {
								log.append("[" + Level.FINER.getName() + "] " + this.getClass().getSimpleName() + ": thread '" + Thread.currentThread().getName() + (waitingForInNotNull ? "' done waiting for input stream" : "' done waiting") + ", (byteCount=" + readLength + ", inputStream=" + in + ")" + "\n");
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
							waitCount++;
						}
						// if the input stream was null, but isn't any more, exit the wait loop
						if(waitingForInNotNull && in == null) {
							break;
						}
					}
					// if the loop ended with too many wait error loops
					if(waitCount >= maxWaitCount) {
						throwWaitCountError = true;
						break;
					}
				}
			}

			if(throwWaitCountError) {
				throw new IllegalStateException("could not finish reading input stream, waited for more input " + waitCount + " times");
			}
		}

	}
}
