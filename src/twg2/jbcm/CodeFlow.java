package twg2.jbcm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import twg2.collections.primitiveCollections.IntArrayList;
import twg2.collections.primitiveCollections.IntListReadOnly;
import twg2.jbcm.Opcodes.Type;
import twg2.jbcm.ir.JumpConditionInfo;
import twg2.jbcm.ir.JumpConditionInfo.UsageHint;

/** Trace all possible paths through the code in a method. A code flow follows jump, branch/condition, return, and throw instructions.
 * Circular paths end at the first jump/branch destination which already exists in the code flow.
 * @author TeamworkGuy2
 * @since 2020-12-03
 */
public class CodeFlow {
	/** The size of a GOTO instruction, 1 byte opcode + 2 byte operand */
	public static final int GOTO_SIZE = 3;


	/** Starting at a given point in a bytecode array, follow code jumps and branches to all termination (return/throw) points potentially reachable from the starting point
	 * @param idx the starting point
	 * @param instr the bytecode array
	 * @param dstPath the list to add jumps, branches/conditions, returns, and throw instruction locations to
	 * @return a list of {@code instr} points at which jumps, branches, returns, and throws occur.
	 * Non-terminating points (jumps and branches) are represented as negated indexes (i.e. {@code ~value})
	 * and can easily be converted back by negating them again. This differentiates non-terminal indexes from all
	 * valid terminal indexes because valid code indexes cannot be less than 0.
	 */
	public static IntArrayList getFlowPaths(byte[] code, int idx) {
		var dstPath = new IntArrayList();
		getFlowPaths(code, idx, code.length, dstPath, 0);
		return dstPath;
	}


	public static int getFlowPaths(byte[] code, int idx, int max, IntArrayList dstPath, int pathJumps) {
		for(int i = idx; i < max; i++) {
			Opcodes opc = Opcodes.get(code[i]);
			int numOperands = opc.getOperandCount();

			// Type.JUMP instruction set includes all Type.CONDITION instructions
			if(opc.hasBehavior(Type.JUMP)) {
				// skip the jump path if it has already been followed and this is the beginning (to avoid loops)
				if(dstPath.contains(~i) && pathJumps == 0) {
					break;
				}
				dstPath.add(~i);
				pathJumps++;
				int jumpDst = opc.getJumpDestination(code, i);
				if(jumpDst < 0) {
					jumpDst = opc.getJumpDestination(code, i);
				}
				int subPathJumps = getFlowPaths(code, jumpDst, max, dstPath, pathJumps);
				pathJumps = subPathJumps;

				// end this code path if the jump path is unconditional (i.e. GOTO or JSR)
				if(!opc.hasBehavior(Type.CONDITION)) {
					break;
				}
			}
			// end this code flow path once a terminal instruction is reached
			else if(opc.hasBehavior(Type.RETURN) || opc == Opcodes.ATHROW) {
				dstPath.add(i);
				pathJumps = 0;
				break;
			}

			i += (numOperands < 0 ? 0 : numOperands);
		}

		return pathJumps;
	}


