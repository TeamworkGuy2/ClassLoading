package twg2.jbcm.classFormat.constantPool;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.Settings;
import twg2.jbcm.modify.IndexUtility;

/** Java class file format constant pool <code>MethodType reference</code> info type.<br>
 * Constant value = 16, class version = 51.0, Java SE = 7
 * @author TeamworkGuy2
 * @since 2013-10-6
 */
public class CONSTANT_MethodType implements CONSTANT_CP_Info {
	public static final byte TAG = 16;
	ClassFile resolver;

	/** The value of the descriptor_index item must be a valid index into the constant_pool table.
	 * The constant_pool entry at that index must be a CONSTANT_Utf8_info (§4.4.7) structure
	 * representing a method descriptor (§4.3.3). 
	 */
	CpIndex<CONSTANT_Utf8> descriptor_index;


	public CONSTANT_MethodType(ClassFile resolver) {
		this.resolver = resolver;
	}


	@Override
	public int getTag() {
		return TAG;
	}


	@Override
	public void changeCpIndex(short oldIndex, short newIndex) {
		IndexUtility.indexChange(descriptor_index, oldIndex, newIndex);
	}


	public CONSTANT_Utf8 getDescriptor() {
		return descriptor_index.getCpObject();
	}


	public void setDescriptorIndex(CpIndex<CONSTANT_Utf8> index) {
		this.descriptor_index = index;
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		out.writeByte(TAG);
		descriptor_index.writeData(out);
	}


	@Override
	public void readData(DataInput in) throws IOException {
		if(!Settings.cpTagRead) {
			int tagV = in.readByte();
			if(tagV != TAG) { throw new IllegalStateException("Illegal CONSTANT_MethodType tag: " + tagV); }
		}
		descriptor_index = resolver.getExpectCpIndex(in.readShort(), CONSTANT_Utf8.class);
	}


	@Override
	public String toShortString() {
		return toString();
	}


	@Override
	public String toString() {
		return "MethodType(16, descriptor=" + descriptor_index.getCpObject() + ")";
	}

}
