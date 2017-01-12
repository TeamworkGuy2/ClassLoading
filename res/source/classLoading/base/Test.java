package classLoading.base;

/** Test of classloading and dynamically loading classes
 * @author TeamworkGuy2
 * @since 2013-7-12
 */
public class Test {

	/* Careful this class relies on class files in a folder called "load" inside this class's folder
	 */
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass("classLoading.ClassLoaded");
		WorkerInterface load = (WorkerInterface)clazz.newInstance();
		//ClassLoaded load = new ClassLoaded();
		System.out.println("Test.Main: " + Thread.currentThread() + ", " + Thread.currentThread().getContextClassLoader());
		load.startThreads(3);
	}

}