	/**
	 * @param code the code array
	 * @param offset the offset into the code array at which to start finding instructions
	 * @param length the number of bytes of the code array to check through
	 * @return
	 */
	public static List<JumpConditionInfo> findFlowConditions(byte[] code, int offset, int length) {
		var conditions = new ArrayList<JumpConditionInfo>(); // track GOTO/IF_* loops detected in the code

		// BYTECODE LOOP:
		for(int i = offset, size = offset + length; i < size; i++) {
			Opcodes opc = Opcodes.get(code[i]);
			int numOperands = opc.getOperandCount();
			// Special handling for instructions with unpredictable byte code lengths
			if(numOperands == Opcodes.Const.UNPREDICTABLE) {
				if(Opcodes.WIDE.is(code[i])) {
					i++; // WIDE opcodes are nested around other operations 
					opc = Opcodes.get(code[i]);
					numOperands = opc.getOperandCount() * 2; // WIDE opcodes double the operands of the widened opcode
				}
				else if(Opcodes.TABLESWITCH.is(code[i])) {
					throw new IllegalStateException("tableswitch code handling not implemented");
				}
				else if(Opcodes.LOOKUPSWITCH.is(code[i])) {
					throw new IllegalStateException("lookupswitch code handling not implemented");
				}
			}
			int jumpRelative = CodeUtility.loadOperands(numOperands, code, i);

			// form 1: [..., GOTO <setup_if[0]>, instructions[], setup_if[], IF_* <instructions[0]>, ...]  - for()/while() forward GOTO, condition after loop with backward jump
			// form 2: [..., setup_if[], IF_* <after[0]>, instructions[], GOTO <setup_if[0]>, after[], ...]  - for()/while() condition before loop with forward jump, backward GOTO
			// form 3: [..., instructions[], setup_if[], IF_* <instructions[0]>, after[], ...]  - do{}while() condition after loop with backward jump
			var isJump = opc.hasBehavior(Opcodes.Type.JUMP);
			// backward jump, required for a loop (thought experiment: create a loop, using Java bytecodes, that does not jump backward)
			// although a code obfuscator could re-arrange code and include backward jumps so not all backward jumps are loops
			if(isJump && jumpRelative < 0) {
				conditions.add(JumpConditionInfo.loadConditionFlow(opc, i, jumpRelative, code, UsageHint.FOR_OR_WHILE_LOOP));
				// 'for' or 'while' loop has to evaluate the condition first so it needs an IF or GOTO at the beginning
				// 'do-while' loop evaluates condition after loop runs once, only compiled form seen so far is: no GOTO and one backward jump at the end
			}
			else if(opc.hasBehavior(Opcodes.Type.CONDITION)) {
				conditions.add(JumpConditionInfo.loadConditionFlow(opc, i, jumpRelative, code, UsageHint.IF));
			}
			i += (numOperands < 0) ? 0 : numOperands;
		}

		Collections.sort(conditions, JumpConditionInfo.LOWER_INDEX_SORTER);

		// post processing - convert special cases
		for(int i = 0, size = conditions.size(); i < size; i++) {
			var loop = conditions.get(i);
			// find and convert if-conditions that may have been miss-identified as loops
			// case: an if-statement inside a loop where there are no instructions after the if-statement and before the
			// end of the loop may be compiled as a condition with a backward jump and thus look like a loop, we can tell
			// in the case when it shares the same jump destination as the closest parent loop that contains it
			// form: [..., loop_start, instructions[], setup_if[], IF_* <loop_start>, instructions_in_if[], loop_end, ...]
			if(loop.targetOffset < 0) {
				var targetIndex = loop.getTargetIndex();
				var loopUpperIndex = loop.getUpperIndex();
				// look at conditions beyond the current one since they are later in the code or contained within the
				// current loop and a nested if-statement is contained within the nearest parent loop
				for(int j = i + 1; j < size; j++) {
					var loopJ = conditions.get(j);
					if(loopJ.opcIdx > loopUpperIndex) {
						break; // skip remaining conditions once we're past beyond the bounds of the current one
					}
					if(loopJ.targetOffset < 0 && targetIndex == loopJ.getTargetIndex() && containsIndex(loop, loopJ.opcIdx)) {
						// TODO debugging
						System.out.println("converted loop to nested IF-within-loop at " + loopJ.opcIdx + " (" + loopJ.opc + ") contained in " + loop + " to " + targetIndex);

						conditions.set(j, loopJ.withLoopEndIndexForIf(loopUpperIndex));
					}
				}
			}

			// set the potential-if-index of loops
			if(UsageHint.isLoop(loop.usageHint) && loop.potentialIfIndex < 0) {
				var loopConditionIdx = findFirstIfConditionPointingToEndOf(conditions, i);

				if(loopConditionIdx >= 0) {
					loop = loop.withPotentialIfIndex(conditions.get(loopConditionIdx).opcIdx);
					conditions.set(i, loop);

					// TODO debugging
					System.out.println("converted if index for loop: " + loop + " found IF " + (loopConditionIdx >= 0 ? conditions.get(loopConditionIdx) : "-1"));

					conditions.remove(loopConditionIdx);
					size--;
					if(loopConditionIdx <= i) {
						i--;
					}
				}
			}
		}

		return conditions;
	}


	/** Find the first IF* condition that is contained within the condition located at {@code startIdx} in the {@code conditions} list.
	 * @param conditions list of conditions, should include all IF* and GOTO instructions in the code,
	 * sorted based on {@link JumpConditionInfo#getLowerIndex()}
	 * @param startIdx the index into the {@code conditions} list of the condition to find an IF* condition within
	 * @return the {@code conditions} index of the first matching IF* condition, else -1 if none is found
	 */
	public static int findFirstIfConditionPointingToEndOf(List<JumpConditionInfo> conditions, int startIdx) {
		var withinThis = conditions.get(startIdx);
		int maxIdx = withinThis.getUpperIndex();
		int lowestOpcIdxFound = Integer.MAX_VALUE;
		int lowestOpcIdxI = -1;

		for(int i = startIdx + 1, size = conditions.size(); i < size; i++) {
			var cond = conditions.get(i);
			// stop once the condition isn't contained within the target condition, we can safely break because the loops are sorted by lower bound index
			if(cond.getLowerIndex() > maxIdx) {
				break;
			}
			if(cond != withinThis && cond.opcIdx < lowestOpcIdxFound && containsIfAndEndsWith(withinThis, cond)) {
				lowestOpcIdxFound = cond.opcIdx;
				lowestOpcIdxI = i;
			}
		}
		return lowestOpcIdxI;
	}


