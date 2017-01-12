package twg2.jbcm.classFormat.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.Settings;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Utf8;
import twg2.jbcm.modify.IndexUtility;

/** A Java class file format Attribute of type <code>InnerClasses</code>
 * @author TeamworkGuy2
 * @since 2013-7-7
 */
public class InnerClasses implements Attribute_Type {
	public static final String ATTRIBUTE_NAME = "InnerClasses";
	ClassFile resolver;
	/* The value of the attribute_name_index item must be a valid index into the constant_pool table. The constant_pool
	 * entry at that index must be a CONSTANT_Utf8_info (§4.4.7) structure representing the string "InnerClasses".
	 */
	CpIndex<CONSTANT_Utf8> attribute_name_index;
	// The value of the attribute_length item indicates the attribute length, excluding the initial six bytes.
	int attribute_length;
	// The value of the number_of_classes item indicates the number of entries in the classes array.
	short number_of_classes;
	/* Every CONSTANT_Class_info entry in the constant_pool table which represents a class or interface C that is not
	 * a package member must have exactly one corresponding entry in the classes array.
	 * If a class has members that are classes or interfaces, its constant_pool table
	 * (and hence its InnerClasses attribute) must refer to each such member, even if that member is not otherwise
	 * mentioned by the class. These rules imply that a nested class or interface member will have InnerClasses
	 * information for each enclosing class and for each immediate member, size [number_of_classes], 0 indexed.
	 */
	InnerClassPoint[] classes;


	public InnerClasses(ClassFile resolver, short attributeNameIndex) {
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
		IndexUtility.indexChange(classes, oldIndex, newIndex);
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		attribute_name_index.writeData(out);
		out.writeInt(attribute_length);
		out.writeShort(number_of_classes);
		for(int i = 0; i < number_of_classes; i++) {
			classes[i].writeData(out);
		}
	}


	@Override
	public void readData(DataInput in) throws IOException {
		if(Settings.doReadAttributeName()) {
			attribute_name_index = Settings.readAttributeNameIndex(in, resolver, ATTRIBUTE_NAME);
		}
		attribute_length = in.readInt();
		number_of_classes = in.readShort();
		classes = new InnerClassPoint[number_of_classes];
		for(int i = 0; i < number_of_classes; i++) {
			classes[i] = new InnerClassPoint(resolver);
			classes[i].readData(in);
		}
	}


	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append(ATTRIBUTE_NAME);
		str.append("(inner_classes[");
		for(int i = 0; i < number_of_classes-1; i++) {
			str.append(classes[i]);
			str.append(", ");
		}
		if(number_of_classes > 0) { str.append(classes[number_of_classes-1]); }
		str.append("])");
		return str.toString();
	}

}
