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

/** A Java class file format Attribute of type {@code LocalVariableTable}
 * @author TeamworkGuy2
 * @since 2013-12-3
 */
public class LocalVariableTypeTable implements Attribute_Type {
	public static final String ATTRIBUTE_NAME = "LocalVariableTypeTable";
	ClassFile resolver;
	Code parent;
	/** The value of the attribute_name_index item must be a valid index into the constant_pool table. The constant_pool
	 * entry at that index must be a CONSTANT_Utf8_info (§4.4.7) structure representing the string
	 * "LocalVariableTypeTable".
	 */
	CpIndex<CONSTANT_Utf8> attribute_name_index;
	/** The value of the attribute_length item indicates the attribute length, excluding the initial six bytes. */
	int attribute_length;
	/** The value of the local_variable_table_length item indicates the number of entries in the local_variable_table array. */
	short local_variable_type_table_length;
	/** Each entry in the local_variable_table array indicates a range of code array offsets within which a local
	 * variable has a value. It also indicates the index into the local variable array of the current frame at which
	 * that local variable can be found.
	 */
	LocalVariableTypePoint[] local_variable_type_table;


	public LocalVariableTypeTable(ClassFile resolver, Code codeParent, short attributeNameIndex) {
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
		IndexUtility.indexChange(local_variable_type_table, oldIndex, newIndex);
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		attribute_name_index.writeData(out);
		out.writeInt(attribute_length);
		out.writeShort(local_variable_type_table_length);
		for(int i = 0; i < local_variable_type_table_length; i++) {
			local_variable_type_table[i].writeData(out);
		}
	}


	@Override
	public void readData(DataInput in) throws IOException {
		if(Settings.readAttributeName) {
			attribute_name_index = ClassFileAttributes.readAttributeNameIndex(in, resolver, ATTRIBUTE_NAME);
		}
		attribute_length = in.readInt();
		local_variable_type_table_length = in.readShort();
		local_variable_type_table = new LocalVariableTypePoint[local_variable_type_table_length];
		for(int i = 0; i < local_variable_type_table_length; i++) {
			local_variable_type_table[i] = new LocalVariableTypePoint(parent);
			local_variable_type_table[i].readData(in);
		}
	}


	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append(ATTRIBUTE_NAME).append("(variables: ").append(local_variable_type_table_length).append(" [");
		for(int i = 0; i < local_variable_type_table_length-1; i++) {
			str.append(local_variable_type_table[i]).append(", ");
		}
		if(local_variable_type_table_length > 0) { str.append(local_variable_type_table[local_variable_type_table_length-1]); }
		str.append("])");
		return str.toString();
	}



	/** Java class file format Code Attribute for local variable type table
	 * @author TeamworkGuy2
	 * @since 2013-12-3
	 */
	public static class LocalVariableTypePoint implements ReadWritable {
		Code parent;
		/** Each entry in the local_variable_type_table array indicates a range of code array offsets within which a local
		 * variable has a value. It also indicates the index into the local variable array of the current frame at which
		 * that local variable can be found. Each entry must contain the following five items:
		 * start_pc, length
		 * The given local variable must have a value at indices into the code array in the interval
		 * [start_pc, start_pc+length], that is, between start_pc and start_pc+length inclusive. The value of start_pc
		 * must be a valid index into the code array of this Code attribute and must be the index of the opcode of an
		 * instruction. Either the value of start_pc+length must be a valid index into the code array of this Code
		 * attribute and be the index of the opcode of an instruction, or it must be the first index beyond the end
		 * of that code array.
		 */
		short start_pc;
		short length;
		/** The value of the name_index item must be a valid index into the constant_pool table.
		 * The constant_pool entry at that index must contain a CONSTANT_Utf8_info (§4.4.7) structure representing
		 * a valid unqualified name (§4.2.2) denoting a local variable. 
		 */
		CpIndex<CONSTANT_Utf8> name_index;
		/** The value of the signature_index item must be a valid index into the constant_pool table.
		 * The constant_pool entry at that index must contain a CONSTANT_Utf8_info structure (§4.4.7)
		 * representing a field type signature (§4.3.4) encoding the type of a local variable in the source program.
		 */
		CpIndex<CONSTANT_Utf8> signature_index;
		/** The given local variable must be at index in the local variable array of the current frame. If the local
		 * variable at index is of type double or long, it occupies both index and index+1.
		 */
		short index;


		public LocalVariableTypePoint(Code codeParent) {
			this.parent = codeParent;
		}


		@Override
		public void changeCpIndex(short oldIndex, short newIndex) {
			IndexUtility.indexChange(name_index, oldIndex, newIndex);
			IndexUtility.indexChange(signature_index, oldIndex, newIndex);
		}


		@Override
		public void writeData(DataOutput out) throws IOException {
			out.writeShort(start_pc);
			out.writeShort(length);
			name_index.writeData(out);
			signature_index.writeData(out);
			out.writeShort(index);
		}


		@Override
		public void readData(DataInput in) throws IOException {
			start_pc = in.readShort();
			length = in.readShort();
			name_index = parent.getResolver().getCheckCpIndex(in.readShort(), CONSTANT_Utf8.class);
			signature_index = parent.getResolver().getCheckCpIndex(in.readShort(), CONSTANT_Utf8.class);
			index = in.readShort();
		}


		@Override
		public String toString() {
			return "LocalVariable(start=" + start_pc + ", length=" + length + ", name=" + name_index.getCpObject() +
					", descriptor=" + signature_index.getCpObject() + ")";
		}

	}

}
