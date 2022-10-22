package twg2.jbcm.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import twg2.jbcm.DataCountingInputStream;

/**
 * @author TeamworkGuy2
 * @since 2022-07-28
 */
public class DataCountingInputStreamTest {

	@Test
	public void readBytes() throws IOException {
		@SuppressWarnings("resource")
		DataCountingInputStream ds = new DataCountingInputStream(new ByteArrayInputStream(new byte[] {2, 4, 6, 10}));

		int d = ds.read();
		Assert.assertEquals(2, d);
		Assert.assertEquals(1, ds.bytesRead());
		d = ds.readShort();
		Assert.assertEquals((4 << 8) | 6, d);
		Assert.assertEquals(3, ds.bytesRead());
		byte[] b = new byte[1];
		ds.read(b);
		Assert.assertArrayEquals(new byte[] {10}, b);
		Assert.assertEquals(4, ds.bytesRead());
	}

}
