package twg2.jbcm.classFormat.constantPool;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.Settings;
import twg2.jbcm.modify.CpIndexChanger;

/** Java class file format constant pool <code>Class</code> info type.<br>
 * Constant value = 7, class version = 45.3, Java SE = 1.0.2
 * @author TeamworkGuy2
 * @since 2013-7-7
 */
public class CONSTANT_Class implements CONSTANT_CP_Info {
	public static final byte TAG = 7;
	ClassFile resolver;

	/** The value of the name_index item must be a valid index into the constant_pool table.
	 * The constant_pool entry at that index must be a CONSTANT_Utf8_info (§4.4.7) structure
	 * representing a valid fully qualified class or interface name (§2.8.1) encoded in internal form (§4.2).
	 */
	CpIndex<CONSTANT_Utf8> name_index;


	public CONSTANT_Class(ClassFile resolver) {
		this.resolver = resolver;
	}


	@Override
	public int getTag() {
		return TAG;
	}


	@Override
	public void changeCpIndex(CpIndexChanger indexChanger) {
		indexChanger.indexChange(name_index);
	}


	public CONSTANT_Utf8 getName() {
		return name_index.getCpObject();
	}


	public void setNameIndex(CpIndex<CONSTANT_Utf8> index) {
		this.name_index = index;
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		out.writeByte(TAG);
		name_index.writeData(out);
	}


	@Override
	public void readData(DataInput in) throws IOException {
		if(!Settings.cpTagRead) {
			int tagV = in.readByte();
			if(tagV != TAG) { throw new IllegalStateException("illegal CONSTANT_Class tag: " + tagV); }
		}
		name_index = resolver.getExpectCpIndex(in.readShort(), CONSTANT_Utf8.class);
	}


	@Override
	public String toShortString() {
		return name_index.getCpObject().toShortString();
	}


	@Override
	public String toString() {
		return "Class(7, name=" + name_index.getCpObject() + ")";
	}

}
