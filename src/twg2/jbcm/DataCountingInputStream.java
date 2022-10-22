package twg2.jbcm;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/** A {@link DataInputStream} that counts the number of bytes that have been read.
 * @author TeamworkGuy2
 * @since 2022-07-28
 */
public class DataCountingInputStream extends DataInputStream {

	public DataCountingInputStream(InputStream in) {
		super(new CountingInputStream(in));
	}


	public int bytesRead() {
		return ((CountingInputStream)super.in).readCount;
	}


	/**
	 * @author TeamworkGuy2
	 * @since 2022-07-28
	 */
	public static class CountingInputStream extends InputStream {
		int readCount = 0;
		InputStream in;

		public CountingInputStream(InputStream in) {
			super();
			this.in = in;
		}


		@Override
		public int read() throws IOException {
			int b = in.read();
			readCount += (b > -1 ? 1 : 0);
			return b;
		}


		@Override
		public int read(byte[] b) throws IOException {
			int res = in.read(b);
			readCount += (res > -1 ? res : 0);
			return res;
		}


		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			int res = in.read(b, off, len);
			readCount += (res > -1 ? res : 0);
			return res;
		}
	}

}
