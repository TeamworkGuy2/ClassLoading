package twg2.jbcm.toSource;

import java.util.BitSet;

import twg2.jbcm.Opcodes;
import twg2.jbcm.Opcodes.Type;

/**
 * @author TeamworkGuy2
 * @since 2020-08-15
 */
public class IterateCode {

	/** Create a bit set of the same size as {@code instr} with each index corresponding to the {@code instr} array and the bit set to true for instructions and false for operands.
	 * @param instr the code array to process
	 * @return a bit set matching the size of {@code instr} where instruction indices are set to true
	 */
	public static BitSet markInstructions(byte[] instr) {
		var isInstr = new BitSet(instr.length);

		for(int i = 0, size = instr.length; i < size; i++) {
			Opcodes opc = Opcodes.get(instr[i] & 0xFF);
			int numOperands = opc.getOperandCount();
			// Special handling for instructions with unpredictable byte code lengths
			if(numOperands == Opcodes.Const.UNPREDICTABLE) {
				if(Opcodes.WIDE.is(instr[i])) {
					i++; // because wide instructions are wrapped around other instructions
					opc = Opcodes.get(instr[i] & 0xFF);
					numOperands = opc.getOperandCount();
				}
				else if(Opcodes.TABLESWITCH.is(instr[i])) {
					throw new IllegalStateException("tableswitch code handling not implemented");
				}
				else if(Opcodes.LOOKUPSWITCH.is(instr[i])) {
					throw new IllegalStateException("lookupswitch code handling not implemented");
				}
			}

			isInstr.set(i);

			i += (numOperands < 0 ? 0 : numOperands);
		}

		return isInstr;
	}


	/** Read the sequence of instructions starting from 'idx' and return the index of the next conditional, jump, or return instruction.
	 * @param idx the {@code instr} code array start index
	 * @param instr the code
	 * @return an index in the range {@code [idx, instr.length)} of the next conditional, jump, or return instruction found or -1 if no conditional, jump, or return instruction is found
	 */
	public static int nextJumpOrEndIndex(int idx, byte[] instr) {
		for(int i = idx, size = instr.length; i < size; i++) {
			Opcodes opc = Opcodes.get(instr[i] & 0xFF);
			int numOperands = opc.getOperandCount();

			if(opc.hasBehavior(Type.JUMP) || opc.hasBehavior(Type.RETURN) || opc == Opcodes.ATHROW) {
				return i;
			}

			i += (numOperands < 0 ? 0 : numOperands);
		}
		return -1;
	}


	/** Check if the sequence of instructions starting from the current code index is terminated by a return instruction and contains no conditional or jump instructions.
	 * @param idx the {@code instr} code array start index
	 * @param instr the code
	 * @return true if the code from the 'idx' to the next return instruction contains no conditional or jump instructions
	 */
	public static boolean isStraightReturnRun(int idx, byte[] instr) {
		int nextJumpIdx = nextJumpOrEndIndex(idx, instr);
		if(nextJumpIdx == -1) {
			return false;
		}
		Opcodes opc = Opcodes.get(instr[nextJumpIdx] & 0xFF);
		return opc.hasBehavior(Type.RETURN);
	}
}
