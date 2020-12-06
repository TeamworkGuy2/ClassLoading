package twg2.jbcm;

import twg2.collections.primitiveCollections.IntArrayList;
import twg2.collections.primitiveCollections.IntListReadOnly;
import twg2.jbcm.Opcodes.Type;

/** Trace all possible paths through the code in a method. A code flow follows jump, branch/condition, return, and throw instructions.
 * Circular paths end at the first jump/branch destination which already exists in the code flow.
 * @author TeamworkGuy2
 * @since 2020-12-03
 */
public class CodeFlow {

	/** Starting at a given point in a bytecode array, follow code jumps and branches to all termination (return/throw) points potentially reachable from the starting point
	 * @param idx the starting point
	 * @param instr the bytecode array
	 * @param dstPath the list to add jumps, branches/conditions, returns, and throw instruction locations to
	 * @return a list of {@code instr} points at which jumps, branches, returns, and throws occur.
	 * Non-terminating points (jumps and branches) are represented as negated indexes (i.e. {@code ~value})
	 * and can easily be converted back by negating them again. This differentiates non-terminal indexes from all
	 * valid terminal indexes because valid code indexes cannot be less than 0.
	 */
	public static IntArrayList getFlowPaths(int idx, byte[] instr, IntArrayList dstPath) {
		for(int i = idx, size = instr.length; i < size; i++) {
			Opcodes opc = Opcodes.get(instr[i] & 0xFF);
			int numOperands = opc.getOperandCount();

			// Type.JUMP instruction set includes all Type.CONDITION instructions
			if(opc.hasBehavior(Type.JUMP)) {
				// follow the jump path if it has not already been followed (to avoid loops)
				if(!dstPath.contains(~i)) {
					dstPath.add(~i);
				}
				int jumpDst = opc.getJumpDestination(instr, i);
				if(jumpDst < 0) {
					jumpDst = opc.getJumpDestination(instr, i);
				}
				getFlowPaths(jumpDst, instr, dstPath);

				// end this code path if the jump path is unconditional (i.e. GOTO or JSR)
				if(!opc.hasBehavior(Type.CONDITION)) {
					break;
				}
			}
			// end this code flow path once a terminal instruction is reached
			else if(opc.hasBehavior(Type.RETURN) || opc == Opcodes.ATHROW) {
				dstPath.add(i);
				break;
			}

			i += (numOperands < 0 ? 0 : numOperands);
		}

		return dstPath;
	}


	public static int maxIndex(IntListReadOnly codeFlow) {
		int max = -1;
		for(int i = 0, size = codeFlow.size(); i < size; i++) {
			int index = codeFlow.get(i);
			max = Math.max(index < 0 ? ~index : index, max);
		}

		return max;
	}


	public static String flowPathToString(byte[] instr, IntListReadOnly codeFlow) {
		var sb = new StringBuilder();
		for(int i = 0, size = codeFlow.size(); i < size; i++) {
			var idx = codeFlow.get(i);
			// a conditional/jump point
			if(idx < 0) {
				var opc = Opcodes.get(instr[~idx] & 0xFF);
				sb.append(~idx).append(' ').append(opc).append(" -> ");
			}
			// a terminal point
			else {
				var opc = Opcodes.get(instr[idx] & 0xFF);
				sb.append(idx).append(' ').append(opc).append("], ");
			}
		}
		return sb.toString();
	}
}
