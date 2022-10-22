package twg2.jbcm.classFormat.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.ClassFileAttributes;
import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.Settings;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Utf8;
import twg2.jbcm.modify.CpIndexChanger;

/** A Java class file format Attribute of type <code>Signature</code>
 * @author TeamworkGuy2
 * @since 2013-12-3
 */
public class Signature implements Attribute_Type {
	public static final String ATTRIBUTE_NAME = "Signature";
	ClassFile resolver;
	/** The value of the attribute_name_index item must be a valid index into the constant_pool table.
	 * The constant_pool entry at that index must be a CONSTANT_Utf8_info (§4.4.7) structure
	 * representing the string "Signature".
	 */
	CpIndex<CONSTANT_Utf8> attribute_name_index;
	/** The value of the attribute_length item of a Signature_attribute structure must be 2. */
	int attribute_length;
	/** The value of the signature_index item must be a valid index into the constant_pool table.
	 * The constant pool entry at that index must be a CONSTANT_Utf8_info (§4.4.7) structure
	 * representing a class signature (§4.3.4) if this Signature attribute is an attribute
	 * of a ClassFile structure; a method signature if this Signature attribute is an
	 * attribute of a method_info structure; or a field type signature otherwise.
	 */
	CpIndex<CONSTANT_Utf8> signature_index;


	public Signature(ClassFile resolver, short attributeNameIndex) {
		this.attribute_name_index = resolver.getAttributeNameIndex(attributeNameIndex);
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
	public void changeCpIndex(CpIndexChanger indexChanger) {
		indexChanger.indexChange(attribute_name_index);
		indexChanger.indexChange(signature_index);
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		attribute_name_index.writeData(out);
		out.writeInt(attribute_length);
		signature_index.writeData(out);
	}


	@Override
	public void readData(DataInput in) throws IOException {
		if(Settings.readAttributeName) {
			attribute_name_index = ClassFileAttributes.readAttributeNameIndex(in, resolver, ATTRIBUTE_NAME);
		}
		attribute_length = in.readInt();
		signature_index = resolver.getCheckCpIndex(in.readShort(), CONSTANT_Utf8.class);
	}


	@Override
	public String toString() {
		return ATTRIBUTE_NAME + "(signature=" + signature_index.getCpObject().getString() + ")";
	}

}
