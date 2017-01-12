package twg2.jbcm.classFormat;

import java.io.DataInput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import twg2.jbcm.classFormat.attributes.Attribute_Type;
import twg2.jbcm.classFormat.attributes.Code;
import twg2.jbcm.classFormat.attributes.Element_Value;
import twg2.jbcm.classFormat.attributes.UnknownAttributeType;
import twg2.jbcm.classFormat.constantPool.CONSTANT_CP_Info;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Class;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Double;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Float;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Integer;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Long;
import twg2.jbcm.classFormat.constantPool.CONSTANT_MethodHandle;
import twg2.jbcm.classFormat.constantPool.CONSTANT_MethodType;
import twg2.jbcm.classFormat.constantPool.CONSTANT_String;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Utf8;



/** Settings for the Java class file format classes
 * @author TeamworkGuy2
 * @since 2013-7-7
 */
public class Settings {
	public static final boolean debug = true;
	public static final boolean cpTagRead = true;
	public static final boolean checkCPIndex = true;
	private static final boolean checkAttributeName = true;
	private static final boolean readAttributeName = false;

	private static Map<String, ClassFileAttributes> attribMap;

	private static ConstantPoolTag[] tagArray;
	private static Map<Integer, ConstantPoolTag> tagMap;
	private static boolean useTagArray;


	static {
		ConstantPoolTag[] tags = ConstantPoolTag.values();
		int min = Integer.MAX_VALUE;
		int max = 0;

		for(ConstantPoolTag tag : tags) {
			if(tag.getTag() < min) { min = tag.getTag(); }
			if(tag.getTag() > max) { max = tag.getTag(); }
		}

		// Check whether the tag values are tightly packed enough to be stored in an array or
		// if they should be stored in a map because they are loosely packed
		useTagArray = useArrayOrMap(tags.length, min, max);
		if(useTagArray) {
			tagArray = new ConstantPoolTag[max];
			// Fill the array with the tags
			for(ConstantPoolTag tag : tags) {
				tagArray[tag.getTag()] = tag;
			}
		}
		else {
			tagMap = new HashMap<Integer, ConstantPoolTag>(tags.length);
			// Fill the map with the tags
			for(ConstantPoolTag tag : tags) {
				tagMap.put(tag.getTag(), tag);
			}
		}

		ClassFileAttributes[] attribs = ClassFileAttributes.values();
		attribMap = new HashMap<String, ClassFileAttributes>(attribs.length);
		for(ClassFileAttributes attrib : attribs) {
			attribMap.put(attrib.getBinaryName(), attrib);
		}
	}


	private Settings() {
		throw new AssertionError("may not instantiate Settings");
	}


	/** Use a set of parameters to determine if a group of values should be stored in an array of map for
	 * most efficient space/time trade off.
	 * @param count the number of items that will be stored in the collection
	 * @param min the minimum value identifying the least item
	 * @param max the maximum value identifying the greatest item
	 * @return true if the items are closely enough packed to be stored efficiently in an array,
	 * false if the items are sparsely packed and should be stored in a map
	 */
	private static final boolean useArrayOrMap(int count, int min, int max) {
		// If the ratio of tags to the distance between the max and min tags is
		// less than 25% or the minimum tag is greater than half the number of tags
		// use a map to store the tags, else use an array

		// For example, if the minimum tag's value is 20 and the maximum tag's value is 60
		// and there are 8 tags between these values, then the range is (60-20=40) and the
		// ratio is 8/40 which is 0.2, so we use a map.

		// The second part checks if the minimum tag (20 in the example) is greater than
		// half the number of tags (8 tags) which it is so we use a map.
		// This forces the tag values to be packed with 1/4 efficiency into their range
		// and start close to zero

		// The third part ensures that if there are more than a 1000 tags, their do not need more than twice
		// their count of array values to be represented.
		if(count/(max-min) < 0.25 || min > (count >>> 1) ||
				(count > 1000 && max > (count << 1))) {
			return false;
		}
		else {
			return true;
		}
	}


