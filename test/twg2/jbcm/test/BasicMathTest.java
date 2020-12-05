package twg2.jbcm.test;

import org.junit.Assert;
import org.junit.Test;

import twg2.jbcm.testParser.BytecodePseudoParser;
import twg2.jbcm.testParser.CompileTest;

/**
 * @author TeamworkGuy2
 * @since 2020-07-13
 */
public class BasicMathTest {

	@Test
	public void arrayTest() {
		var java = CompileTest.compileCode("int[] ary = new int[3]; int i = 2; ary[i] = (int)(i * Math.random()) % 2;");

		var res = BytecodePseudoParser.parse(
			"ICONST_3 NEWARRAY 10 ASTORE_1 ICONST_2 ISTORE_2 ALOAD_1 ILOAD_2 ILOAD_2 I2D INVOKESTATIC 0 2 DMUL D2I ICONST_2 IREM IASTORE RETURN"
		);

		Assert.assertArrayEquals(java, res);
	}


	@Test
	public void mathTest() {
		var java = CompileTest.compileCode("int x = 2; int y = 6; int r = x * y; r++;");

		var res = BytecodePseudoParser.parse(
			"ICONST_2 ISTORE_1 BIPUSH 6 ISTORE_2 ILOAD_1 ILOAD_2 IMUL ISTORE_3 IINC 3 1 RETURN"
		);

		Assert.assertArrayEquals(java, res);
	}

}
