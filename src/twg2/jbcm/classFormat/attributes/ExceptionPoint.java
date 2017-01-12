package twg2.jbcm.classFormat.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.ReadWritable;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Class;
import twg2.jbcm.modify.IndexUtility;

/** A Java class file format Attribute exception table entry
 * @author TeamworkGuy2
 * @since 2013-7-7
 */
public class ExceptionPoint implements ReadWritable, Code_Attribute {
	Code parent;
	/* Each entry in the exception_table array describes one exception handler in the code array. The order of the
	 * handlers in the exception_table array is significant. See Section 3.10 for more details.
	 * Each exception_table entry contains the following four items:
	 * start_pc, end_pc
	 * The values of the two items start_pc and end_pc indicate the ranges in the code array at which the exception
	 * handler is active. The value of start_pc must be a valid index into the code array of the opcode of an instruction.
	 * The value of end_pc either must be a valid index into the code array of the opcode of an instruction or must be
	 * equal to code_length, the length of the code array. The value of start_pc must be less than the value of end_pc.
	 * The start_pc is inclusive and end_pc is exclusive; that is, the exception handler must be active while the program
	 * counter is within the interval [start_pc, end_pc).4
	 */
	short start_pc;
	short end_pc;
	/* handler_pc
	 * The value of the handler_pc item indicates the start of the exception handler. The value of the item must be
	 * a valid index into the code array and must be the index of the opcode of an instruction.
	 */
	short handler_pc;
	/* catch_type
	 * If the value of the catch_type item is nonzero, it must be a valid index into the constant_pool table. The
	 * constant_pool entry at that index must be a CONSTANT_Class_info (§4.4.1) structure representing a class of
	 * exceptions that this exception handler is designated to catch. This class must be the class Throwable or one
	 * of its subclasses. The exception handler will be called only if the thrown exception is an instance of the
	 * given class or one of its subclasses.
	 * If the value of the catch_type item is zero, this exception handler is called for all exceptions. This is used
	 * to implement finally (see Section 7.13, "Compiling finally").
	 */
	CpIndex<CONSTANT_Class> catch_type;


	public ExceptionPoint(Code codeParent) {
		this.parent = codeParent;
	}


	@Override
	public void changeCpIndex(short oldIndex, short newIndex) {
		IndexUtility.indexChange(catch_type, oldIndex, newIndex);
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		out.writeShort(start_pc);
		out.writeShort(end_pc);
		out.writeShort(handler_pc);
		catch_type.writeData(out);
	}


	@Override
	public void readData(DataInput in) throws IOException {
		start_pc = in.readShort();
		end_pc = in.readShort();
		handler_pc = in.readShort();
		catch_type = parent.getResolver().getCheckCpIndex(in.readShort(), CONSTANT_Class.class);
	}


	@Override
	public String toString() {
		return "Exception(start=" + start_pc + ", end=" + end_pc + ", handler=" + handler_pc + ", catch_type=" + (catch_type.getIndex() != 0 ? catch_type.getCpObject() : "finally") + ")";
	}

}