	/* Each tag byte must be followed by two or more bytes giving information about the specific constant.
	 * The format of the additional information varies with the tag value.
	 * Constant Type 	Value
	 * CONSTANT_Class 	7
	 * CONSTANT_Fieldref 	9
	 * CONSTANT_Methodref 	10
	 * CONSTANT_InterfaceMethodref 	11
	 * CONSTANT_String 	8
	 * CONSTANT_Integer 	3
	 * CONSTANT_Float 	4
	 * CONSTANT_Long 	5
	 * CONSTANT_Double 	6
	 * CONSTANT_NameAndType 	12
	 * CONSTANT_Utf8 	1
	 * CONSTANT_MethodHandle 	15
	 * CONSTANT_MethodType 	16
	 * CONSTANT_InvokeDynamic 	18
	 * 
	 * The contents of the info array vary with the value of tag.
	 * 
	 * CONSTANT_Class_info Structure
	 * u1 tag;
	 * // The value of the name_index item must be a valid index into the constant_pool table.
	 * // The constant_pool entry at that index must be a CONSTANT_Utf8_info (§4.4.7) structure
	 * // representing a valid fully qualified class or interface name (§2.8.1) encoded in internal form (§4.2). 
	 * u2 name_index;
	 * 
	 * CONSTANT_Fieldref_info, CONSTANT_Methodref_info, and CONSTANT_InterfaceMethodref_info Structures
	 * u1 tag;
	 * // The value of the class_index item must be a valid index into the constant_pool table. The constant_pool entry
	 * // at that index must be a CONSTANT_Class_info (§4.4.1) structure representing the class or interface type that
	 * // contains the declaration of the field or method.
	 * // The class_index item of a CONSTANT_Methodref_info structure must be a class type, not an interface type.
	 * //The class_index item of a CONSTANT_InterfaceMethodref_info structure must be an interface type.
	 * //The class_index item of a CONSTANT_Fieldref_info structure may be either a class type or an interface type. 
	 * u2 class_index;
	 * // The value of the name_and_type_index item must be a valid index into the constant_pool table. The constant_pool
	 * // entry at that index must be a CONSTANT_NameAndType_info (§4.4.6) structure. This constant_pool entry indicates
	 * // the name and descriptor of the field or method. In a CONSTANT_Fieldref_info the indicated descriptor must be a
	 * // field descriptor (§4.3.2). Otherwise, the indicated descriptor must be a method descriptor (§4.3.3).
	 * // If the name of the method of a CONSTANT_Methodref_info structure begins with a' <' ('\u003c'), then the name
	 * // must be the special name <init>, representing an instance initialization method (§3.9). Such a method must
	 * // return no value. 
	 * u2 name_and_type_index;
	 * 
	 * CONSTANT_String_info Structure
	 * u1 tag;
	 * // The value of the string_index item must be a valid index into the constant_pool table. The constant_pool entry
	 * // at that index must be a CONSTANT_Utf8_info (§4.4.7) structure representing the sequence of characters to which
	 * // the String object is to be initialized.
	 * u2 string_index;
	 * 
	 * CONSTANT_Integer_info and CONSTANT_Float_info Structures
	 * u1 tag;
	 * // The bytes item of the CONSTANT_Integer_info structure represents the value of the int constant. The bytes of the
	 * // value are stored in big-endian (high byte first) order.
	 * // The bytes item of the CONSTANT_Float_info structure represents the value of the float constant in IEEE 754
	 * // floating-point single format (§3.3.2). The bytes of the single format representation are stored in
	 * // big-endian (high byte first) order.
	 * // The value represented by the CONSTANT_Float_info structure is determined as follows. The bytes of the value
	 * // are first converted into an int constant bits. Then:
	 * // If bits is 0x7f800000, the float value will be positive infinity.
	 * // If bits is 0xff800000, the float value will be negative infinity.
	 * // If bits is in the range 0x7f800001 through 0x7fffffff or in the range 0xff800001 through 0xffffffff,
	 * // the float value will be NaN.
	 * // In all other cases, let s, e, and m be three values that might be computed from bits: 
	 * // 	int s = ((bits >> 31) == 0) ? 1 : -1;
	 * // 	int e = ((bits >> 23) & 0xff);
	 * // 	int m = (e == 0) ? (bits & 0x7fffff) << 1 : (bits & 0x7fffff) | 0x800000;
	 * // Then the float value equals the result of the mathematical expression s·m·2e-150.
	 * u4 bytes;
	 * 
	 * CONSTANT_Long_info and CONSTANT_Double_info Structures
	 * u1 tag;
	 * // The unsigned high_bytes and low_bytes items of the CONSTANT_Long_info structure together represent the value
	 * // of the long constant ((long) high_bytes << 32) + low_bytes, where the bytes of each of high_bytes and low_bytes
	 * // are stored in big-endian (high byte first) order.
	 * // The high_bytes and low_bytes items of the CONSTANT_Double_info structure together represent the double value in
	 * // IEEE 754 floating-point double format (§3.3.2). The bytes of each item are stored in big-endian
	 * // (high byte first) order.
	 * // The value represented by the CONSTANT_Double_info structure is determined as follows. The high_bytes and
	 * // low_bytes items are first converted into the long constant bits, which is equal
	 * // to ((long) high_bytes << 32) + low_bytes. Then:
	 * // If bits is 0x7ff0000000000000L, the double value will be positive infinity.
	 * // If bits is 0xfff0000000000000L, the double value will be negative infinity.
	 * // If bits is in the range 0x7ff0000000000001L through 0x7fffffffffffffffL or in the
	 * // range 0xfff0000000000001L through 0xffffffffffffffffL, the double value will be NaN.
	 * // In all other cases, let s, e, and m be three values that might be computed from bits: 
	 * // int s = ((bits >> 63) == 0) ? 1 : -1;
	 * // int e = (int)((bits >> 52) & 0x7ffL);
	 * // long m = (e == 0) ? (bits & 0xfffffffffffffL) << 1 : (bits & 0xfffffffffffffL) | 0x10000000000000L;
	 * // Then the floating-point value equals the double value of the mathematical expression s·m·2e-1075. 
	 * u4 high_bytes;
	 * u4 low_bytes;
	 * 
	 * CONSTANT_NameAndType_info Structure 
	 * u1 tag;
	 * // The value of the name_index item must be a valid index into the constant_pool table. The constant_pool entry
	 * // at that index must be a CONSTANT_Utf8_info (§4.4.7) structure representing either a valid field or method name
	 * // (§2.7) stored as a simple name (§2.7.1), that is, as a Java programming language identifier (§2.2) or as the
	 * // special method name <init> (§3.9).
	 * u2 name_index;
	 * // The value of the descriptor_index item must be a valid index into the constant_pool table. The constant_pool
	 * // entry at that index must be a CONSTANT_Utf8_info (§4.4.7) structure representing a valid field descriptor
	 * // (§4.3.2) or method descriptor (§4.3.3).
	 * u2 descriptor_index;
	 * 
	 * CONSTANT_Utf8_info Structure
	 * u1 tag;
	 * // The value of the length item gives the number of bytes in the bytes array
	 * // (not the length of the resulting string). The strings in the CONSTANT_Utf8_info structure are
	 * // not null-terminated.
	 * u2 length;
	 * // The bytes array contains the bytes of the string. No byte may have the value (byte)0 or lie in the
	 * // range (byte)0xf0-(byte)0xff.
	 * u1 bytes[length];
	 * 
	 * 1 byte characters: 0xxxxxxx ('\u0001' to '\u007F')
	 * 2 byte characters: (x)110xxxxx (y)10xxxxxx ('\u0000' and '\u0080' to '\u07FF') ((x&0x1f)<<6)+(y&03f)
	 * 3 byte characters: (x)1110xxxx (y)10xxxxxx (z)10xxxxxx ((x & 0xf) << 12) + ((y & 0x3f) << 6) + (z & 0x3f)
	 * stored in big-endian order in class file
	 * 
	 * CONSTANT_MethodHandle_info Structure
	 * u1 tag;
	 * // The tag item of the CONSTANT_MethodHandle_info structure has the value CONSTANT_MethodHandle (15).
	 * u1 reference_kind;
	 * // The value of the reference_kind item must be in the range 1 to 9. The value denotes the kind of
	 * // this method handle, which characterizes its bytecode behavior (§5.4.3.5).
	 * u2 reference_index;
	 * // The value of the reference_index item must be a valid index into the constant_pool table.
	 * // If the value of the reference_kind item is 1 (REF_getField), 2 (REF_getStatic), 3 (REF_putField),
	 * // or 4 (REF_putStatic), then the constant_pool entry at that index must be a CONSTANT_Fieldref_info
	 * // (§4.4.2) structure representing a field for which a method handle is to be created.
	 * // If the value of the reference_kind item is 5 (REF_invokeVirtual), 6 (REF_invokeStatic),
	 * // 7 (REF_invokeSpecial), or 8 (REF_newInvokeSpecial), then the constant_pool entry at that
	 * // index must be a CONSTANT_Methodref_info structure (§4.4.2) representing a class's method or
	 * // constructor (§2.9) for which a method handle is to be created.
	 * // If the value of the reference_kind item is 9 (REF_invokeInterface), then the constant_pool entry
	 * // at that index must be a CONSTANT_InterfaceMethodref_info (§4.4.2) structure representing an interface's
	 * // method for which a method handle is to be created.
	 * // If the value of the reference_kind item is 5 (REF_invokeVirtual), 6 (REF_invokeStatic),
	 * // 7 (REF_invokeSpecial), or 9 (REF_invokeInterface), the name of the method represented by a
	 * // CONSTANT_Methodref_info structure must not be <init> or <clinit>.
	 * // If the value is 8 (REF_newInvokeSpecial), the name of the method represented by a
	 * // CONSTANT_Methodref_info structure must be <init>.
	 * 
	 * CONSTANT_MethodType_info Structure
	 * u1 tag;
	 * // The tag item of the CONSTANT_MethodType_info structure has the value CONSTANT_MethodType (16).
	 * u2 descriptor_index;
	 * // The value of the descriptor_index item must be a valid index into the constant_pool table.
	 * // The constant_pool entry at that index must be a CONSTANT_Utf8_info (§4.4.7) structure representing
	 * // a method descriptor (§4.3.3).
	 * 
	 * CONSTANT_InvokeDynamic_info Structure
	 * u1 tag;
	 * // The tag item of the CONSTANT_InvokeDynamic_info structure has the value CONSTANT_InvokeDynamic (18).
	 * u2 bootstrap_method_attr_index;
	 * // The value of the bootstrap_method_attr_index item must be a valid index into the bootstrap_methods
	 * // array of the bootstrap method table (§4.7.21) of this class file.
	 * u2 name_and_type_index;
	 * // The value of the name_and_type_index item must be a valid index into the constant_pool table.
	 * // The constant_pool entry at that index must be a CONSTANT_NameAndType_info (§4.4.6) structure
	 * // representing a method name and method descriptor (§4.3.3).
	 */

