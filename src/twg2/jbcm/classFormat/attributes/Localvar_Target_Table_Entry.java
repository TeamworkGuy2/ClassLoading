package twg2.jbcm.classFormat.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ReadWritable;

/** A member of the {@link TypeAnnotation} type and used in {@link Target_Info_Type}
 * by {@link Target_Info_Type.Localvar_Target}.
 * Indicates a range of code array offsets within which a local variable has a value. It also
 * indicates the index into the local variable array of the current frame at which that local
 * variable can be found.
 * @author TeamworkGuy2
 * @since 2014-3-19
 */
public class Localvar_Target_Table_Entry implements ReadWritable, Code_Attribute {
	/** The given local variable has a value at indices into the code array in the
	 * interval [start_pc, start_pc + length), that is, between start_pc inclusive
	 * and start_pc + length exclusive. */
	short start_pc;
	short length;
	/** The given local variable must be at index in the local variable array of the current frame.
	 * If the local variable at index is of type double or long, it occupies both index and index + 1. */
	short index;


	@Override
	public void changeCpIndex(short oldIndex, short newIndex) {
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		out.writeShort(start_pc);
		out.writeShort(length);
		out.writeShort(index);
	}


	@Override
	public void readData(DataInput in) throws IOException {
		start_pc = in.readShort();
		length = in.readShort();
		index = in.readShort();
	}

}
