package twg2.jbcm.classFormat.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.CodeUtility;
import twg2.jbcm.Opcodes;
import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.ClassFileAttributes;
import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.ReadWritable;
import twg2.jbcm.classFormat.Settings;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Class;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Utf8;
import twg2.jbcm.modify.CpIndexChangeable;
import twg2.jbcm.modify.CpIndexChanger;

/** A Java class file format Attribute of type <code>Code</code>
 * @author TeamworkGuy2
 * @since 2013-7-7
 */
public class Code implements Attribute_Type {
	public static final String ATTRIBUTE_NAME = "Code";
	ClassFile resolver;
	/** The value of the attribute_name_index item must be a valid index into the constant_pool table. The constant_pool
	 * entry at that index must be a CONSTANT_Utf8_info (§4.4.7) structure representing the string "Code".
	 */
	CpIndex<CONSTANT_Utf8> attribute_name_index;
	/** The value of the attribute_length item indicates the length of the attribute, excluding the initial six bytes. */
	int attribute_length;
	/** The value of the max_stack item gives the maximum depth (§3.6.2) of the operand stack of this method at
	 * any point during execution of the method.
	 */
	short max_stack;
	/** The value of the max_locals item gives the number of local variables in the local variable array allocated upon
	 * invocation of this method, including the local variables used to pass parameters to the method on its invocation.
	 * The greatest local variable index for a value of type long or double is max_locals-2. The greatest local variable
	 * index for a value of any other type is max_locals-1.
	 */
	short max_locals;
	/** The value of the code_length item gives the number of bytes in the code array for this method. The value of
	 * code_length must be greater than zero; the code array must not be empty.
	 */
	int code_length;
	/** The code array gives the actual bytes of Java virtual machine code that implement the method.
	 * When the code array is read into memory on a byte-addressable machine, if the first byte of the array is
	 * aligned on a 4-byte boundary, the tableswitch and lookupswitch 32-bit offsets will be 4-byte aligned.
	 * (Refer to the descriptions of those instructions for more information on the consequences of code array alignment.)
	 * The detailed constraints on the contents of the code array are extensive and are given in a separate
	 * section (§4.8), size [code_length], 0 indexed.
	 */
	byte[] code;
	/** The value of the exception_table_length item gives the number of entries in the exception_table table. */
	short exception_table_length;
	/** Each entry in the exception_table array describes one exception handler in the code array. The order of the
	 * handlers in the exception_table array is significant. See Section 3.10 for more details,
	 * size [exception_table_length], 0 indexed.
	 */
	ExceptionPoint[] exception_table;
	/** The value of the attributes_count item indicates the number of attributes of the Code attribute. */ 
	short attributes_count;
	/** Each value of the attributes table must be an attribute structure (§4.7). A Code attribute can have any number
	 * of optional attributes associated with it.
	 * Currently, the LineNumberTable (§4.7.8) and LocalVariableTable (§4.7.9) attributes, both of which contain
	 * debugging information, are defined and used with the Code attribute.
	 * A Java virtual machine implementation is permitted to silently ignore any or all attributes in the attributes
	 * table of a Code attribute. Attributes not defined in this specification are not allowed to affect the semantics
	 * of the class file, but only to provide additional descriptive information (§4.7.1),
	 * size [attributes_count], 0 indexed.
	 */
	Attribute_Type[] attributes;