	/** Java class file format <code>ConstantPool</code> info type loading method
	 * @author TeamworkGuy2
	 * @since 2013-7-7
	 */
	public static final CONSTANT_CP_Info loadConstantPoolObject(DataInput in, ClassFile resolver) throws IOException {
		CONSTANT_CP_Info infoObj = null;
		int tag = in.readByte();

		if(useTagArray) {
			try {
				infoObj = tagArray[tag].create(resolver);
			} catch(ArrayIndexOutOfBoundsException e) {
				throw new IllegalArgumentException("Unkown constant pool tag of: " + tag);
			}
		}
		else {
			infoObj = tagMap.get(tag).create(resolver);
			if(infoObj == null) {
				throw new IllegalArgumentException("Unknown constant pool tag of: " + tag);
			}
		}

		infoObj.readData(in);
		return infoObj;
	}


	/** Java class file format <code>Attribute</code> info type loader mothod
	 * @author TeamworkGuy2
	 * @since 2013-7-7
	 * 
	 * @param in the input data stream to read the attribute from
	 * @param resolver the class file to use for constant pool index resolution
	 * @param codeCaller the code related to the attribute being loaded
	 * @return the attribute object read from the input stream
	 * @throws IOException if there is an error reading from the input stream
	 * or if the data read does not match a known attribute format.
	 */
	public static final Attribute_Type loadAttributeObject(DataInput in, ClassFile resolver, Code codeCaller) throws IOException {
		Attribute_Type attrib = null;
		short attributeNameIndex = in.readShort();
		String name = resolver.getCpString(attributeNameIndex);

		Map<String, ClassFileAttributes> attribs = attribMap;
		attrib = attribs.get(name).create(resolver, attributeNameIndex, codeCaller);

		if(attrib == null) {
			attrib = new UnknownAttributeType(resolver, attributeNameIndex);
			System.err.println("[Settings] Unknown Attribute_Info type, name: " + name);
		}

		attrib.readData(in);
		return attrib;
	}


