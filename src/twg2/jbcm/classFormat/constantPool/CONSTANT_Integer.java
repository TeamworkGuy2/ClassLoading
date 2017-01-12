package twg2.jbcm.classFormat.constantPool;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.Settings;

/** Java class file format constant pool <code>Integer</code> info type
 * @author TeamworkGuy2
 * @since 2013-7-7
 */
public class CONSTANT_Integer implements CONSTANT_CP_Info {
	public static final int CONSTANT_Integer_info = 3;
	ClassFile resolver;

	byte tag = CONSTANT_Integer_info;
	int bytes;


	public CONSTANT_Integer(ClassFile resolver) {
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
		out.write(CONSTANT_Integer_info);
		out.writeInt(bytes);
	}


	@Override
	public void readData(DataInput in) throws IOException {
		if(!Settings.cpTagRead) {
			int tagV = in.readByte();
			if(tagV != CONSTANT_Integer_info) { throw new IllegalStateException("Illegal CONSTANT_Integer tag: " + tagV); }
		}
		bytes = in.readInt();
	}


	@Override
	public String toString() {
		return "CONSTANT_Integer(3, " + bytes + ")";
	}

}