package twg2.jbcm.test;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import twg2.jbcm.testParser.BytecodePseudoParser;
import twg2.jbcm.testParser.CompileJava;

/**
 * @author TeamworkGuy2
 * @since 2020-08-10
 */
public class MethodCallTest {

	@Test
	public void invokeVirtualTest() {
		var java = CompileJava.compileCode("byte[] res = \"\".getBytes();");

		var res = BytecodePseudoParser.parse(
				"LDC 2 INVOKEVIRTUAL 0 3 ASTORE_1 RETURN"
		);

		Assert.assertArrayEquals(java, res);
	}


	@Test
	public void invokeStaticAndVirtualTest() {
		var java = CompileJava.compileCode(Arrays.asList("java.nio.charset.Charset"), "Charset charset = Charset.forName(\"ASCII\"); byte[] res = \"\".getBytes(charset);", false);

		var res = BytecodePseudoParser.parse(
				"LDC 2 INVOKESTATIC 0 3 ASTORE_1 LDC 4 ALOAD_1 INVOKEVIRTUAL 0 5 ASTORE_2 RETURN"
		);

		Assert.assertArrayEquals(java, res);
	}

}
