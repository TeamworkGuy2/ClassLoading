package twg2.jbcm.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.FileChannel;
import java.util.NoSuchElementException;
import java.util.Scanner;

/** Test how and whether classes can be reloaded during runtime
 * @author TeamworkGuy2
 * @since 2013-7-20
 */
public class RuntimeReloadMain implements Runnable {
	private static final String classPostfix = ".class";
	private static final String defaultMainClassPath = "res/destination";
	private static final String classPath2 = "res/second_runtime";
	private static URL appPath;
	private static String appPathStr;
	private String className;
	private String methodName;
	private Object classInstance;
	private Object[] arguments;
	private Class<?> previousReload;
	private Class<?> currentReload;


	/** Load the specified classes over and over for testing purposes.
	 * Trying to test if an application running an instance of this class can reload a newer version of this class
	 * and continue running normally without needing to restart
	 * @param customClassPath the custom class path to load classes from
	 * @param customPath2 the second custom class path to copy overwrite files from
	 * @param args the string array arguments to pass to the method being called
	 */
	public void loadLoopUserInput(String customClassPath, String customPath2, String[] args) {
		String applicationPath = getApplicationPathString();

		String path = new File(applicationPath).getParent() + File.separatorChar + customClassPath;
		String path2 = new File(applicationPath).getParent() + File.separatorChar + customPath2;
		System.out.println("Classpath=" + path + "\nCopyFromPath=" + path2);
		byte choice = 0;
		boolean doLoop = true;

		@SuppressWarnings("resource")
		Scanner input = new Scanner(System.in);

		// Loop to process class loading choices
		while(doLoop) {
			System.out.print("\nLoad next class (a=Reload.main(), b=Test.main(), c=overwrite-call-RuntimeReloadMain.main(), i=compare, z=exit): ");
			Thread t = null;
			try {
				choice = (byte)input.nextLine().charAt(0);
			} catch(NoSuchElementException e) {
				e.printStackTrace();
			}

			if(choice == 'a') {
				System.out.println("Choice: " + choice);
				t= callClassMethod(path, "classLoading.Reload", "main", null, (Object)args);
				try {
					t.join();
				} catch (InterruptedException e) {
					System.err.println("Error waiting for thread: " + e.getLocalizedMessage());
				}
			}
			else if(choice == 'b') {
				System.out.println("Choice: " + choice);
				t = callClassMethod(path, "classLoading.base.Test", "main", null, (Object)args);
				try {
					t.join();
				} catch (InterruptedException e) {
					System.err.println("Error waiting for thread: " + e.getLocalizedMessage());
				}
			}
			else if(choice == 'c') {
				System.out.println("Choice loader: " + choice);
				overwriteClass(path, "classLoading.Settings", path2, "classLoading.Settings", "main", null, new Object[] {args});
				t = callClassMethod(path, "classLoading.Reload", "main", null, new Object[] {args});
				try {
					t.join();
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



	/** Load the specified classes over and over for testing purposes.
	 * Trying to test if an application running an instance of this class can reload a newer version of this class
	 * and continue running normally without needing to restart
	 * @param args - the string array arguments to pass to the method being called
	 */
	public void loadRun(String customClassPath, String className, String methodName, String[] args) {
		String applicationPath = getApplicationPathString();

		String mainClassPath = customClassPath;
		String path = new File(applicationPath).getParent() + File.separatorChar + mainClassPath;
		System.out.println("Classpath=" + applicationPath + "\nAlternate path=" + path);

		System.out.print("Load class: ");
		callClassMethod(path, className, methodName, null, (Object)args);
	}


	/** Call the method name in the class name saved in this object's member variables
	 */
	@Override
	public void run() {
		callMethod(className, classInstance, methodName, arguments);
	}


	/** Call a method reflexively
	 * @param classNameToLoad the name of the class to containing the method
	 * @param instance the object instance to use, or null if the method is
	 * static or a new instance should be created
	 * @param methodNameToCall the name of the method to call
	 * @param args the parameters to pass to the method
	 */
	public void callMethod(String classNameToLoad, Object instance, String methodNameToCall, Object[] args) {
		Class<?> clazz = null;
		// Create the list of parameter class types from the list of parameters
		Class<?>[] argumentTypes = null;
		if(args != null && !(args.length == 1 && args[0] == null)) {
			int argCount = args.length;
			argumentTypes = new Class<?>[argCount];
			for(int i = 0; i < argCount; i++) {
				argumentTypes[i] = args[i].getClass();
			}
		}
		else {
			args = null;
		}
		// Get this thread's class loader and load the class, then get the method in the class with the specified name
		Method method = null;
		int modifiers = -1;
		try {
			clazz = Thread.currentThread().getContextClassLoader().loadClass(classNameToLoad);
			method = clazz.getMethod(methodNameToCall, argumentTypes);
			modifiers = method.getModifiers();
		} catch (SecurityException e1) {
			System.err.println("Security exception, class " + classNameToLoad + " is not public, caused by " + e1.getCause());
		} catch (NoSuchMethodException e1) {
			System.err.println("No such method exception, method: " + methodNameToCall + ", caused by " + e1.getCause());
		} catch (ClassNotFoundException e1) {
			System.err.println("Error, could not find class name=" + classNameToLoad + ", caused by " + e1.getCause());
		}
		// Save the class instance for comparison to other loaded classes later
		currentReload = clazz;
		// If the method is not static and the instance is null, create an instance of the class to use to call the method
		try {
			if(instance == null && !Modifier.isStatic(modifiers)) {
				instance = clazz.newInstance();
			}
		} catch(Exception e) {
			System.err.println("Error creating new instance of class: " + classNameToLoad + ", " + e.getLocalizedMessage() + ", caused by " + e.getCause());
		}
		// Call the method
		try {
			method.invoke(instance, args);
		} catch (IllegalAccessException e) {
			System.err.println("Illegal Access Exception: cannot invoke \'" + clazz.getSimpleName() + "." +
					methodNameToCall + "\' method, caused by " + e.getCause());
			return;
		} catch (InvocationTargetException e) {
			System.err.println("Exception throw by " + classNameToLoad + "." + methodNameToCall +
					"() while calling it, caused by " + e.getCause());
		} finally {
			method = null;
		}
		return;
	}


	/** Dynamically load and call a specified class' method in a new thread.
	 * Designed to start or restart a particular dynamic program module that
	 * is well suited to run in its own thread.
	 * @param path the folder path or jar file path that the class is located in.
	 * This path should end with a '/' forward slash if it is a folder, otherwise
	 * it is assumed to be a jar file.
	 * @param classNameToLoad the fully qualifying Java class name separated by
	 * '.' periods
	 * @param methodNameToCall the name of the method to call without parentheses
	 * or arguments.
	 * @param instance an optional instance of the class to use when calling the method,
	 * if the method being called is not static and this instance parameter is null,
	 * than a new instance of the class is created using {@link Class#newInstance()}.
	 * @param args the list of parameters to pass to the method call
	 * @return true if the method was called, false if there was an error
	 */
	public Thread callClassMethod(String path, String classNameToLoad, String methodNameToCall, Object instance, Object... args) {
		this.className = classNameToLoad;
		this.methodName = methodNameToCall;
		this.classInstance = instance;
		this.arguments = args;
		Thread t = new Thread(this, "Run " + classNameToLoad + "." + methodNameToCall);
		URLClassLoader cl = createClassLoader(path);

		try {
			t.setContextClassLoader(cl);
		} catch(SecurityException se) {
			System.err.println("Error, security error setting new thread class loader while trying to run class: " + classNameToLoad);
		}
		t.start();
		return t;
	}


	/** The new class file is copied and written over the original class file. Designed to start
	 * or restart a particular dynamic program module that is well suited to run in its own thread.<br/>
	 * <br/>
	 * Unfortunately, the trick to get this to work requires that both class files be located
	 * in the same named relative class path with unique base paths, for example, to load a
	 * new version of the class "myProject.Original" the new class must also be contained in
	 * a path ending with "myProject.Original" but must be located in a folder outside of the
	 * current application's classpath. The original class might be located in
	 * "/home/name/project/myProject/Original.class" and the new class might be
	 * located in "/home/name/projectAlt/myProject/Original.class".<br/>
	 * <br/>
	 * @param originalPath the folder path (jar file paths are not yet supported) that the original
	 * class to be overwritten is located in.
	 * @param originalClassNameToLoad the fully qualifying Java name used of the original
	 * class to overwrite, the path separated by '.' periods.
	 * @param newPath the folder path (jar file paths are not yet supported) that the new class
	 * to load and overwrite the original class with is located in.
	 * @param newClassNameToLoad the fully qualifying Java name used for the new class to
	 * overwrite the old class, the path separated by '.' periods.
	 * @param methodNameToCall the name of the method to call
	 * @param instance an optional instance of the class to use when calling the method, if the
	 * method being called is not static and this instance parameter is null, than a new instance
	 * of the class is created using {@link Class#newInstance()}.
	 * @param args the list of parameters to pass to the method call
	 * @return true if the method was called, false if there was an error
	 */
	public boolean overwriteClass(String originalPath, String originalClassNameToLoad, String newPath, String newClassNameToLoad, String methodNameToCall, Object instance, Object[] args) {
		try {
			// The sub path of the class file with the class' name
			String originalClassNamePath = originalClassNameToLoad.replace('.', File.separatorChar) + classPostfix;
			String newClassNamePath = newClassNameToLoad.replace('.', File.separatorChar) + classPostfix;
			// Create the full original path using the original path and class name path
			String fullOriginalPath = originalPath + ((originalPath.endsWith("\\") || originalPath.endsWith("/")) ? originalClassNamePath : File.separatorChar + originalClassNamePath);
			// Create the full new path using the new path and class name path
			String fullNewPath = newPath + ((newPath.endsWith("\\") || newPath.endsWith("/")) ? newClassNamePath : File.separatorChar + newClassNamePath);
			// Overwrite the original file with the new file
			overwriteFile(fullNewPath, fullOriginalPath);
		} catch(IOException ioe) {
			System.err.println("Error, could not overwrite the old file, caused by " + ioe.getCause());
			return false;
		}
		return true;
	}


	/** Create a class loader using the specified file path
	 * @param path the file path to set as the classloader's path
	 * @return the classloader with the specified classpath
	 */
	private static URLClassLoader createClassLoader(String path) {
		URL url = null;
		try {
			url = toURL(path);
		} catch (IOException e) {
			System.err.println("Error creating class loader URL");
			e.printStackTrace();
		}
		return new URLClassLoader(new URL[] {url}, Thread.currentThread().getContextClassLoader());
	}


	/** Convert a local file path to a {@link URL}
	 * @param path the local file path to parse into a URL
	 * @return the URL corresponding to the specified file/folder path
	 * @throws IOException if there is an error creating the URL
	 */
	private static URL toURL(String path) throws IOException {
		URL url = null;
		// Convert the path to a URL
		try {
			url = new URL(path);
		} catch(Exception e) {
			// If the path is not a valid URL convert it to a file then to a URL
			File file = new File(path).getCanonicalFile();
			url = file.toURI().toURL();
		}
		return url;
	}


	/** Overwrite the destination file with the source file
	 * @param newFilePath the source file to use
	 * @param oldFilePath the destination file to overwrite with the contents of the source file
	 * @throws IOException if there is an error opening either of the files or copying the source file's
	 * contents into the destination file
	 */
	private static void overwriteFile(String newFilePath, String oldFilePath) throws IOException {
		FileChannel src = null;
		FileChannel dst = null;
		try {
			File oldFile = new File(oldFilePath).getCanonicalFile();
			File newFile = new File(newFilePath).getCanonicalFile();
			System.out.println("Overwriting: " + oldFile.getAbsolutePath() + ", with: " + newFile.getAbsolutePath());
			if(!oldFile.isFile() || !newFile.isFile()) {
				throw new IOException("Error, one of these paths is not a valid file (" + oldFilePath + ", " + newFilePath + ")");
			}

			@SuppressWarnings("resource")
			FileInputStream srcStream = new FileInputStream(newFile);
			src = srcStream.getChannel();

			@SuppressWarnings("resource")
			FileOutputStream dstStream = new FileOutputStream(oldFile);
			dst = dstStream.getChannel();

			long transferCount = 0;
			long size = src.size();
			do {
				transferCount += dst.transferFrom(src, transferCount, size-transferCount);
			} while(transferCount < size);
		} catch (IOException e) {
			throw e;
		}
		finally {
			try {
				if(src != null) {
					src.close();
				}
				if(dst != null) {
					dst.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	public static URL getApplicationPathURL() {
		if(appPath == null) {
			appPath = RuntimeReloadMain.class.getProtectionDomain().getCodeSource().getLocation();
			// more sure way
			/*
			java.security.AccessController.doPrivileged(new java.security.PrivilegedAction<URL>() {
				public URL run() {
					return (appPath = RuntimeReloadMain.class.getProtectionDomain().getCodeSource().getLocation());
				}
			});
			*/
		}
		return appPath;
	}


	public static String getApplicationPathString() {
		if(appPathStr == null) {
			appPathStr = getApplicationPathURL().getPath();
		}
		return appPathStr;
	}


	public static void main(String[] args) throws IOException {
		// How to properly retrieve the based folder in which the current class is running
		// This folder is the base folder, for example: /home/me/project/code/MyClass where
		// MyClass' package declaration is "packet project.code;" would return a domain of "/home/me"
		System.out.println("java.class.path" + ": " + System.getProperty("java.class.path"));
		System.out.println("Domain: " + getApplicationPathURL());
		System.out.println();

		RuntimeReloadMain load = new RuntimeReloadMain();

		load.loadLoopUserInput(defaultMainClassPath, classPath2, args);
	}

}
