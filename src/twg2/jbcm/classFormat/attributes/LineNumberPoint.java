package twg2.jbcm.classFormat.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ReadWritable;

/** Java class file format Code Attribute for {@code LineNumberTable}
 * @author TeamworkGuy2
 * @since 2013-7-7
 */
public class LineNumberPoint implements ReadWritable, Code_Attribute {
	Code parent;
	/* Each entry in the line_number_table array indicates that the line number in the original source file changes at
	 * a given point in the code array. Each line_number_table entry must contain the following two items:
	 * start_pc
	 * The value of the start_pc item must indicate the index into the code array at which the code for a new line
	 * in the original source file begins. The value of start_pc must be less than the value of the code_length item
	 * of the Code attribute of which this LineNumberTable is an attribute.
	 */
	short start_pc;
	/* line_number
	 * The value of the line_number item must give the corresponding line number in the original source file.
	 */
	short line_number;


	public LineNumberPoint(Code codeParent) {
		super();
		this.parent = codeParent;
	}


	@Override
	public void changeCpIndex(short oldIndex, short newIndex) {
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		out.writeShort(start_pc);
		out.writeShort(line_number);
	}


	@Override
	public void readData(DataInput in) throws IOException {
		start_pc = in.readShort();
		line_number = in.readShort();
	}


	@Override
	public String toString() {
		return "LineNumber(pc=" + start_pc + ", line=" + line_number + ")";
	}

}
