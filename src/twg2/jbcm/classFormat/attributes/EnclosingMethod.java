package twg2.jbcm.classFormat.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.Settings;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Class;
import twg2.jbcm.classFormat.constantPool.CONSTANT_NameAndType;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Utf8;
import twg2.jbcm.modify.IndexUtility;

/** A Java class file format Attribute of type <code>EnclosingMethod</code>
 * @author TeamworkGuy2
 * @since 2013-12-3
 */
public class EnclosingMethod implements Attribute_Type {
	public static final String ATTRIBUTE_NAME = "EnclosingMethod";
	ClassFile resolver;
	/** The value of the attribute_name_index item must be a valid index into the constant_pool table.
	 * The constant_pool entry at that index must be a CONSTANT_Utf8_info (§4.4.7) structure
	 * representing the string "EnclosingMethod".
	 */
	CpIndex<CONSTANT_Utf8> attribute_name_index;
	/** The value of the attribute_length item is four. */
	int attribute_length;
	/** The value of the class_index item must be a valid index into the constant_pool table.
	 * The constant_pool entry at that index must be a CONSTANT_Class_info (§4.4.1) structure
	 * representing the innermost class that encloses the declaration of the current class.
	 */
	CpIndex<CONSTANT_Class> class_index;
	/** If the current class is not immediately enclosed by a method or constructor, then the value
	 * of the method_index item must be zero.
	 * Otherwise, the value of the method_index item must be a valid index into the constant_pool table.
	 * The constant_pool entry at that index must be a CONSTANT_NameAndType_info structure (§4.4.6) representing
	 * the name and type of a method in the class referenced by the class_index attribute above.
	 * It is the responsibility of a Java compiler to ensure that the method identified via the method_index is
	 * indeed the closest lexically enclosing method of the class that contains this EnclosingMethod attribute.
	 */
	CpIndex<CONSTANT_NameAndType> method_index;


	public EnclosingMethod(ClassFile resolver, short attributeNameIndex) {
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
		IndexUtility.indexChange(class_index, oldIndex, newIndex);
		IndexUtility.indexChange(method_index, oldIndex, newIndex);
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		attribute_name_index.writeData(out);
		out.writeInt(attribute_length);
		class_index.writeData(out);
		method_index.writeData(out);
	}


	@Override
	public void readData(DataInput in) throws IOException {
		if(Settings.doReadAttributeName()) {
			attribute_name_index = Settings.readAttributeNameIndex(in, resolver, ATTRIBUTE_NAME);
		}
		attribute_length = in.readInt();
		class_index = resolver.getCheckCpIndex(in.readShort(), CONSTANT_Class.class);
		method_index = resolver.getCheckCpIndex(in.readShort(), CONSTANT_NameAndType.class, true);
	}


	@Override
	public String toString() {
		return ATTRIBUTE_NAME + "(class=" + class_index.getCpObject() +
				", method=" + (method_index.getIndex() > 0 ? method_index.getCpObject() : "null") + ")";
	}

}