	public static boolean containsJumpTo(byte[] code, int offset, int length, int targetIndex) {
		// BYTECODE LOOP:
		for(int i = offset, size = offset + length; i < size; i++) {
			Opcodes opc = Opcodes.get(code[i]);
			int numOperands = opc.getOperandCount();
			// Special handling for instructions with unpredictable byte code lengths
			if(numOperands == Opcodes.Const.UNPREDICTABLE) {
				if(Opcodes.WIDE.is(code[i])) {
					i++; // WIDE opcodes are nested around other operations
					opc = Opcodes.get(code[i]);
					numOperands = opc.getOperandCount() * 2; // WIDE opcodes double the operands of the widened opcode
				}
				else if(Opcodes.TABLESWITCH.is(code[i])) {
					throw new IllegalStateException("tableswitch code handling not implemented");
				}
				else if(Opcodes.LOOKUPSWITCH.is(code[i])) {
					throw new IllegalStateException("lookupswitch code handling not implemented");
				}
			}
			if(opc.hasBehavior(Opcodes.Type.JUMP)) {
				int jumpRelative = CodeUtility.loadOperands(numOperands, code, i);
				if(i + jumpRelative == targetIndex) {
					return true;
				}
			}

			i += (numOperands < 0) ? 0 : numOperands;
		}
		return false;
	}


	public static int findLastOpcodeIndex(byte[] instr, int start, int end) {
		AtomicInteger lastIdx = new AtomicInteger(-1);
		CodeUtility.forEach(instr, start, end - start, (opc, instrs, idx) -> {
			lastIdx.set(idx);
		});
		return lastIdx.get();
	}


	public static int findContainsIfIndex(List<JumpConditionInfo> loops, int index) {
		for(int i = 0, size = loops.size(); i < size; i++) {
			if(loops.get(i).potentialIfIndex == index) {
				return i;
			}
		}
		return -1;
	}


	public static int findOpcIndex(List<JumpConditionInfo> loops, int index) {
		for(int i = 0, size = loops.size(); i < size; i++) {
			if(loops.get(i).opcIdx == index) {
				return i;
			}
		}
		return -1;
	}


	public static boolean containsIndex(JumpConditionInfo cond, int index) {
		var condTarget = cond.opcIdx + cond.targetOffset;
		// avoid branch logic (ternary statements such as Math.min/max)
		return (index >= cond.opcIdx && index <= condTarget) || (index >= condTarget && index <= cond.opcIdx);
	}


	/**
	 * Check that an {@code ifCond}'s lower bound (generally its opcode index) is within a loop condition's
	 * bounds and that the {@code ifCond}'s upper bound (generally its target index) is the instruction immediately after
	 * the loop end instruction.
	 * ASSUMPTION: the {@code loopCond}'s opcode index is its upper bound (i.e. the loop ends with a backward jump instruction)
	 * @param loopCond the loop condition
	 * @param ifCond the other condition, could be a loop or if
	 * @return true if the conditions described above hold, false if not
	 */
	public static boolean containsIfAndEndsWith(JumpConditionInfo loopCond, JumpConditionInfo ifCond) {
		return loopCond.getTargetIndex() <= ifCond.getLowerIndex() &&
			// require the match to be a condition that jumps to the instruction after the loop
			loopCond.getOpcodeIndex() + loopCond.opc.getOperandCount() + 1 == ifCond.getUpperIndex();
	}


	public static int maxIndex(IntListReadOnly codeFlow) {
		int max = -1;
		for(int i = 0, size = codeFlow.size(); i < size; i++) {
			int index = codeFlow.get(i);
			max = Math.max(index < 0 ? ~index : index, max);
		}

		return max;
	}


	public static String flowPathToString(byte[] code, IntListReadOnly codeFlow) {
		var sb = new StringBuilder();
		for(int i = 0, size = codeFlow.size(); i < size; i++) {
			var idx = codeFlow.get(i);
			// a conditional/jump point
			if(idx < 0) {
				var opc = Opcodes.get(code[~idx]);
				sb.append(~idx).append(' ').append(opc).append(" -> ");
			}
			// a terminal point
			else {
				var opc = Opcodes.get(code[idx]);
				sb.append(idx).append(' ').append(opc).append("], ");
			}
		}
		return sb.toString();
	}
}
