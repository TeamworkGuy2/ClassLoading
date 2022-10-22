package twg2.jbcm.classFormat.constantPool;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.Settings;
import twg2.jbcm.modify.CpIndexChanger;

/** Java class file format constant pool <code>Double</code> info type.<br>
 * Constant value = 6, class version = 45.3, Java SE = 1.0.2
 * @author TeamworkGuy2
 * @since 2013-7-7
 */
public class CONSTANT_Double implements CONSTANT_CP_Info {
	public static final int TAG = 6;
	ClassFile resolver;

	/** The high_bytes and low_bytes items of the CONSTANT_Double_info structure together represent the double value in IEEE 754
	 * floating-point double format (§2.3.2). The bytes of each item are stored in big-endian (high byte first) order.<br>
	 * The value represented by the CONSTANT_Double_info structure is determined as follows. The high_bytes and
	 * low_bytes items are first converted into the long constant bits, which is equal
	 * to ((long) high_bytes << 32) + low_bytes. Then:<br>
	 * If bits is 0x7ff0000000000000L, the double value will be positive infinity.<br>
	 * If bits is 0xfff0000000000000L, the double value will be negative infinity.<br>
	 * If bits is in the range 0x7ff0000000000001L through 0x7fffffffffffffffL or in the
	 * range 0xfff0000000000001L through 0xffffffffffffffffL, the double value will be NaN.<br>
	 * In all other cases, let s, e, and m be three values that might be computed from bits:<br>
	 * int s = ((bits >> 63) == 0) ? 1 : -1;<br>
	 * int e = (int)((bits >> 52) & 0x7ffL);<br>
	 * long m = (e == 0) ? (bits & 0xfffffffffffffL) << 1 : (bits & 0xfffffffffffffL) | 0x10000000000000L;<br>
	 * Then the floating-point value equals the double value of the mathematical expression s·m·2e-1075.
	 */
	int high_bytes;
	int low_bytes;


	public CONSTANT_Double(ClassFile resolver) {
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
		out.writeInt(high_bytes);
		out.writeInt(low_bytes);
	}


	@Override
	public void readData(DataInput in) throws IOException {
		if(!Settings.cpTagRead) {
			int tagV = in.readByte();
			if(tagV != TAG) { throw new IllegalStateException("Illegal CONSTANT_Double tag: " + tagV); }
		}
		high_bytes = in.readInt();
		low_bytes = in.readInt();
	}


	@Override
	public String toShortString() {
		return Double.toString(Double.longBitsToDouble((long)high_bytes << 32 + low_bytes));
	}


	@Override
	public String toString() {
		return "Double(6, " + Double.longBitsToDouble((long)high_bytes << 32 + low_bytes) + ")";
	}

}