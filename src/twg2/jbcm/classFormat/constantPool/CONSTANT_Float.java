package twg2.jbcm.classFormat.constantPool;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.Settings;
import twg2.jbcm.modify.CpIndexChanger;

/** Java class file format constant pool <code>Float</code> info type.<br>
 * Constant value = 4, class version = 45.3, Java SE = 1.0.2
 * @author TeamworkGuy2
 * @since 2013-7-7
 */
public class CONSTANT_Float implements CONSTANT_CP_Info {
	public static final byte TAG = 4;
	ClassFile resolver;

	/** The bytes item of the CONSTANT_Float_info structure represents the value of the float constant in IEEE 754 floating-point single format (§2.3.2).
	 * The bytes of the single format representation are stored in big-endian (high byte first) order.<br>
	 * The value represented by the CONSTANT_Float_info structure is determined as follows. The bytes of the value are
	 * first converted into an int constant bits. Then:<br>
	 * If bits is 0x7f800000, the float value will be positive infinity.<br>
	 * If bits is 0xff800000, the float value will be negative infinity.<br>
	 * If bits is in the range 0x7f800001 through 0x7fffffff or in the range 0xff800001 through 0xffffffff,
	 * the float value will be NaN.<br>
	 * In all other cases, let s, e, and m be three values that might be computed from bits:<br>
	 * 	int s = ((bits >> 31) == 0) ? 1 : -1;<br>
	 * 	int e = ((bits >> 23) & 0xff);<br>
	 * 	int m = (e == 0) ? (bits & 0x7fffff) << 1 : (bits & 0x7fffff) | 0x800000;<br>
	 * Then the float value equals the result of the mathematical expression s·m·2e-150.
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
	public void changeCpIndex(CpIndexChanger indexChanger) {
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
	public String toShortString() {
		return Float.toString(Float.intBitsToFloat(bytes));
	}


	@Override
	public String toString() {
		return "Float(4, " + Float.intBitsToFloat(bytes) + ")";
	}

}