package twg2.jbcm;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

/**
 * @author TeamworkGuy2
 * @since 2014-4-19
 */
public final class IoUtility {

	private IoUtility() { throw new AssertionError("cannot instantiate static class IoUtility"); }


	/** Write the specified long to the specified byte array
	 * @param value the long to write
	 * @param b the byte array to write the long to
	 * @param offset the offset into the array at which to start writing bytes
	 */
	public static void writeLong(long value, byte[] b, int offset) {
		b[offset] = (byte)(value >>> 56);
		b[offset+1] = (byte)(value >>> 48);
		b[offset+2] = (byte)(value >>> 40);
		b[offset+3] = (byte)(value >>> 32);
		b[offset+4] = (byte)(value >>> 24);
		b[offset+5] = (byte)(value >>> 16);
		b[offset+6] = (byte)(value >>> 8);
		b[offset+7] = (byte)(value & 0xFF);
	}


	/** Write the specified int to the specified byte array
	 * @param value the integer to write
	 * @param b the byte array to write the integer to
	 * @param offset the offset into the array at which to start writing bytes
	 */
	public static void writeInt(int value, byte[] b, int offset) {
		b[offset] = (byte)((value >>> 24) & 0xFF);
		b[offset+1] = (byte)((value >>> 16) & 0xFF);
		b[offset+2] = (byte)((value >>> 8) & 0xFF);
		b[offset+3] = (byte)(value & 0xFF);
	}


	/** Write the specified short to the specified byte array
	 * @param value the short to write
	 * @param b the byte array to write the short to
	 * @param offset the offset into the array at which to start writing bytes
	 */
	public static void writeShort(short value, byte[] b, int offset) {
		b[offset] = (byte)((value >>> 8) & 0xFF);
		b[offset+1] = (byte)(value & 0xFF);
	}


	/** Read a long value from the specified location in the specified array
	 * @param b the array to read the short from
	 * @param offset the offset into the array at which to start reading bytes
	 * @return eight bytes read from the indices {@code [offset, offset+3]} and converted to
	 * a long by {@code (b[offset] << 24) | (b[offset+1] << 16) | (b[offset+2] << 8) | b[offset+3]}
	 */
	public static long readLong(byte[] b, int offset) {
		return ((long)b[offset] << 56) |
				((long)(b[offset+1] & 0xFF) << 48) |
				((long)(b[offset+2] & 0xFF) << 40) |
				((long)(b[offset+3] & 0xFF) << 32) |
				((long)(b[offset+4] & 0xFF) << 24) |
				((b[offset+5] & 0xFF) << 16) |
				((b[offset+6] & 0xFF) << 8) |
				(b[offset+7] & 0xFF);
	}


	/** Read an integer value from the specified location in the specified array
	 * @param b the array to read the short from
	 * @param offset the offset into the array at which to start reading bytes
	 * @return four bytes read from the indices {@code [offset, offset+3]} and converted to
	 * an integer by {@code (b[offset] << 24) | (b[offset+1] << 16) | (b[offset+2] << 8) | b[offset+3]}
	 */
	public static int readInt(byte[] b, int offset) {
		return (b[offset] << 24) | (b[offset+1] << 16) | (b[offset+2] << 8) | b[offset+3];
	}


	/** Read a short value from the specified location in the specified array
	 * @param b the array to read the short from
	 * @param offset the offset into the array at which to start reading bytes
	 * @return two bytes read from indices {@code offset} and {@code offset+1} and converted to
	 * a short by {@code (b[offset] << 8) | b[offset+1]}
	 */
	public static short readShort(byte[] b, int offset) {
		return (short)((b[offset] << 8) | b[offset+1]);
	}


	public static byte[] loadBytes(File file) throws IOException {
		return loadBytes(file.toURI().toURL());
	}


	public static byte[] loadBytes(URL url) throws IOException {
		byte[] buf = new byte[8192];
		InputStream is = null;
		// Open the URL and load its contents into a byte buffer
		is = url.openStream();
		int read = 0;
		int totalRead = 0;
		// Read each input stream into our byte array buffer
		try {
			while((read = is.read(buf, totalRead, buf.length-totalRead)) > -1) {
				if(totalRead >= buf.length) {
					buf = Arrays.copyOf(buf, ((buf.length+2) << 1));
				}
				totalRead += read;
			}
		} finally {
			if(is != null) { is.close(); }
		}
		// Create a copy of the buffers exact length and add it to the classloader
		byte[] bufN = new byte[totalRead];
		System.arraycopy(buf, 0, bufN, 0, totalRead);
		return bufN;
	}

}
