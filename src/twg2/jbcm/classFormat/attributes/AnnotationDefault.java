package twg2.jbcm.classFormat.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.Settings;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Utf8;
import twg2.jbcm.modify.IndexUtility;

/** A Java class file format Attribute of type <code>AnnotationDefault</code>
 * @author TeamworkGuy2
 * @since 2013-12-3
 */
public class AnnotationDefault implements Attribute_Type {
	public static final String ATTRIBUTE_NAME = "AnnotationDefault";
	ClassFile resolver;
	/* The value of the attribute_name_index item must be a valid index into the constant_pool table. The constant_pool
	 * entry at that index must be a CONSTANT_Utf8_info (§4.4.7) structure representing the string "AnnotationDefault".
	 */
	CpIndex<CONSTANT_Utf8> attribute_name_index;
	// The value of the attribute_length indicates the length of the attribute, excluding the initial six bytes.
	int attribute_length;
	/* The default_value item represents the default value of the annotation type element whose default
	 * value is represented by this AnnotationDefault attribute. 
	 */
	Element_Value default_value;


	public AnnotationDefault(ClassFile resolver, short attributeNameIndex) {
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
		IndexUtility.indexChange(default_value, oldIndex, newIndex);
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		attribute_name_index.writeData(out);
		out.writeInt(attribute_length);
		default_value.writeData(out);
	}


	@Override
	public void readData(DataInput in) throws IOException {
		if(Settings.doReadAttributeName()) {
			attribute_name_index = Settings.readAttributeNameIndex(in, resolver, ATTRIBUTE_NAME);
		}
		attribute_length = in.readInt();
		default_value = new Element_Value(resolver);
		default_value.readData(in);
	}


	@Override
	public String toString() {
		return ATTRIBUTE_NAME + "(element_value=" + default_value + ")";
	}

}