	public Code(ClassFile resolver, short attributeNameIndex) {
		this.attribute_name_index = resolver.getAttributeNameIndex(attributeNameIndex);
		this.resolver = resolver;
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
	public void changeCpIndex(CpIndexChanger indexChanger) {
		indexChanger.indexChange(attribute_name_index);
		indexChanger.indexChange(exception_table);
		indexChanger.indexChange(attributes);
	}


	public ClassFile getResolver() {
		return resolver;
	}


	public byte[] getCode() {
		return code;
	}


	public Attribute_Type[] getAttributes() {
		return attributes;
	}


	public ExceptionPoint[] getExceptionTable() {
		return exception_table;
	}


	public int getMaxStack() {
		return max_stack;
	}


	public int getMaxLocals() {
		return max_locals;
	}


	// TODO these modify operations should clone this Code attribute

	public void setCode(byte[] instructions) {
		byte[] oldCode = this.code;
		this.code = instructions;
		this.code_length = instructions.length;
		// TODO figure out how to calculate shift for entire code
		this.attribute_length += (instructions.length - oldCode.length);
		updateOffsets(1, oldCode.length, 0);
	}


	public void prependCode(byte[] instructions) {
		byte[] oldCode = this.code;
		final int newLength = oldCode.length + instructions.length;
		this.code_length = newLength;
		this.code = new byte[newLength];
		System.arraycopy(instructions, 0, code, 0, instructions.length);
		System.arraycopy(oldCode, 0, code, instructions.length, oldCode.length);
		// Shift all offsets up except those that the code starts with at pc 0
		this.attribute_length += instructions.length;
		updateOffsets(1, oldCode.length, instructions.length);
	}


	public void appendCode(byte[] instructions) {
		byte[] oldCode = this.code;
		final int newLength = oldCode.length + instructions.length;
		this.code_length = newLength;
		this.code = new byte[newLength];
		System.arraycopy(oldCode, 0, code, 0, oldCode.length);
		System.arraycopy(instructions, 0, code, oldCode.length, instructions.length);
		// Shift only offsets that extend to the end of the block
		this.attribute_length += instructions.length;
		updateOffsets(oldCode.length, instructions.length, instructions.length);
	}


	private void updateOffsets(int offset, int length, int shift) {
		for(int i = 0, size = attributes_count; i < size; i++) {
			Attribute_Type attrib = attributes[i];
			if(LocalVariableTable.ATTRIBUTE_NAME.equals(attrib.getAttributeName())) {
				((LocalVariableTable)attrib).getAttributeOffsetModifier().changeOffset(offset, length, shift);
			}
		}
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		attribute_name_index.writeData(out);
		out.writeInt(attribute_length);
		out.writeShort(max_stack);
		out.writeShort(max_locals);
		out.writeInt(code_length);
		out.write(code, 0, code_length);
		out.writeShort(exception_table_length);
		for(int i = 0; i < exception_table_length; i++) {
			exception_table[i].writeData(out);
		}
		out.writeShort(attributes_count);
		for(int i = 0; i < attributes_count; i++) {
			attributes[i].writeData(out);
		}
	}


	@Override
	public void readData(DataInput in) throws IOException {
		if(Settings.readAttributeName) {
			attribute_name_index = ClassFileAttributes.readAttributeNameIndex(in, resolver, ATTRIBUTE_NAME);
		}
		attribute_length = in.readInt();
		max_stack = in.readShort();
		max_locals = in.readShort();
		code_length = in.readInt();
		code = new byte[code_length];
		in.readFully(code, 0, code_length);
		exception_table_length = in.readShort();
		exception_table = new ExceptionPoint[exception_table_length];
		for(int i = 0; i < exception_table_length; i++) {
			exception_table[i] = new ExceptionPoint(this);
			exception_table[i].readData(in);
		}
		attributes_count = in.readShort();
		attributes = new Attribute_Type[attributes_count];
		for(int i = 0; i < attributes_count; i++) {
			attributes[i] = ClassFileAttributes.loadAttributeObject(in, resolver, this);
		}
	}


	@Override
	public String toString() {
		StringBuilder dst = new StringBuilder();
		toClassCodeString("\t", dst);
		return dst.toString();
	}


	// approximately black-boxed based on Eclipse class file view
	public void toClassCodeString(String tab, StringBuilder str) {
		str.append(ATTRIBUTE_NAME).append("(stack: ").append(max_stack)
			.append(", locals: ").append(max_locals).append(",\n").append(tab)
			.append("code: ").append(code_length).append(" [\n");

		for(int i = 0; i < code_length; i++) {
			Opcodes opc = Opcodes.get(code[i]);
			boolean isWide = false;
			int numOperands = opc.getOperandCount();
			// Read following bytes of code and convert them to an operand depending on the current instruction
			int operand = CodeUtility.loadOperands(numOperands, code, i);
			// Special handling for instructions with unpredictable byte code lengths
			if(numOperands == Opcodes.Const.UNPREDICTABLE) {
				if(Opcodes.WIDE.is(code[i])) {
					// TODO doesn't properly read extra wide operand bytes
					isWide = true;
					i++; // because wide instructions are wrapped around other instructions
					opc = Opcodes.get(code[i]);
					numOperands = opc.getOperandCount();
				}
				else if(Opcodes.TABLESWITCH.is(code[i])) {
					throw new IllegalStateException("tableswitch code handling not implemented");
				}
				else if(Opcodes.LOOKUPSWITCH.is(code[i])) {
					throw new IllegalStateException("lookupswitch code handling not implemented");
				}
			}

			str.append(tab).append(instructionIndex(i)).append(isWide ? "WIDE " : "").append(opc.displayName());

			if(operand > 0) {
				if(opc.hasBehavior(Opcodes.Type.CP_INDEX) && operand < resolver.getConstantPoolCount()) {
					str.append(' ').append(resolver.getCpIndex((short)operand).getCpObject().toShortString()).append(" [").append(operand).append(']');
				}
				else if(opc.hasBehavior(Opcodes.Type.JUMP)) {
					str.append(' ').append(i + operand);
				}
				else {
					str.append(' ').append(operand).append(" 0x").append(Integer.toHexString(operand));
				}
				str.append("\n");
			}
			else {
				str.append("\n");
			}

			i += (numOperands < 0 ? 0 : numOperands);
		}
		str.append(tab).append("],\n").append(tab);

		str.append("exceptions: ").append(exception_table_length).append(" [");
		if(exception_table_length > 0) { str.append("\n").append(tab); }
		for(int i = 0; i < exception_table_length - 1; i++) {
			str.append(exception_table[i]).append(",\n").append(tab);
		}
		if(exception_table_length > 0) { str.append(exception_table[exception_table_length - 1]); }
		str.append("],\n").append(tab);

		str.append("attributes: ").append(attributes_count).append(" [");
		if(attributes_count > 0) { str.append("\n").append(tab); }
		for(int i = 0; i < attributes_count - 1; i++) {
			str.append(attributes[i]).append(",\n").append(tab);
		}
		if(attributes_count > 0) { str.append(attributes[attributes_count - 1]); }
		str.append("])");
	}


	private static String instructionIndex(int instPtr) {
		if(instPtr < 10) {
			return "  " + instPtr + "  ";
		}
		else if(instPtr < 100) {
			return " " + instPtr + "  ";
		}
		else {
			return Integer.toString(instPtr) + "  ";
		}
	}



	/** A Java class file format Attribute of type <code>Exceptions</code>
	 * @author TeamworkGuy2
	 * @since 2013-7-7
	 */
	public static class ExceptionPoint implements ReadWritable, CpIndexChangeable {
		Code parent;
		/** Each entry in the exception_table array describes one exception handler in the code array. The order of the
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
		/** The value of the handler_pc item indicates the start of the exception handler. The value of the item must be
		 * a valid index into the code array and must be the index of the opcode of an instruction.
		 */
		short handler_pc;
		/** If the value of the catch_type item is nonzero, it must be a valid index into the constant_pool table. The
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


		/**
		 * @see #parent
		 */
		public Code getParent() {
			return parent;
		}


		/**
		 * @see #start_pc
		 */
		public short getStartPc() {
			return start_pc;
		}


		/**
		 * @see #end_pc
		 */
		public short getEndPc() {
			return end_pc;
		}


		/**
		 * @see #handler_pc
		 */
		public short getHandlerPc() {
			return handler_pc;
		}


		/**
		 * @see #catch_type
		 */
		public CpIndex<CONSTANT_Class> getCatchType() {
			return catch_type;
		}


		@Override
		public void changeCpIndex(CpIndexChanger indexChanger) {
			indexChanger.indexChange(catch_type);
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
			catch_type = parent.getResolver().getCheckCpIndex(in.readShort(), CONSTANT_Class.class, true);
		}


		@Override
		public String toString() {
			return "Exception(start=" + start_pc + ", end=" + end_pc + ", handler=" + handler_pc + ", catch_type=" + (catch_type != null && catch_type.getIndex() > 0 ? catch_type.getCpObject() : "finally") + ")";
		}

	}

}
