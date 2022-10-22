package twg2.jbcm.runtime;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.AbstractMap;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/** Dynamically reloading a class a runtime has proven tricky.
 * This class tries to provide patterns to work around that.
 * If a class has already been loaded then it CANNOT be reloaded without
 * some form of class loading trickery that I have not yet discovered.
 * Instead, spin up a new thread with a new class loader that can reload the
 * class.
 * <br>
 * I've also been unable to load a class from byte-code in-memory
 * so we have to write the file to disk before reloading it.
 * <br>
 * See {@link MemoryClassLoader} for my in-memory class loading attempt.
 * @author TeamworkGuy2
 * @since 2022-09-05
 */
public class ClassLoaders {
	private static URL appPath;
	private static String appPathStr;


	public static class ThreadMethodResultFuture<T extends Object> {
		public final Thread thread;
		public final Future<Method> method;
		public final Future<T> result;

		public ThreadMethodResultFuture(Thread thread, Future<Method> method, Future<T> result) {
			this.thread = thread;
			this.method = method;
			this.result = result;
		}
	}


	public static class ThreadMethodResult<T extends Object> {
		public final Thread thread;
		public final Method method;
		public final T result;

		public ThreadMethodResult(Thread thread, Method method, T result) {
			this.thread = thread;
			this.method = method;
			this.result = result;
		}
	}


	/** Dynamically load and call a specified class' method in a new thread.
	 * Designed to start or restart a particular dynamic program module that
	 * is well suited to run in its own thread.
	 * @param path the folder path or jar file path that the class is located in.
	 * This path should end with a '/' forward slash if it is a folder, otherwise
	 * it is assumed to be a jar file
	 * @param className the fully qualifying Java class name separated by '.' periods
	 * @param methodName the name of the method to call without parentheses or arguments
	 * @param classInst an optional instance of the class to use when calling the method,
	 * if the method being called is not static and this instance parameter is null,
	 * than a new instance of the class is created using {@link Class#newInstance()}
	 * @param args the list of parameters to pass to the method call
	 * @return true if the method was called, false if there was an error
	 */
	public static <T extends Object> ThreadMethodResultFuture<T> callClassMethod(String path, String className, String methodName, Object classInst, Object... args) {
		return callClassMethod(toURLClean(path), className, methodName, classInst, args);
	}


	/** Dynamically load and call a specified class' method in a new thread.
	 * Designed to start or restart a particular dynamic program module that
	 * is well suited to run in its own thread.
	 * @param path the folder path or jar file path that the class is located in.
	 * @param className the fully qualifying Java class name separated by '.' periods
	 * @param methodName the name of the method to call without parentheses or arguments
	 * @param classInst an optional instance of the class to use when calling the method,
	 * if the method being called is not static and this instance parameter is null,
	 * than a new instance of the class is created using {@link Class#newInstance()}
	 * @param args the list of parameters to pass to the method call
	 * @return true if the method was called, false if there was an error
	 */
	public static <T extends Object> ThreadMethodResultFuture<T> callClassMethod(URL path, String className, String methodName, Object classInst, Object... args) {
		final CompletableFuture<T> resultFuture = new CompletableFuture<>();
		FutureTask<Method> task = new FutureTask<>(() -> {
			@SuppressWarnings("unchecked")
			var res = (Entry<Method, T>)callMethod(className, classInst, methodName, args);
			resultFuture.complete(res.getValue());
			return res.getKey();
		});

		Thread t = new Thread(task, "Run " + className + "." + methodName);

		URLClassLoader cl = createClassLoader(path);

		try {
			t.setContextClassLoader(cl);
		} catch(SecurityException se) {
			System.err.println("Error, security error setting new thread classloader: " + className);
		}
		t.start();
		return new ThreadMethodResultFuture<T>(t, task, resultFuture);
	}


