package twg2.jbcm.classFormat.constantPool;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.Settings;
import twg2.jbcm.modify.IndexUtility;

/** Java class file format constant pool <code>String</code> info type.<br>
 * Constant value = 8, class version = 45.3, Java SE = 1.0.2
 * @author TeamworkGuy2
 * @since 2013-7-7
 */
public class CONSTANT_String implements CONSTANT_CP_Info {
	public static final byte TAG = 8;
	ClassFile resolver;

	/** The value of the string_index item must be a valid index into the constant_pool table. The constant_pool entry
	 * at that index must be a CONSTANT_Utf8_info (§4.4.7) structure representing the sequence of characters to which
	 * the String object is to be initialized.
	 */
	CpIndex<CONSTANT_Utf8> string_index;


	public CONSTANT_String(ClassFile resolver) {
		this.resolver = resolver;
	}


	@Override
	public int getTag() {
		return TAG;
	}


	@Override
	public void changeCpIndex(short oldIndex, short newIndex) {
		IndexUtility.indexChange(string_index, oldIndex, newIndex);
	}


	public CONSTANT_Utf8 getString() {
		return string_index.getCpObject();
	}


	public void setStringIndex(CpIndex<CONSTANT_Utf8> index) {
		this.string_index = index;
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		out.writeByte(TAG);
		string_index.writeData(out);
	}


	@Override
	public void readData(DataInput in) throws IOException {
		if(!Settings.cpTagRead) {
			int tagV = in.readByte();
			if(tagV != TAG) { throw new IllegalStateException("Illegal CONSTANT_String tag: " + tagV); }
		}
		string_index = resolver.getExpectCpIndex(in.readShort(), CONSTANT_Utf8.class);
	}


	@Override
	public String toShortString() {
		return string_index.getCpObject().toShortString();
	}


	@Override
	public String toString() {
		return "String(8, index=" + string_index + ", string=" + string_index.getCpObject() + ")";
	}

}
