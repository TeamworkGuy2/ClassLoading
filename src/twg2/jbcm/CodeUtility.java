package twg2.jbcm;

import twg2.jbcm.modify.BytecodeConsumer;
import twg2.jbcm.modify.ChangeCpIndex;
import twg2.jbcm.modify.CodeOffsetChanger;
import twg2.jbcm.modify.CpIndexChanger;

/** Utilities for dealing with byte code arrays
 * @author TeamworkGuy2
 * @since 2020-12-3
 */
public class CodeUtility {

	/** Shift the offset values associated with a specific instruction in a chunk of code.
	 * For example, shifting a goto offsets at position 55 by 12 might look like:<br/>
	 * {@code shiftOffset(0xA7, 12, 1, 2, code, 55);}<br/>
	 * Or shifting a goto_w offsets at position 25 by 160:<br/>
	 * {@code shiftOffset(0xC8, 160, 1, 4, code, 25);}
	 * @param offset the instruction code offset to adjust
	 * @param offsetOffset the number of bytes ahead of the opcode at which the offset to adjust starts (1 for an offset that immediately follows an opcode)
	 * @param code the array of code to search through for the opcode
	 * @param codeOffset the offset into the code array at which to update the opcode's offset value
	 * @return the location after the opcode's offset value, calculated as {@code codeOffset + offsetOffset + 1}
	 */
	public static int shift1Offset(int offset, final int offsetOffset, byte[] code, int codeOffset) {
		codeOffset += offsetOffset;
		byte curOffset = code[codeOffset];
		if(curOffset + offset < 0) {
			throw new ArithmeticException("byte overflow: " + curOffset + "+" + offset + "=" + (curOffset+offset));
		}
		curOffset += offset;
		code[codeOffset] = curOffset;
		return codeOffset + 1;
	}


	/** Shift the offset values associated with a specific instruction in a chunk of code.
	 * For example, shifting a goto offsets at position 55 by 12 might look like:<br/>
	 * {@code shiftOffset(12, 1, 2, code, 55);}<br/>
	 * Or shifting a goto_w offsets at position 25 by 160:<br/>
	 * {@code shiftOffset(160, 1, 4, code, 25);}
	 * @param offset the instruction code offset to adjust
	 * @param offsetOffset the number of bytes ahead of the opcode at which the offset to adjust starts (1 for an offset that immediately follows an opcode)
	 * @param code the array of code to search through for the opcode
	 * @param codeOffset the offset into the code array at which to update the opcode's offset value
	 * @return the location after the opcode's offset value, calculated as {@code codeOffset + offsetOffset + 2}
	 */
	public static int shift2Offset(int offset, int offsetOffset, byte[] code, int codeOffset) {
		codeOffset += offsetOffset;
		short curOffset = IoUtility.readShort(code, codeOffset);
		if(curOffset + offset < 0) {
			throw new ArithmeticException("short overflow: " + curOffset + "+" + offset + "=" + (curOffset+offset));
		}
		curOffset += offset;
		IoUtility.writeShort(curOffset, code, codeOffset);
		return codeOffset + 2;
	}


	/** Shift the offset values associated with a specific instruction in a chunk of code.
	 * For example, shifting a goto offsets at position 55 by 12 might look like:<br/>
	 * {@code shiftOffset(12, 1, 2, code, 55);}<br/>
	 * Or shifting a goto_w offsets at position 25 by 160:<br/>
	 * {@code shiftOffset(160, 1, 4, code, 25);}
	 * @param offset the instruction code offset to adjust
	 * @param offsetOffset the number of bytes ahead of the opcode at which the offset to adjust starts (1 for an offset that immediately follows an opcode)
	 * @param code the array of code to search through for the opcode
	 * @param codeOffset the offset into the code array at which to update the opcode's offset value
	 * @return the location after the opcode's offset value, calculated as {@code codeOffset + offsetOffset + 4}
	 */
	public static int shift4Offset(int offset, int offsetOffset, byte[] code, int codeOffset) {
		codeOffset += offsetOffset;
		int curOffset = IoUtility.readInt(code, codeOffset);
		if(curOffset + offset < 0) {
			throw new ArithmeticException("integer overflow: " + curOffset + "+" + offset + "=" + (curOffset+offset));
		}
		curOffset += offset;
		IoUtility.writeInt(curOffset, code, codeOffset);
		return codeOffset + 4;
	}


	/** Call the specified {@code BytecodeConsumer} for each instruction in the specified code array
	 * @param code the code array
	 * @param offset the offset into the code array at which to start finding instructions
	 * @param length the number of bytes of the code array to check through
	 * @param cbFunc the function to call for each instruction found in specified code array range
	 */
	public static void forEach(byte[] code, int offset, int length, BytecodeConsumer cbFunc) {
		int numOperands = 0;
		@SuppressWarnings("unused")
		int operand = 0;

		for(int i = offset, size = offset + length; i < size; i++) {
			numOperands = Opcodes.get((code[i] & 0xFF)).getOperandCount();
			// Read following bytes of code and convert them to an operand depending on the number of operands specified for the current command
			operand = CodeUtility.loadOperands(numOperands, code, i);
			// Special handling for instructions with unpredictable byte code lengths
			if(numOperands == Opcodes.Const.UNPREDICTABLE) {
				if(Opcodes.WIDE.is(code[i])) {
					cbFunc.accept(Opcodes.get((code[i] & 0xFF)), code, i);
					i++; // because wide operations are nested around other operations 
					numOperands = Opcodes.get((code[i] & 0xFF)).getOperandCount();
				}
				else if(Opcodes.TABLESWITCH.is(code[i])) {
					throw new IllegalStateException("tableswitch code handling not implemented");
				}
				else if(Opcodes.LOOKUPSWITCH.is(code[i])) {
					throw new IllegalStateException("lookupswitch code handling not implemented");
				}
			}
			cbFunc.accept(Opcodes.get((code[i] & 0xFF)), code, i);
			i+= (numOperands < 0) ? 0 : numOperands;
		}
	}

	/** Extract from [0, 4] operands following a specified index in little-endian style order:
	 * <pre>{@code
     * (((a & 0xFF) << 24) | ((b & 0xFF) << 16) | ((c & 0xFF) <<  8) | (d & 0xFF))
     * }</pre>
	 * @param numOperands the number of bytes to read as operand(s)
	 * @param code the byte code array
	 * @param index the index of the instruction immediately preceding the operand(s)
	 * @return The binary OR'ed value of the operand bytes with the first operand in the most significant position or -1 if {@code numOperations = 0}
	 */
	public static int loadOperands(int numOperands, byte[] code, int index) {
		return (numOperands > 3 ? (((code[index+1] & 0xFF) << 24) | ((code[index+2] & 0xFF) << 16) | ((code[index+3] & 0xFF) << 8) | (code[index+4] & 0xFF)) :
			(numOperands > 2 ? (((code[index+1] & 0xFF) << 16) | ((code[index+2] & 0xFF) << 8) | (code[index+3] & 0xFF)) :
				(numOperands > 1 ? (((code[index+1] & 0xFF) << 8) | (code[index+2] & 0xFF)) :
					(numOperands > 0 ? ((code[index+1] & 0xFF)) : -1))));
	}


	public static CpIndexChanger cpIndex(int offset, int len) {
		return new ChangeCpIndex(offset, len);
	}


	public static CodeOffsetChanger offsetModifier(int offset, int len) {
		return new ChangeCpIndex(offset, len);
	}

}