	/** Loads a class and calls a method on it via reflection using the current thread's
	 * context class loader. Catches exceptions and prints appropriate error messages.
	 * @param className the name of the class to containing the method
	 * @param instance the object instance to use, or null if the method is
	 * static or a new instance should be created
	 * @param methodName the name of the method to call
	 * @param args the parameters to pass to the method
	 */
	public static Entry<Method, Object> callMethod(String className, Object instance, String methodName, Object[] args) {
		Method method = loadClassAndFindMethod(className, methodName, args);
		Class<?> clazz = method.getDeclaringClass();

		// If the method is not static and the instance is null, create an instance of the class to use to call the method
		int modifiers = method.getModifiers();
		try {
			if(instance == null && !Modifier.isStatic(modifiers)) {
				@SuppressWarnings("deprecation")
				Object obj = clazz.newInstance();
				instance = obj;
			}
		} catch(Exception e) {
			System.err.println("Error creating new instance of class: " + className + ", " + e.getLocalizedMessage() + ", caused by " + e);
		}

		// Call the method
		Object result = null;
		try {
			result = method.invoke(instance, args);
		} catch (IllegalAccessException e) {
			System.err.println("Illegal Access Exception: cannot invoke \'" + clazz.getSimpleName() + "." +
					methodName + "\' method, caused by " + e.getCause());
		} catch (InvocationTargetException e) {
			System.err.println("Exception throw by " + className + "." + methodName +
					"() while calling it, caused by " + e.getCause());
		}
		return new AbstractMap.SimpleImmutableEntry<>(method, result);
	}


	/** Loads a class and find a method by name and arguments via reflection using the
	 * current thread's context class loader.
	 * Catches exceptions and prints appropriate error messages.
	 * @param className the name of the class to containing the method
	 * @param methodName the name of the method to find
	 * @param args the parameters to pass to the method
	 */
	public static Method loadClassAndFindMethod(String className, String methodName, Object[] args) {
		// Infer the list of parameter types from the arguments, we may not find the
		// method if the argument types don't exactly match the method's parameter types
		Class<?>[] argumentTypes = inferArgumentTypes(args);

		// Get this thread's class loader and load the class, then get the method in the class with the specified name
		Class<?> clazz = null;
		Method method = null;
		try {
			clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
			method = clazz.getMethod(methodName, argumentTypes);
		} catch (SecurityException e1) {
			System.err.println("Security exception, ensure that class " + className + " is public, caused by " + e1);
		} catch (NoSuchMethodException e1) {
			System.err.println("No such method exception, method: " + methodName + ", caused by " + e1);
		} catch (ClassNotFoundException e1) {
			System.err.println("Error, could not find class name=" + className + ", caused by " + e1);
		}
		return method;
	}


	/** Create the list of parameter class types from the list of parameters
	 * @param args the list of arguments to infer types from
	 * @return list of argument types
	 */
	public static Class<?>[] inferArgumentTypes(Object[] args) {
		Class<?>[] argumentTypes = null;
		if(args != null && args.length > 0 && !(args.length == 1 && args[0] == null)) {
			int argCount = args.length;
			argumentTypes = new Class<?>[argCount];
			for(int i = 0; i < argCount; i++) {
				argumentTypes[i] = args[i].getClass();
			}
		}
		return argumentTypes;
	}


	/** Create a class loader using the specified URL
	 * @param path the URL to set as the classloader's path
	 * @return the classloader with the specified classpath
	 */
	public static URLClassLoader createClassLoader(URL path) {
		return new URLClassLoader(new URL[] {path}, Thread.currentThread().getContextClassLoader());
	}


	public static URL toURLClean(String path) {
		try {
			return toURL(path);
		} catch (IOException e) {
			System.err.println("Error creating class loader URL");
			e.printStackTrace();
		}
		return null;
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


	public static URL getApplicationPathURL() {
		if(appPath == null) {
			appPath = ClassLoaders.class.getProtectionDomain().getCodeSource().getLocation();
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

}
