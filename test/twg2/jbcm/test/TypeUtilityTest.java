package twg2.jbcm.test;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import twg2.jbcm.Opcodes;
import twg2.jbcm.Opcodes.Type;
import twg2.jbcm.modify.TypeUtility;

/**
 * @author TeamworkGuy2
 * @since 2020-08-09
 */
public class TypeUtilityTest {

	@Test
	public void classToFieldDescriptor() {
		Assert.assertEquals("Ljava/lang/String;", TypeUtility.classToFieldDescriptor(String.class));
		Assert.assertEquals("[Ljava/lang/String;", TypeUtility.classToFieldDescriptor(String[].class));
		Assert.assertEquals("[[Ljava/lang/Object;", TypeUtility.classToFieldDescriptor(Object[][].class));
		Assert.assertEquals("I", TypeUtility.classToFieldDescriptor(Integer.TYPE));
		Assert.assertEquals("J", TypeUtility.classToFieldDescriptor(Long.TYPE));

		var tmp = new StringBuilder();
		tmp.setLength(0); Assert.assertEquals("Ljava/lang/String;", TypeUtility.classToFieldDescriptor(String.class, tmp).toString());
		tmp.setLength(0); Assert.assertEquals("[Ljava/lang/String;", TypeUtility.classToFieldDescriptor(String[].class, tmp).toString());
		tmp.setLength(0); Assert.assertEquals("[[Ljava/lang/Object;", TypeUtility.classToFieldDescriptor(Object[][].class, tmp).toString());
		tmp.setLength(0); Assert.assertEquals("I", TypeUtility.classToFieldDescriptor(Integer.TYPE, tmp).toString());
		tmp.setLength(0); Assert.assertEquals("J", TypeUtility.classToFieldDescriptor(Long.TYPE, tmp).toString());
		tmp.setLength(0); Assert.assertEquals("[Z", TypeUtility.classToFieldDescriptor(boolean[].class, tmp).toString());
	}


	@Test
	public void methodParameters() {
		var dst = new ArrayList<String>();

		dst.clear(); TypeUtility.methodParameters("(IDLjava/lang/Thread;)Ljava/lang/Object;", dst);
		Assert.assertArrayEquals(new String[] { "int", "double", "Thread" }, toArray(dst));

		dst.clear(); TypeUtility.methodParameters("(JLjava/lang/String;Ljava/util/Collection;)Z", dst);
		Assert.assertArrayEquals(new String[] { "long", "String", "java.util.Collection" }, toArray(dst));

		dst.clear(); TypeUtility.methodParameters("()B", dst);
		Assert.assertArrayEquals(new String[0], toArray(dst));
	}


	@Test
	public void methodReturnDescriptorToSource() {
		var tmp = new StringBuilder();
		tmp.setLength(0); TypeUtility.methodReturnType("(IDLjava/lang/Thread;)Ljava/lang/Object;", tmp);
		Assert.assertEquals("Object", tmp.toString());

		tmp.setLength(0); TypeUtility.methodReturnType("(JLjava/lang/String;Ljava/util/Collection;)Z", tmp);
		Assert.assertEquals("boolean", tmp.toString());

		tmp.setLength(0); TypeUtility.methodReturnType("(F)Ljava/util/Collection;", tmp);
		Assert.assertEquals("java.util.Collection", tmp.toString());

		tmp.setLength(0); TypeUtility.methodReturnType("()B", tmp);
		Assert.assertEquals("byte", tmp.toString());

		tmp.setLength(0); TypeUtility.methodReturnType("()V", tmp);
		Assert.assertEquals("void", tmp.toString());
	}


	@Test
	public void opcodeTypesTest() {
		Assert.assertTrue(Opcodes.IF_ACMPNE.hasBehavior(Type.POP2));
		Assert.assertTrue(Opcodes.IF_ACMPNE.hasBehavior(Type.CONDITION));
		Assert.assertTrue(Opcodes.IF_ACMPNE.hasBehavior(Type.JUMP));
	}


	private static String[] toArray(Collection<String> src) {
		return src.toArray(new String[src.size()]);
	}

}