	public static boolean doReadAttributeName() {
		return Settings.readAttributeName;
	}


	/** Read an attribute name index from an input stream and compare the constant pool string
	 * at that index to the expected name.
	 * This method checks if attribute names should be read first and does not modify the input stream
	 * if attribute names should not be read
	 * @author TeamworkGuy2
	 * @since 2014-3-19
	 * 
	 * @param in the input stream to read the name from
	 * @param resolver the constant pool resolver to check the name index against
	 * @param attributeName the expected name of the attribute
	 * @return -1 if the expected name does not match the constant pool string at the index read
	 * or the constant pool index of the correct string read
	 * @throws IOException if there is an error reading from the input stream
	 */
	public static final CpIndex<CONSTANT_Utf8> readAttributeNameIndex(DataInput in,
			ClassFile resolver, String attributeName) throws IOException {
		short nameIndex = -1;
		nameIndex = in.readShort();
		String cpName = resolver.getCpString(nameIndex);
		if(Settings.checkAttributeName) {
			if(!attributeName.equals(cpName)) {
				throw new IllegalStateException(attributeName + " attribute name does not match: " + cpName);
			}
		}
		return resolver.getCheckCpIndex(nameIndex, CONSTANT_Utf8.class);
	}


	/** Read an attribute name index from an input stream.
	 * This method checks if attribute names should be read first and does not modify the input stream
	 * if attribute names should not be read
	 * @author TeamworkGuy2
	 * @since 2014-3-19
	 * 
	 * @param in the input stream to read the name from
	 * @param resolver the constant pool resolver to check the name index against
	 * @return -1 if the expected name does not match the constant pool string at the index read
	 * or the constant pool index of the correct string read
	 * @throws IOException if there is an error reading from the input stream
	 */
	public static final CpIndex<CONSTANT_Utf8> readAttributeNameIndex(DataInput in, ClassFile resolver) throws IOException {
		short nameIndex = -1;
		nameIndex = in.readShort();
		return resolver.getCheckCpIndex(nameIndex, CONSTANT_Utf8.class);
	}


