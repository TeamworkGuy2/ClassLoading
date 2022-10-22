package twg2.jbcm.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** A class for converting URL resources to classes.
 * @author TeamworkGuy2
 * @since 2013-9-24
 */
public class ResourceToClass {

	public static Class<?>[] loadClasses(URL[] urls, MemoryClassLoader cl) throws IOException {
		int size = 8192;
		byte[] buf = new byte[size];
		int urlCount = urls.length;
		// Open each URL and load its contents into a byte buffer
		for(int i = 0; i < urlCount; i++) {
			InputStream is = urls[i].openStream();
			int bufPos = 0;
			int read = 0;
			// Read each input stream into our byte array buffer
			while(read > -1) {
				int newSize = bufPos + size;
				if(newSize > buf.length) {
					buf = Arrays.copyOf(buf, (buf.length << 1));
				}
				bufPos = newSize;
				read = is.read(buf, bufPos - size, size);
			}
			// Create a copy of the buffers exact length and add it to the classloader
			byte[] bufN = new byte[bufPos];
			System.arraycopy(buf, 0, bufN, 0, bufPos);
			cl.addClassByteCode(urls[i].getFile(), bufN);
		}

		// For each URL class, convert the byte array into a class in the classloader
		Class<?>[] classes = new Class<?>[urlCount];
		for(int i = 0; i < urlCount; i++) {
			classes[i] = cl.findClass(urls[i].getFile());
		}
		return classes;
	}


	public static List<Class<?>> loadClasses(InputStream[] dataStreams, String[] fileNames, MemoryClassLoader cl) throws IOException {
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		int size = 8192;
		byte[] buf = new byte[size];
		int bufPos = 0;
		int newSize = 0;
		int read = 0;
		// Open each URL and load its contents into a byte buffer
		for(int i = 0, sz = dataStreams.length; i < sz; i++) {
			InputStream stream = dataStreams[i];
			bufPos = 0;
			read = 0;
			// Read each input stream into our byte array buffer
			while(read > -1) {
				newSize = bufPos + size;
				if(newSize > buf.length) {
					buf = Arrays.copyOf(buf, (buf.length << 1));
				}
				bufPos = newSize;
				read = stream.read(buf, bufPos-size, size);
			}
			// Create a copy of the buffers exact length and add it to the classloader
			byte[] bufN = new byte[bufPos];
			System.arraycopy(buf, 0, bufN, 0, bufPos);
			cl.addClassByteCode(fileNames[i], bufN);
		}

		// For each URL class, convert the byte array into a class in the classloader
		for(int i = 0, sz = dataStreams.length; i < sz; i++) {
			classes.add(cl.findClass(fileNames[i]));
		}
		return classes;
	}

}
