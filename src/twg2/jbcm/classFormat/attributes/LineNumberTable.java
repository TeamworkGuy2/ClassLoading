package twg2.jbcm.classFormat.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.ClassFileAttributes;
import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.ReadWritable;
import twg2.jbcm.classFormat.Settings;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Utf8;
import twg2.jbcm.modify.IndexUtility;

/** A Java class file format Attribute of type <code>LineNumberTable</code>
 * @author TeamworkGuy2
 * @since 2013-7-7
 */
public class LineNumberTable implements Attribute_Type {
	public static final String ATTRIBUTE_NAME = "LineNumberTable";
	ClassFile resolver;
	Code parent;
	/* The value of the attribute_name_index item must be a valid index into the constant_pool table. The constant_pool
	 * entry at that index must be a CONSTANT_Utf8_info (§4.4.7) structure representing the string "LineNumberTable".
	 */
	CpIndex<CONSTANT_Utf8> attribute_name_index;
	// The value of the attribute_length item indicates the attribute length, excluding the initial six bytes.
	int attribute_length;
	// The value of the line_number_table_length item indicates the number of entries in the line_number_table array.
	short line_number_table_length;
	/* Each entry in the line_number_table array indicates that the line number in the original source file changes at
	 * a given point in the code array, size [line_number_table_length], 0 indexed.
	 */
	LineNumberPoint[] line_number_table;


	public LineNumberTable(ClassFile resolver, Code codeParent, short attributeNameIndex) {
		this.attribute_name_index = resolver.getAttributeNameIndex(attributeNameIndex);
		this.resolver = resolver;
		this.parent = codeParent;
	}


	@Override
	public String getAttributeName() {
		return attribute_name_index.getCpObject().getString();
	}


	@Override
	public int getAttributeLength() {
		return attribute_length;
	}


	@Override
	public void changeCpIndex(short oldIndex, short newIndex) {
		IndexUtility.indexChange(attribute_name_index, oldIndex, newIndex);
		IndexUtility.indexChange(line_number_table, oldIndex, newIndex);
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		attribute_name_index.writeData(out);
		out.writeInt(attribute_length);
		out.writeShort(line_number_table_length);
		for(int i = 0; i < line_number_table_length; i++) {
			line_number_table[i].writeData(out);
		}
	}


	@Override
	public void readData(DataInput in) throws IOException {
		if(Settings.readAttributeName) {
			attribute_name_index = ClassFileAttributes.readAttributeNameIndex(in, resolver, ATTRIBUTE_NAME);
		}
		attribute_length = in.readInt();
		line_number_table_length = in.readShort();
		line_number_table = new LineNumberPoint[line_number_table_length];
		for(int i = 0; i < line_number_table_length; i++) {
			line_number_table[i] = new LineNumberPoint(parent);
			line_number_table[i].readData(in);
		}
	}


	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append(ATTRIBUTE_NAME + "(line numbers: " + line_number_table_length + " [");
		for(int i = 0; i < line_number_table_length - 1; i++) {
			str.append(line_number_table[i] + ", ");
		}
		if(line_number_table_length > 0) { str.append(line_number_table[line_number_table_length - 1]); }
		str.append("])");
		return str.toString();
	}



	/** Java class file format Code Attribute for {@code LineNumberTable}
	 * @author TeamworkGuy2
	 * @since 2013-7-7
	 */
	public static class LineNumberPoint implements ReadWritable {
		Code parent;
		/** Each entry in the line_number_table array indicates that the line number in the original source file changes at
		 * a given point in the code array. Each line_number_table entry must contain the following two items:
		 * start_pc
		 * The value of the start_pc item must indicate the index into the code array at which the code for a new line
		 * in the original source file begins. The value of start_pc must be less than the value of the code_length item
		 * of the Code attribute of which this LineNumberTable is an attribute.
		 */
		short start_pc;
		/** line_number
		 * The value of the line_number item must give the corresponding line number in the original source file.
		 */
		short line_number;


		public LineNumberPoint(Code codeParent) {
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

}
