package twg2.jbcm.classFormat.constantPool;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.Settings;
import twg2.jbcm.modify.IndexUtility;

/** Java class file format constant pool <code>Module</code> info type
 * @author TeamworkGuy2
 * @since 2017-12-22
 */
public class CONSTANT_Module implements CONSTANT_CP_Info {
	public static final byte TAG = 19;
	ClassFile resolver;

	/** The value of the name_index item must be a valid index into the constant_pool table. The constant_pool entry
	 * at that index must be a CONSTANT_Utf8_info structure representing a valid module name (§4.2.3).
	 */
	CpIndex<CONSTANT_Utf8> name_index;


	public CONSTANT_Module(ClassFile resolver) {
		this.resolver = resolver;
	}


	@Override
	public int getTag() {
		return TAG;
	}


	@Override
	public void changeCpIndex(short oldIndex, short newIndex) {
		IndexUtility.indexChange(name_index, oldIndex, newIndex);
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
			if(tagV != TAG) { throw new IllegalStateException("Illegal CONSTANT_Module tag: " + tagV); }
		}
		name_index = resolver.getCheckCpIndex(in.readShort(), CONSTANT_Utf8.class);
	}


	@Override
	public String toString() {
		CONSTANT_Utf8 name = name_index.getCpObject();
		return "Module(19, name=" + name.getString() + ")";
	}

}
