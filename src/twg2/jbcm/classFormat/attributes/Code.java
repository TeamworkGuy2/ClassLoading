package twg2.jbcm.classFormat.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.Opcodes;
import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.Settings;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Utf8;
import twg2.jbcm.modify.IndexUtility;

/** A Java class file format Attribute of type <code>Code</code>
 * @author TeamworkGuy2
 * @since 2013-7-7
 */
public class Code implements Attribute_Type {
	public static final String ATTRIBUTE_NAME = "Code";
	ClassFile resolver;
	/* The value of the attribute_name_index item must be a valid index into the constant_pool table. The constant_pool
	 * entry at that index must be a CONSTANT_Utf8_info (§4.4.7) structure representing the string "Code".
	 */
	CpIndex<CONSTANT_Utf8> attribute_name_index;
	// The value of the attribute_length item indicates the length of the attribute, excluding the initial six bytes.
	int attribute_length;
	/* The value of the max_stack item gives the maximum depth (§3.6.2) of the operand stack of this method at
	 * any point during execution of the method.
	 */
	short max_stack;
	/* The value of the max_locals item gives the number of local variables in the local variable array allocated upon
	 * invocation of this method, including the local variables used to pass parameters to the method on its invocation.
	 * The greatest local variable index for a value of type long or double is max_locals-2. The greatest local variable
	 * index for a value of any other type is max_locals-1.
	 */
	short max_locals;
	/* The value of the code_length item gives the number of bytes in the code array for this method. The value of
	 * code_length must be greater than zero; the code array must not be empty.
	 */
	int code_length;
	/* The code array gives the actual bytes of Java virtual machine code that implement the method.
	 * When the code array is read into memory on a byte-addressable machine, if the first byte of the array is
	 * aligned on a 4-byte boundary, the tableswitch and lookupswitch 32-bit offsets will be 4-byte aligned.
	 * (Refer to the descriptions of those instructions for more information on the consequences of code array alignment.)
	 * The detailed constraints on the contents of the code array are extensive and are given in a separate
	 * section (§4.8), size [code_length], 0 indexed.
	 */
	byte[] code;
	// The value of the exception_table_length item gives the number of entries in the exception_table table.
	short exception_table_length;
	/* Each entry in the exception_table array describes one exception handler in the code array. The order of the
	 * handlers in the exception_table array is significant. See Section 3.10 for more details,
	 * size [exception_table_length], 0 indexed.
	 */
	ExceptionPoint[] exception_table;
	// The value of the attributes_count item indicates the number of attributes of the Code attribute. 
	short attributes_count;
	/* Each value of the attributes table must be an attribute structure (§4.7). A Code attribute can have any number
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
		this.attribute_name_index = Settings.initAttributeNameIndex(attributeNameIndex, resolver);
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
	public void changeCpIndex(short oldIndex, short newIndex) {
		IndexUtility.indexChange(attribute_name_index, oldIndex, newIndex);
		IndexUtility.indexChange(exception_table, oldIndex, newIndex);
		IndexUtility.indexChange(attributes, oldIndex, newIndex);
	}


	public ClassFile getResolver() {
		return resolver;
	}


	public byte[] getCode() {
		return code;
	}


	public void setCode(byte[] instructions) {
		byte[] oldCode = this.code;
		this.code = instructions;
		this.code_length = instructions.length;
		// TODO figure out how to calculate shift for entire code
		this.attribute_length += (instructions.length-oldCode.length);
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


	/** Set this code block's max stack size
	 * @param maxStackSize the size of this code's stack
	 */
	public void setStackSize(short maxStackSize) {
		this.max_stack = maxStackSize;
	}


	/** Ensure that this code block's max stack size is atleast the specified size
	 * @param maxStackSize the minimum size of this code's stack
	 */
	public void ensureStackSize(short maxStackSize) {
		if(maxStackSize > this.max_stack) {
			this.max_stack = maxStackSize;
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
		if(Settings.doReadAttributeName()) {
			attribute_name_index = Settings.readAttributeNameIndex(in, resolver, ATTRIBUTE_NAME);
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
			attributes[i] = Settings.loadAttributeObject(in, resolver, this);
		}
	}


	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		boolean endParentheses = false;
		int numOperands = 0;
		int operand = 0;
		str.append(ATTRIBUTE_NAME + "(stack: " + max_stack + ", locals: " + max_locals + ",\n\t");
		str.append("code: " + code_length + " [\n");
		for(int i = 0; i < code_length; i++) {
			numOperands = Opcodes.getOpcode((int)(code[i] & 0xFF)).getOperandCount();
			// Read following bytes of code and convert them to an operand depending on the number of operands specified for the current command
			operand = loadOperands(numOperands, code, i);
			// Special handling for instructions with unpredictable byte code lengths
			if(numOperands == Opcodes.Const.UNPREDICTABLE) {
				str.append("\t" + Opcodes.getOpcode((int)(code[i] & 0xFF)).getOperandCount() + "(");
				endParentheses = true;
				if(Opcodes.WIDE.is(code[i])) {
					i++; // because wide operations are nested around other operations 
					numOperands = Opcodes.getOpcode((int)(code[i] & 0xFF)).getOperandCount();
				}
				else if(Opcodes.TABLESWITCH.is(code[i])) {
					throw new IllegalStateException("tableswitch code handling not implemented");
				}
				else if(Opcodes.LOOKUPSWITCH.is(code[i])) {
					throw new IllegalStateException("lookupswitch code handling not implemented");
				}
			}
			str.append("\t" + Opcodes.getOpcode((int)(code[i] & 0xFF)).name() + ((operand > 0 && operand < resolver.getConstantPoolCount()) ? "(" + numOperands + ", " + resolver.getCpIndex((short)operand).getCpObject() + "),\n" : ",\n"));
			if(endParentheses == true) {
				str.append(")\n");
				endParentheses = false;
			}
			i+= (numOperands < 0) ? 0 : numOperands;
		}
		str.append("\n\t],\n\t");

		str.append("exceptions: " + exception_table_length + " [");
		if(exception_table_length > 0) { str.append("\n\t"); }
		for(int i = 0; i < exception_table_length-1; i++) {
			str.append(exception_table[i] + ",\n\t");
		}
		if(exception_table_length > 0) { str.append(exception_table[exception_table_length-1]); }
		str.append("],\n\t");

		str.append("attributes: " + attributes_count + " [");
		if(attributes_count > 0) { str.append("\n\t"); }
		for(int i = 0; i < attributes_count-1; i++) {
			str.append(attributes[i] + ",\n\t");
		}
		if(attributes_count > 0) { str.append(attributes[attributes_count-1]); }
		str.append("])");
		return str.toString();
	}


	private int loadOperands(int numOperands, byte[] code, int index) {
		return (numOperands > 3 ? (((code[index+1] & 0xFF) << 24) | ((code[index+2] & 0xFF) << 16) | ((code[index+3] & 0xFF) << 8) | (code[index+4] & 0xFF)) :
			(numOperands > 2 ? (((code[index+1] & 0xFF) << 16) | ((code[index+2] & 0xFF) << 8) | (code[index+3] & 0xFF)) :
				(numOperands > 1 ? (((code[index+1] & 0xFF) << 8) | (code[index+2] & 0xFF)) :
					(numOperands > 0 ? ((code[index+1] & 0xFF)) : -1))));
	}

}
