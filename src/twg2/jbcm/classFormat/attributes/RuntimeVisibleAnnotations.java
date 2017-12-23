package twg2.jbcm.classFormat.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.Settings;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Utf8;
import twg2.jbcm.modify.IndexUtility;

/** A Java class file format Attribute of type <code>RuntimeVisibleAnnotations</code>
 * @author TeamworkGuy2
 * @since 2013-12-3
 */
public class RuntimeVisibleAnnotations implements Attribute_Type {
	public static final String ATTRIBUTE_NAME = "RuntimeVisibleAnnotations";
	ClassFile resolver;
	/** The value of the attribute_name_index item must be a valid index into the constant_pool table.
	 * The constant_pool entry at that index must be a CONSTANT_Utf8_info (§4.4.7) structure
	 * representing the string "RuntimeVisibleAnnotations".
	 */
	CpIndex<CONSTANT_Utf8> attribute_name_index;
	/** The value of the attribute_length indicates the length of the attribute, excluding the initial six bytes.
	 * The value of the attribute_length item is thus dependent on the number of run-time-invisible
	 * annotations represented by the structure, and their values.
	 */
	int attribute_length;
	/** The value of the num_annotations item gives the number of run-time-invisible annotations
	 * represented by the structure.
	 * Note that a maximum of 65535 run-time-invisible Java programming language annotations may be
	 * directly attached to a program element.
	 */
	short num_annotations;
	/** Each value of the annotations table represents a single run-time-visible annotation on a program element.
	 */
	Annotation[] annotations;


	public RuntimeVisibleAnnotations(ClassFile resolver, short attributeNameIndex) {
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
		IndexUtility.indexChange(annotations, oldIndex, newIndex);
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		attribute_name_index.writeData(out);
		out.writeInt(attribute_length);
		out.writeShort(num_annotations);
		for(int i = 0; i < num_annotations; i++) {
			annotations[i].writeData(out);
		}
	}


	@Override
	public void readData(DataInput in) throws IOException {
		if(Settings.doReadAttributeName()) {
			attribute_name_index = Settings.readAttributeNameIndex(in, resolver, ATTRIBUTE_NAME);
		}
		attribute_length = in.readInt();
		num_annotations = in.readShort();
		annotations = new Annotation[num_annotations];
		for(int i = 0; i < num_annotations; i++) {
			annotations[i] = new Annotation(resolver);
			annotations[i].readData(in);
		}
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(ATTRIBUTE_NAME);
		sb.append("([");
		for(int i = 0; i < num_annotations - 1; i++) {
			sb.append(annotations[i]);
			sb.append(", ");
		}
		if(num_annotations > 0) { sb.append(annotations[num_annotations - 1]); }
		sb.append("])");
		return sb.toString();
	}

}
