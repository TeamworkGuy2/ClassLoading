package twg2.jbcm.classFormat.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.Settings;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Utf8;
import twg2.jbcm.modify.IndexUtility;

/** A Java class file format Attribute of type <code>SourceDebugExtension</code>
 * @author TeamworkGuy2
 * @since 2013-12-3
 */
public class SourceDebugExtension implements Attribute_Type {
	public static final String ATTRIBUTE_NAME = "SourceDebugExtension";
	ClassFile resolver;
	/** The value of the attribute_name_index item must be a valid index into the constant_pool table.
	 * The constant_pool entry at that index must be a CONSTANT_Utf8_info (§4.4.7) structure
	 * representing the string "SourceDebugExtension".
	 */
	CpIndex<CONSTANT_Utf8> attribute_name_index;
	/** The value of the attribute_length item indicates the length of the attribute, excluding the initial six bytes.
	 * The value of the attribute_length item is thus the number of bytes in the debug_extension[] item. 
	 */
	int attribute_length;
	/** The debug_extension array holds extended debugging information which has no semantic effect
	 * on the Java Virtual Machine. The information is represented using a modified UTF-8 string (§4.4.7)
	 * with no terminating zero byte.
	 * Note that the debug_extension array may denote a string longer than that which can be
	 * represented with an instance of class String. 
	 */
	byte[] debug_extension;


	public SourceDebugExtension(ClassFile resolver, short attributeNameIndex) {
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
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		attribute_name_index.writeData(out);
		out.writeInt(attribute_length);
		out.write(debug_extension, 0, attribute_length);
	}


	@Override
	public void readData(DataInput in) throws IOException {
		if(Settings.doReadAttributeName()) {
			attribute_name_index = Settings.readAttributeNameIndex(in, resolver, ATTRIBUTE_NAME);
		}
		attribute_length = in.readInt();
		debug_extension = new byte[attribute_length];
		in.readFully(debug_extension, 0, attribute_length);
	}


	@Override
	public String toString() {
		return ATTRIBUTE_NAME + "(data=" + Arrays.toString(debug_extension) + ")";
	}

}
