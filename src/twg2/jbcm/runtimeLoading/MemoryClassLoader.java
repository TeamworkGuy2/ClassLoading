package twg2.jbcm.runtimeLoading;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;

import twg2.jbcm.IoUtility;

/** A class loader to load classes from byte arrays representing class files
 * @author TeamworkGuy2
 * @since 2013-9-24
 */
public class MemoryClassLoader extends ClassLoader implements ResourceClassLoader {
	private File customPath;

	private class CustomClass {
		private final String className;
		private final byte[] data;
		private boolean isDefined;
		private Class<?> definedClass;

		CustomClass(String className, byte[] b) {
			this.className = className;
			this.data = b;
		}
	}

	/** {@link sun.misc.Launcher$AppClassLoader}
	 * {@link java.lang.ClassLoader#loadClass()}
	 * {@link java.net.URLClassLoader$1} */
	private HashMap<String, CustomClass> memoryClasses = new HashMap<String, CustomClass>(64, 0.5f);


	public MemoryClassLoader() {
		super();
	}


	public MemoryClassLoader(Void v, File projectBasePath) {
		super(null);
		this.customPath = projectBasePath;
	}


	@Override
	public Class<?> loadClass(String name) {
		System.out.println("Load class: " + name);
		CustomClass classData = memoryClasses.get(name);
		if(classData != null) {
			return setupCustomClass(classData);
		}
		//throw new IllegalStateException("Stack for loading class [" + name + "] from overridden loadClass()");
		else {
			try {
				return super.loadClass(name);
			} catch (ClassNotFoundException e) {
				throw new IllegalStateException("parent class loader could not load '" + name + "', " + e);
			}
		}
	}


	@Override
	public Class<?> findClass(String name) {
		System.out.println("Find class: " + name);
		CustomClass classData = memoryClasses.get(name);
		if(classData != null) {
			return setupCustomClass(classData);
		}
		else {
			if(customPath != null) {
				try {
					byte[] classBytes = IoUtility.loadBytes(new File(customPath, name.replace('.', '/') + ".class"));
					CustomClass customClass = new CustomClass(name, classBytes);
					memoryClasses.put(name, customClass);
					return setupCustomClass(customClass);
				} catch (MalformedURLException e) {
					throw new NoClassDefFoundError("Could not load class \"" + name + "\" using MemoryClassLoader, cause: " + e);
				} catch (IOException e) {
					throw new NoClassDefFoundError("Could not load class \"" + name + "\" using MemoryClassLoader, cause: " + e);
				}
			}
			throw new NoClassDefFoundError("Could not load class \"" + name + "\" using MemoryClassLoader");
		}
		
	}


	/** Get the class object of the specified custom class, initialize the class if necessary
	 * @param classData the custom class object to process
	 * @return the class stored in the custom class object
	 */
	private final Class<?> setupCustomClass(CustomClass classData) {
		if(classData.isDefined == false) {
			System.out.println("Define class: " + classData.className);
			Class<?> clas = super.defineClass(classData.className, classData.data, 0, classData.data.length);
			classData.isDefined = true;
			classData.definedClass = clas;
			return clas;
		}
		else {
			System.out.println("Using class: " + classData.className);
			return classData.definedClass;
		}
	}


	@Override
	public void addClassByteCode(String name, byte[] b) {
		memoryClasses.put(name, new CustomClass(name, b));
	}

}
