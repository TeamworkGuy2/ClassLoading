package twg2.jbcm.classFormat.constantPool;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.Settings;
import twg2.jbcm.modify.IndexUtility;

/** Java class file format constant pool <code>NameAndType</code> info type
 * @author TeamworkGuy2
 * @since 2013-7-7
 */
public class CONSTANT_NameAndType implements CONSTANT_CP_Info {
	public static final byte TAG = 12;
	ClassFile resolver;

	/** The value of the name_index item must be a valid index into the constant_pool table. The constant_pool entry
	 * at that index must be a CONSTANT_Utf8_info (§4.4.7) structure representing either a valid field or
	 * method name (§2.7) stored as a simple name (§2.7.1), that is, as a Java programming language
	 * identifier (§2.2) or as the special method name <init> (§3.9).
	 */	
	CpIndex<CONSTANT_Utf8> name_index;
	/** The value of the descriptor_index item must be a valid index into the constant_pool table. The constant_pool
	 * entry at that index must be a CONSTANT_Utf8_info (§4.4.7) structure representing a valid
	 * field descriptor (§4.3.2) or method descriptor (§4.3.3).
	 */
	CpIndex<CONSTANT_Utf8> descriptor_index;


	public CONSTANT_NameAndType(ClassFile resolver) {
		this.resolver = resolver;
	}


	@Override
	public int getTag() {
		return TAG;
	}


	@Override
	public void changeCpIndex(short oldIndex, short newIndex) {
		IndexUtility.indexChange(name_index, oldIndex, newIndex);
		IndexUtility.indexChange(descriptor_index, oldIndex, newIndex);
	}


	public CONSTANT_Utf8 getName() {
		return name_index.getCpObject();
	}


	public CONSTANT_Utf8 getDescriptor() {
		return descriptor_index.getCpObject();
	}


	public void setNameIndex(CpIndex<CONSTANT_Utf8> index) {
		this.name_index = index;
	}


	public void setDescriptorIndex(CpIndex<CONSTANT_Utf8> index) {
		this.descriptor_index = index;
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		out.writeByte(TAG);
		name_index.writeData(out);
		descriptor_index.writeData(out);
	}


	@Override
	public void readData(DataInput in) throws IOException {
		if(!Settings.cpTagRead) {
			int tagV = in.readByte();
			if(tagV != TAG) { throw new IllegalStateException("Illegal CONSTANT_NameAndType tag: " + tagV); }
		}
		name_index = resolver.getCheckCpIndex(in.readShort(), CONSTANT_Utf8.class);
		descriptor_index = resolver.getCheckCpIndex(in.readShort(), CONSTANT_Utf8.class);
	}


	@Override
	public String toString() {
		return "NameAndType(12, name=" + name_index.getCpObject() + ", descriptor=" + descriptor_index.getCpObject() + ")";
	}

}
