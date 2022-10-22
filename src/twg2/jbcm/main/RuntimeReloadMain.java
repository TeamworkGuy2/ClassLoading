package twg2.jbcm.main;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import twg2.jbcm.runtime.ClassLoaders;
import twg2.jbcm.runtime.ClassLoaders.ThreadMethodResultFuture;
import twg2.jbcm.runtime.FileUtility;

/** Test how and whether classes can be reloaded during runtime
 * @author TeamworkGuy2
 * @since 2013-7-20
 */
public class RuntimeReloadMain {
	private Class<?> previousReload;
	private Class<?> currentReload;


	/** Load the specified classes over and over for testing purposes.
	 * Trying to test if an application running an instance of this class can reload a newer version of this class
	 * and continue running normally without needing to restart
	 * @param customClasspath the custom class path to load classes from
	 * @param customPath2 the second custom class path to copy overwrite files from
	 * @param args the string array arguments to pass to the method being called
	 */
	public void loadLoopUserInput(String customClasspath, String customPath2, String[] args) {
		String applicationPath = ClassLoaders.getApplicationPathString();

		String path = new File(applicationPath).getParent() + File.separatorChar + customClasspath;
		String path2 = new File(applicationPath).getParent() + File.separatorChar + customPath2;
		System.out.println("Classpath=" + path + "\nCopyFromPath=" + path2);
		char choice = 0;
		boolean doLoop = true;

		@SuppressWarnings("resource")
		Scanner input = new Scanner(System.in);

		// Loop to process class loading choices
		while(doLoop) {
			System.out.print("Load next class (a=Reload.main(), b=Test.main(), c=overwrite-call-RuntimeReloadMain.main(), i=compare, z=exit): ");
			ThreadMethodResultFuture<Object> t = null;
			try {
				choice = input.nextLine().charAt(0);
			} catch(NoSuchElementException e) {
				e.printStackTrace();
			}

			if(choice == 'a') {
				System.out.println("Choice: " + choice);
				t = ClassLoaders.callClassMethod(path, "classLoading.Reload", "main", null, (Object)args);
				try {
					t.thread.join();
				} catch (InterruptedException e) {
					System.err.println("Error waiting for thread: " + e.getLocalizedMessage());
				}
			}
			else if(choice == 'b') {
				System.out.println("Choice: " + choice);
				t = ClassLoaders.callClassMethod(path, "classLoading.base.Test", "main", null, (Object)args);
				try {
					t.thread.join();
				} catch (InterruptedException e) {
					System.err.println("Error waiting for thread: " + e.getLocalizedMessage());
				}
			}
			else if(choice == 'c') {
				System.out.println("Choice loader: " + choice);
				FileUtility.overwriteClass(path, "classLoading.Settings", path2, "classLoading.Settings");
				t = ClassLoaders.callClassMethod(path, "classLoading.Reload", "main", null, new Object[] {args});
				try {
					t.thread.join();
				} catch (InterruptedException e) {
					System.err.println("Error waiting for thread: " + e.getLocalizedMessage());
				}
			}
			else if(choice == 'i') {
				if(previousReload != null && currentReload != null) {
					System.out.println("Previous.isAssignableFrom(Current): " + previousReload.isAssignableFrom(currentReload) +
							", " + previousReload + "@" + previousReload.hashCode() +
							", " + currentReload + "@" + currentReload.hashCode());
				}
				previousReload = currentReload;
			}
			else if(choice == 'z') {
				break;
			}
		}
	}


	/** Load the specified class and run the specified method on an instance of that class.
	 * Trying to test if an application running an instance of this class can reload a newer version of this class
	 * and continue running normally without needing to restart
	 * @param args the arguments to pass to the method being called
	 */
	public static <T extends Object> ThreadMethodResultFuture<T> loadRun(String customClassPath, String className, String methodName, Object[] args) {
		String applicationPath = ClassLoaders.getApplicationPathString();
		String path = new File(applicationPath).getParent() + File.separatorChar + customClassPath;

		return ClassLoaders.callClassMethod(path, className, methodName, null, args);
	}


	public static void main(String[] args) throws IOException {
		// How to properly retrieve the based folder in which the current class is running
		// This folder is the base folder, for example: /home/me/project/code/MyClass where
		// MyClass' package declaration is "packet project.code;" would return a domain of "/home/me"
		System.out.println("java.class.path: " + System.getProperty("java.class.path"));
		System.out.println("Domain: " + ClassLoaders.getApplicationPathURL());
		RuntimeReloadMain load = new RuntimeReloadMain();
		load.loadLoopUserInput("res/destination", "res/second_runtime", args);
	}

}
