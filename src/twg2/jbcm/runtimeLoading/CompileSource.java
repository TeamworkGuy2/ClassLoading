package twg2.jbcm.runtimeLoading;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.Charset;

import org.eclipse.jdt.core.compiler.CompilationProgress;

import twg2.jbcm.main.RuntimeReloadMain;

/** Compile a Java source file
 * @author TeamworkGuy2
 * @since 2013-10-7
 */
public class CompileSource {

	public CompileSource() {
	}


	// http://help.eclipse.org/indigo/index.jsp?topic=%2Forg.eclipse.jdt.doc.user%2Ftasks%2Ftask-using_batch_compiler.htm
	public Class<?>[] compile(URL path, URL sourceClassPath, URL destinationClassPath, URL[] sources) throws ClassNotFoundException {

		String pathString = new File(path.getPath()).getPath();
		String sourcePathString = new File(sourceClassPath.getPath()).getPath();
		String destinationPathString = new File(destinationClassPath.getPath()).getPath();
		String[] clArgs = new String[] {
			"-1.5",
			//"-classpath",
			//"rt.jar",
			//pathString,
			"-sourcepath",
			sourcePathString,
			"-d",
			destinationPathString,
			sourcePathString+"\\compile\\Hello.java",
		};
		String clArg = "-1.6 -classpath rt.jar;" + pathString + " -sourcepath " + sourcePathString +
				" -d " + destinationPathString + " " + sourcePathString+"\\compile\\Hello.java";
		System.out.print("Compiler parameters: ");
		for(String arg : clArgs) {
			System.out.print(arg + ", ");
		}
		System.out.println();
		System.out.println("Compiler param: " + clArg);
		Charset charset = Charset.forName("UTF-8");
		CompilationProgress compileProgress = new CompilationProgressImpl();
		ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
		PrintWriter outWriter = new PrintWriter(new OutputStreamWriter(out, charset));
		PrintWriter errWriter = new PrintWriter(System.err);
		org.eclipse.jdt.core.compiler.batch.BatchCompiler.compile(clArg, outWriter, errWriter, compileProgress);

		Class<?>[] classes = new Class<?>[sources.length];
		RuntimeReloadMain reload = new RuntimeReloadMain();
		reload.loadRun("res/compile/class_files", "compile.Hello", "run", (String[])null);
		return classes;
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

}