	public static final CpIndex<CONSTANT_Utf8> checkAttributeNameIndex(short index, ClassFile resolver) {
		return resolver.getCheckCpIndex(index, CONSTANT_Utf8.class);
	}


	public static final CpIndex<CONSTANT_Utf8> initAttributeNameIndex(short index, ClassFile resolver) {
		return resolver.getCheckCpIndex(index, CONSTANT_Utf8.class);
	}


	public static final CpIndex<CONSTANT_CP_Info> readCpConstantIndex(DataInput in, ClassFile resolver) throws IOException {
		short constIndex = in.readShort();
		CpIndex<CONSTANT_CP_Info> cpIndex = resolver.getCpIndex(constIndex);
		CONSTANT_CP_Info obj = cpIndex.getCpObject();
		checkCpConstant(obj);
		return cpIndex;
	}


	public static final CONSTANT_CP_Info checkCpConstant(CONSTANT_CP_Info obj) {
		if(obj instanceof CONSTANT_Long || obj instanceof CONSTANT_Float || obj instanceof CONSTANT_Double
				|| obj instanceof CONSTANT_Integer || obj instanceof CONSTANT_String) {
			return obj;
		}
		throw new IllegalArgumentException("constant pool constant value index not valid type: " + obj.getClass());
	}


	/** Read an {@link Element_Value}'s {@code const_value_index} constant pool index.
	 * Handle's tag types of B, C, D, F, I, J, S, Z, s.
	 * Does not handle e, c, @, [.
	 * @param in the data input stream to read the constant pool index from
	 * @param tag the tag constant pool type tag
	 * @param resolver the constant pool to use
	 * @return the constant pool object of the specified tag type at the index read from the input stream
	 * @throws IOException if there is an error reading from the input stream
	 * @throws IllegalArgumentException if the tag type is not one of B, C, D, F, I, J, S, Z, s.
	 */
	public static final CpIndex<CONSTANT_CP_Info> getElement_ValuePrimitiveOrString(DataInput in, byte tag, ClassFile resolver) throws IOException {
		switch(tag) {
		case 'B':
			@SuppressWarnings("unchecked")
			CpIndex<CONSTANT_CP_Info> objB = (CpIndex<CONSTANT_CP_Info>)(CpIndex<? extends CONSTANT_CP_Info>)
					resolver.getCheckCpIndex(in.readShort(), CONSTANT_Integer.class);
			return objB;
		case 'C':
			@SuppressWarnings("unchecked")
			CpIndex<CONSTANT_CP_Info> objC = (CpIndex<CONSTANT_CP_Info>)(CpIndex<? extends CONSTANT_CP_Info>)
					resolver.getCheckCpIndex(in.readShort(), CONSTANT_Integer.class);
			return objC;
		case 'D':
			@SuppressWarnings("unchecked")
			CpIndex<CONSTANT_CP_Info> objD = (CpIndex<CONSTANT_CP_Info>)(CpIndex<? extends CONSTANT_CP_Info>)
					resolver.getCheckCpIndex(in.readShort(), CONSTANT_Double.class);
			return objD;
		case 'F':
			@SuppressWarnings("unchecked")
			CpIndex<CONSTANT_CP_Info> objF = (CpIndex<CONSTANT_CP_Info>)(CpIndex<? extends CONSTANT_CP_Info>)
					resolver.getCheckCpIndex(in.readShort(), CONSTANT_Float.class);
			return objF;
		case 'I':
			@SuppressWarnings("unchecked")
			CpIndex<CONSTANT_CP_Info> objI = (CpIndex<CONSTANT_CP_Info>)(CpIndex<? extends CONSTANT_CP_Info>)
					resolver.getCheckCpIndex(in.readShort(), CONSTANT_Integer.class);
			return objI;
		case 'J':
			@SuppressWarnings("unchecked")
			CpIndex<CONSTANT_CP_Info> objJ = (CpIndex<CONSTANT_CP_Info>)(CpIndex<? extends CONSTANT_CP_Info>)
					resolver.getCheckCpIndex(in.readShort(), CONSTANT_Long.class);
			return objJ;
		case 'S':
			@SuppressWarnings("unchecked")
			CpIndex<CONSTANT_CP_Info> objS = (CpIndex<CONSTANT_CP_Info>)(CpIndex<? extends CONSTANT_CP_Info>)
					resolver.getCheckCpIndex(in.readShort(), CONSTANT_Integer.class);
			return objS;
		case 'Z':
			@SuppressWarnings("unchecked")
			CpIndex<CONSTANT_CP_Info> objZ = (CpIndex<CONSTANT_CP_Info>)(CpIndex<? extends CONSTANT_CP_Info>)
					resolver.getCheckCpIndex(in.readShort(), CONSTANT_Integer.class);
			return objZ;
		case 's':
			@SuppressWarnings("unchecked")
			CpIndex<CONSTANT_CP_Info> objs = (CpIndex<CONSTANT_CP_Info>)(CpIndex<? extends CONSTANT_CP_Info>)
					resolver.getCheckCpIndex(in.readShort(), CONSTANT_Utf8.class);
			return objs;
		default:
			throw new IllegalArgumentException("unknown element_value of " + tag + ", valid values are: B, C, D, F, I, J, S, Z, s, e, c, @, [");
		}
	}


