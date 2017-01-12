package twg2.jbcm.classFormat.constantPool;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.Settings;

/** Java class file format constant pool <code>Long</code> info type
 * @author TeamworkGuy2
 * @since 2013-7-7
 */
public class CONSTANT_Long implements CONSTANT_CP_Info {
	public static final int CONSTANT_Long_info = 5;
	ClassFile resolver;

	byte tag = CONSTANT_Long_info;
	int high_bytes;
	int low_bytes;


	public CONSTANT_Long(ClassFile resolver) {
		this.resolver = resolver;
	}


	@Override
	public int getTag() {
		return tag;
	}


	@Override
	public void changeCpIndex(short oldIndex, short newIndex) {
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		out.write(CONSTANT_Long_info);
		out.writeInt(high_bytes);
		out.writeInt(low_bytes);
	}


	@Override
	public void readData(DataInput in) throws IOException {
		if(!Settings.cpTagRead) {
			int tagV = in.readByte();
			if(tagV != CONSTANT_Long_info) { throw new IllegalStateException("Illegal CONSTANT_Long tag: " + tagV); }
		}
		high_bytes = in.readInt();
		low_bytes = in.readInt();
	}


	@Override
	public String toString() {
		return "CONSTANT_Long(5, " + high_bytes + ", " + low_bytes + "=" + ((long)high_bytes << 32 | (long)low_bytes) + ")";
	}

}