package twg2.jbcm.runtimeLoading;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;

import org.eclipse.jdt.core.compiler.CompilationProgress;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.main.RuntimeReloadMain;

/** Compile a Java source file
 * @author TeamworkGuy2
 * @since 2013-10-7
 */
public class CompileSource {
	public static String defaultCompileClassName = "CompilerTemp";

	private CompileSource() {
	}


	/** Compile a Java source code using jdk1.8.0_25.
	 * TODO: make JDK path/version a variable in future.
	 * @param useEclipse whether to compile using {@link org.eclipse.jdt.core.compiler.batch.BatchCompiler}
	 * @param entrySourceFile the source file to compile
	 * @param sourceFilesPath the directory containing all source files required for compiling {@code entrySourceFile}
	 * @param classPaths 'classpath' semicolon separated list of for JDK/JRE and dependency libraries
	 * @param destinationPath the directory to write compiled {@code .class} files to
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static boolean compile(boolean useEclipse, File entrySourceFile, File sourceFilesPath, String classPaths, File destinationPath) throws ClassNotFoundException, IOException {
		Charset charset = Charset.forName("UTF-8");
		String sourceFilesPathStr = sourceFilesPath.getAbsoluteFile().getPath();
		String dstPathStr = destinationPath.getAbsoluteFile().getPath();
		String clArg = (useEclipse ? "-1.6" : "-source 1.8 -target 1.8") +
				" -classpath rt.jar;" + classPaths +
				" -sourcepath " + sourceFilesPathStr +
				" -d " + dstPathStr +
				" " + entrySourceFile.getAbsolutePath();

		System.out.println("Compiler args: " + clArg);

		if(useEclipse) {
			// http://help.eclipse.org/indigo/index.jsp?topic=%2Forg.eclipse.jdt.doc.user%2Ftasks%2Ftask-using_batch_compiler.htm
			CompilationProgress compileProgress = new CompilationProgressImpl();
			ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
			PrintWriter outWriter = new PrintWriter(new OutputStreamWriter(out, charset));
			PrintWriter errWriter = new PrintWriter(System.err);
			boolean res = org.eclipse.jdt.core.compiler.batch.BatchCompiler.compile(clArg, outWriter, errWriter, compileProgress);

			System.out.println(out.toString(charset));
			return res;
		}
		else {
			int res = runCommand(Runtime.getRuntime(), "\"C:/Program Files/Java/jdk1.8.0_25/bin/javac\" " + clArg, System.out, System.err);
			return res == 0;
		}
	}



	/** Compile a Java source string to bytecode
	 * @param javaSrc the Java source code string
	 * @return the compiled byte code loaded as a byte[] (which can be loaded to {@link ClassFile} via {@link ClassFile#readData(java.io.DataInput)}
	 * @throws MalformedURLException
	 * @throws ClassNotFoundException
	 */
	public static byte[] compileSourceToBytecode(String javaSrc) {
		File tmpSrcFile = null;
		File tmpDstFile = null;
		try {
			new File("res/tmp/src").mkdir();
			new File("res/tmp/bin").mkdir();

			tmpSrcFile = new File("res/tmp/src/" + defaultCompileClassName + ".java");
			Files.write(tmpSrcFile.toPath(), javaSrc.getBytes("UTF-8"), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
			File sourceFilesPath = new File("res/tmp/src");
			String classPath = new File("res/tmp/src").getAbsolutePath();
			File destinationPath = new File("res/tmp/bin");

			boolean success = CompileSource.compile(false, tmpSrcFile, sourceFilesPath, classPath, destinationPath);
			if(!success) {
				throw new RuntimeException("Error compiling source");
			}

			tmpDstFile = new File("res/tmp/bin/" + defaultCompileClassName + ".class");
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


	/** Compile a Java source file and load it
	 * @param srcJavaFile
	 * @param dstClassFile
	 * @throws ClassNotFoundException
	 * @throws IOException 
	 */
	public static void compileSourceFileToClass(File srcJavaFile, File dstClassFile) throws ClassNotFoundException, IOException {
		File entryFile = new File("res/tmp/src/twg2/compileTest/Hello.java");
		File sourceFilesPath = new File("res/tmp/src");
		String classPath = new File("res/tmp/src/twg2/compileTest").getAbsolutePath();
		File destinationClassPath = new File("res/tmp/bin");

		CompileSource.compile(false, entryFile, sourceFilesPath, classPath, destinationClassPath);

		RuntimeReloadMain reload = new RuntimeReloadMain();
		reload.loadRun("res/tmp/bin", "twg2.compileTest.Hello", "run", (String[])null);

		//Runnable thing = (Runnable)classes[0].newInstance();
		//thing.run();
	}


	private static int runCommand(Runtime runtime, String command, OutputStream outStream, OutputStream errStream) throws IOException {
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
	private static int finishSync(Process process, ReadInputStream inputReader, ReadInputStream errorReader) {
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



	private static class CompilationProgressImpl extends CompilationProgress {
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