	public static final CpIndex<CONSTANT_CP_Info> checkElement_Value(CpIndex<CONSTANT_CP_Info> element) {
		CONSTANT_CP_Info obj = element.getCpObject();
		if(obj instanceof CONSTANT_Integer || obj instanceof CONSTANT_Long || obj instanceof CONSTANT_Float
				|| obj instanceof CONSTANT_Double || obj instanceof CONSTANT_Utf8) {
			return element;
		}
		throw new IllegalArgumentException("element value constant index is not a valid type: " + obj.getClass());
	}


	public static final CpIndex<CONSTANT_CP_Info> getBootstrap_ArgumentType(DataInput in, ClassFile resolver) throws IOException {
		short  argIndex = in.readShort();
		CpIndex<CONSTANT_CP_Info> cpIndex = resolver.getCpIndex(argIndex);
		CONSTANT_CP_Info obj = cpIndex.getCpObject();
		checkBootstrapArgumentType(obj);
		return cpIndex;
	}


	public static final CONSTANT_CP_Info checkBootstrapArgumentType(CONSTANT_CP_Info obj) {
		if(obj instanceof CONSTANT_String || obj instanceof CONSTANT_Class || obj instanceof CONSTANT_Integer
				|| obj instanceof CONSTANT_Long || obj instanceof CONSTANT_Float || obj instanceof CONSTANT_Double
				|| obj instanceof CONSTANT_MethodHandle || obj instanceof CONSTANT_MethodType) {
			return obj;
		}
		throw new IllegalArgumentException("constant pool boostrap argument type index not valid type: " + obj.getClass());
	}

}
