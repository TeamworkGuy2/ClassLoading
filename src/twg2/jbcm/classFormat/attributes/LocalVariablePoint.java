package twg2.jbcm.classFormat.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.ReadWritable;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Utf8;
import twg2.jbcm.modify.AttributeOffsetFunction;
import twg2.jbcm.modify.IndexUtility;
import twg2.jbcm.modify.OffsetAttribute;

/** Java class file format Code Attribute for local variable table
 * @author TeamworkGuy2
 * @since 2013-7-7
 */
public class LocalVariablePoint implements ReadWritable, OffsetAttribute, Code_Attribute {
	private AttributeOffsetFunction offsetAction = new AttributeOffsetFunction() {
		@Override public void changeOffset(int off, int len, int shift) {
			if(start_pc >= off && start_pc < off+len) {
				start_pc += shift;
			}
			if(length >= off && length < off+len) {
				length += shift;
			}
			// TODO bounds checking and start_pc+length falls on the index of another opcode or the first index
			// beyond the end of the code array
		}
	};

	Code parent;
	/* Each entry in the local_variable_table array indicates a range of code array offsets within which a local
	 * variable has a value. It also indicates the index into the local variable array of the current frame at which
	 * that local variable can be found. Each entry must contain the following five items:
	 * start_pc, length
	 * The given local variable must have a value at indices into the code array in the interval
	 * [start_pc, start_pc+length], that is, between start_pc inclusive and start_pc+length exclusive. The value of start_pc
	 * must be a valid index into the code array of this Code attribute and must be the index of the opcode of an
	 * instruction. Either the value of start_pc+length must be a valid index into the code array of this Code
	 * attribute and be the index of the opcode of an instruction, or it must be the first index beyond the end
	 * of that code array.
	 */
	short start_pc;
	short length;
	/* name_index, descriptor_index
	 * The value of the name_index item must be a valid index into the constant_pool table. The constant_pool entry at
	 * that index must contain a CONSTANT_Utf8_info (§4.4.7) structure representing a valid local variable name
	 * stored as a simple name (§2.7.1).
	 * The value of the descriptor_index item must be a valid index into the constant_pool table. The constant_pool
	 * entry at that index must contain a CONSTANT_Utf8_info (§4.4.7) structure representing a field
	 * descriptor (§4.3.2) encoding the type of a local variable in the source program.
	 */
	CpIndex<CONSTANT_Utf8> name_index;
	CpIndex<CONSTANT_Utf8> descriptor_index;
	/* The given local variable must be at this index in the local variable array of the current frame. If the local
	 * variable at index is of type double or long, it occupies both index and index+1.
	 */
	short index;


	public LocalVariablePoint(Code codeParent) {
		this.parent = codeParent;
	}


	@Override
	public AttributeOffsetFunction getAttributeOffsetModifier() {
		return offsetAction;
	}


	@Override
	public void changeCpIndex(short oldIndex, short newIndex) {
		IndexUtility.indexChange(name_index, oldIndex, newIndex);
		IndexUtility.indexChange(descriptor_index, oldIndex, newIndex);
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		out.writeShort(start_pc);
		out.writeShort(length);
		name_index.writeData(out);
		descriptor_index.writeData(out);
		out.writeShort(index);
	}


	@Override
	public void readData(DataInput in) throws IOException {
		start_pc = in.readShort();
		length = in.readShort();
		name_index = parent.getResolver().getCheckCpIndex(in.readShort(), CONSTANT_Utf8.class);
		descriptor_index = parent.getResolver().getCheckCpIndex(in.readShort(), CONSTANT_Utf8.class);
		index = in.readShort();
	}


	@Override
	public String toString() {
		return "LocalVariable(start=" + start_pc + ", length=" + length + ", name=" + name_index.getCpObject() + ", descriptor=" + descriptor_index.getCpObject() + ")";
	}

}
