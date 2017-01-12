package twg2.jbcm.classFormat.constantPool;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.Settings;
import twg2.jbcm.modify.IndexUtility;

/** Java class file format constant pool <code>Class</code> info type
 * @author TeamworkGuy2
 * @since 2013-7-7
 */
public class CONSTANT_Class implements CONSTANT_CP_Info {
	public static final int CONSTANT_Class_info = 7;
	ClassFile resolver;

	byte tag = CONSTANT_Class_info;
	/* The value of the name_index item must be a valid index into the constant_pool table.
	 * The constant_pool entry at that index must be a CONSTANT_Utf8_info (§4.4.7) structure
	 * representing a valid fully qualified class or interface name (§2.8.1) encoded in internal form (§4.2). 
	 */
	CpIndex<CONSTANT_Utf8> name_index;


	public CONSTANT_Class(ClassFile resolver) {
		this.resolver = resolver;
	}


	@Override
	public int getTag() {
		return tag;
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
		out.write(CONSTANT_Class_info);
		name_index.writeData(out);
	}


	@Override
	public void readData(DataInput in) throws IOException {
		if(!Settings.cpTagRead) {
			int tagV = in.readByte();
			if(tagV != CONSTANT_Class_info) { throw new IllegalStateException("illegal CONSTANT_Class tag: " + tagV); }
		}
		name_index = resolver.getCheckCpIndex(in.readShort(), CONSTANT_Utf8.class);
	}


	@Override
	public String toString() {
		return "CONSTANT_Class(7, name=" + name_index.getCpObject() + ")";
	}

}
