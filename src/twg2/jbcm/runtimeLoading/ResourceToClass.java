package twg2.jbcm.runtimeLoading;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

/** A class for converting URL resources to classes.
 * @author TeamworkGuy2
 * @since 2013-9-24
 */
public class ResourceToClass {

	/** Load a list of URLs as class files and create an instance of the
	 * <code>runI</code> index class and cast it to {@link Runnable} and
	 * call the {@link Runnable#run() run()} method on it.
	 * @param urls the list of class file locations to load
	 * @param runI the index in the url array of corresponding to the class to run
	 * once the classes are loaded
	 * @param cl the classloader to handle loading the classes
	 * @throws InstantiationException if an instance of the class at
	 * index <code>runI</code> cannot be created.
	 * @throws IllegalAccessException if the constructor for the class instance
	 * cannot be called because it is private, protected, etc.
	 * @throws IOException if there is any other error loading the urls
	 */
	public ResourceToClass(URL[] urls, int runI, MemoryClassLoader cl) throws InstantiationException, IllegalAccessException, IOException {
		Class<?>[] classes = loadClasses(urls, cl);
		Runnable r = (Runnable)classes[runI].newInstance();
		r.run();
	}


	private Class<?>[] loadClasses(URL[] urls, MemoryClassLoader cl) throws IOException {
		Class<?>[] classes = new Class<?>[urls.length];
		int size = 8192;
		byte[] buf = new byte[size];
		int bufP = 0;
		int newSize = 0;
		int urlCount = urls.length;
		int read = 0;
		InputStream is = null;
		// Open each URL and load its contents into a byte buffer
		for(int i = 0; i < urlCount; i++) {
			is = urls[i].openStream();
			bufP = 0;
			read = 0;
			// Read each input stream into our byte array buffer
			while(read > -1) {
				newSize = bufP + size;
				if(newSize > buf.length) {
					buf = Arrays.copyOf(buf, (buf.length+2 << 1));
				}
				bufP = newSize;
				read = is.read(buf, bufP-size, size);
			}
			// Create a copy of the buffers exact length and add it to the classloader
			byte[] bufN = new byte[bufP];
			System.arraycopy(buf, 0, bufN, 0, bufP);
			cl.addClassByteCode(urls[i].getFile(), bufN);
		}
		// For each URL class, convert the byte array into a class in the classloader
		for(int i = 0; i < urlCount; i++) {
			System.out.println("[ResourceToClass.loadClasses()] find class: " + urls[i].getFile() + " (" + urls[i] + ")");
			classes[i] = cl.findClass(urls[i].getFile());
		}
		return classes;
	}

}
