package twg2.jbcm.classFormat.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.Settings;
import twg2.jbcm.classFormat.constantPool.CONSTANT_CP_Info;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Utf8;
import twg2.jbcm.modify.IndexUtility;

/** A Java class file format Attribute of type <code>ConstantValue</code>
 * @author TeamworkGuy2
 * @since 2013-7-7
 */
public class ConstantValue implements Attribute_Type {
	public static final String ATTRIBUTE_NAME = "ConstantValue";
	ClassFile resolver;
	/** The value of the attribute_name_index item must be a valid index into the constant_pool table. The constant_pool
	 * entry at that index must be a CONSTANT_Utf8_info (§4.4.7) structure representing the string "ConstantValue".
	 */
	CpIndex<CONSTANT_Utf8> attribute_name_index;
	/** The value of the attribute_length item of a ConstantValue_attribute structure must be 2. */
	int attribute_length;
	/** The value of the constantvalue_index item must be a valid index into the constant_pool table. The constant_pool
	 * entry at that index gives the constant value represented by this attribute. The constant_pool entry must be of a
	 * type appropriate to the field, as shown by Table 4.6.
	 * Field Type 	Entry Type
	 * long 	CONSTANT_Long
	 * float 	CONSTANT_Float
	 * double 	CONSTANT_Double
	 * int, short, char, byte, boolean 	CONSTANT_Integer
	 * String 	CONSTANT_String
	 */
	CpIndex<? extends CONSTANT_CP_Info> constantvalue_index;


	public ConstantValue(ClassFile resolver, short attributeNameIndex) {
		this.attribute_name_index = Settings.initAttributeNameIndex(attributeNameIndex, resolver);
		this.resolver = resolver;
	}


	@Override
	public String getAttributeName() {
		return attribute_name_index.getCpObject().getString();
	}


	@Override
	public int getAttributeLength() {
		return attribute_length;
	}


	@Override
	public void changeCpIndex(short oldIndex, short newIndex) {
		IndexUtility.indexChange(attribute_name_index, oldIndex, newIndex);
		IndexUtility.indexChange(constantvalue_index, oldIndex, newIndex);
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		attribute_name_index.writeData(out);
		out.writeInt(attribute_length);
		constantvalue_index.writeData(out);
	}


	@Override
	public void readData(DataInput in) throws IOException {
		if(Settings.doReadAttributeName()) {
			attribute_name_index = Settings.readAttributeNameIndex(in, resolver, ATTRIBUTE_NAME);
		}
		attribute_length = in.readInt();
		if(attribute_length != 2) {
			throw new IllegalStateException(ATTRIBUTE_NAME + " attribute length " + attribute_length + " expected to equal " + 2);
		}
		constantvalue_index = Settings.readCpConstantIndex(in, resolver);
	}


	@Override
	public String toString() {
		return ATTRIBUTE_NAME + "(index=" + constantvalue_index + ", value=" + constantvalue_index.getCpObject() + ")";
	}

}
