package twg2.jbcm.classFormat.constantPool;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.Settings;
import twg2.jbcm.modify.CpIndexChanger;

/** Java class file format constant pool <code>Package</code> info type.<br>
 * Constant value = 20, class version = 53.0, Java SE = 9<br>
 * <br>
 * A CONSTANT_Package_info structure is permitted only in the constant pool of a class file that declares a module,
 * that is, a ClassFile structure where the access_flags item has the ACC_MODULE flag set. In all other class files,
 * a CONSTANT_Package_info structure is illegal.
 * @author TeamworkGuy2
 * @since 2017-12-22
 */
public class CONSTANT_Package implements CONSTANT_CP_Info {
	public static final byte TAG = 20;
	ClassFile resolver;

	/** The value of the name_index item must be a valid index into the constant_pool table. The constant_pool entry
	 * at that index must be a CONSTANT_Utf8_info structure representing a valid package name encoded in internal form (§4.2.3).
	 */
	CpIndex<CONSTANT_Utf8> name_index;


	public CONSTANT_Package(ClassFile resolver) {
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
			if(tagV != TAG) { throw new IllegalStateException("Illegal CONSTANT_Package tag: " + tagV); }
		}
		name_index = resolver.getExpectCpIndex(in.readShort(), CONSTANT_Utf8.class);
	}


	@Override
	public String toShortString() {
		return "package " + name_index.getCpObject().toShortString();
	}


	@Override
	public String toString() {
		CONSTANT_Utf8 name = name_index.getCpObject();
		// null checks for malformed classes, for example, when a constant pool item references another item ahead of
		// it in the constant pool that hasn't been parsed yet
		return "Package(19, name=" + (name != null ? name.getString() : name_index) + ")";
	}

}
