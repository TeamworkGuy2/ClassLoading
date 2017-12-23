package twg2.jbcm.classFormat.constantPool;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.Settings;

/** Java class file format constant pool <code>Float</code> info type
 * @author TeamworkGuy2
 * @since 2013-7-7
 */
public class CONSTANT_Float implements CONSTANT_CP_Info {
	public static final byte TAG = 4;
	ClassFile resolver;

	/** The bytes item of the CONSTANT_Float_info structure represents the value of the float constant in IEEE 754 floating-point single format (§2.3.2).
	 * The bytes of the single format representation are stored in big-endian (high byte first) order.
	 */
	int bytes;


	public CONSTANT_Float(ClassFile resolver) {
		this.resolver = resolver;
	}


	@Override
	public int getTag() {
		return TAG;
	}


	@Override
	public void changeCpIndex(short oldIndex, short newIndex) {
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		out.writeByte(TAG);
		out.writeInt(bytes);
	}


	@Override
	public void readData(DataInput in) throws IOException {
		if(!Settings.cpTagRead) {
			int tagV = in.readByte();
			if(tagV != TAG) { throw new IllegalStateException("Illegal CONSTANT_Float tag: " + tagV); }
		}
		bytes = in.readInt();
	}


	@Override
	public String toString() {
		return "Float(4, " + bytes + ")";
	}

}