package twg2.jbcm.ir;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import twg2.collections.primitiveCollections.IntArrayList;
import twg2.jbcm.CodeFlow;
import twg2.jbcm.Opcodes;

/** Contains the beginning and end opcodes and targets for a goto/if condition pair
 * @author TeamworkGuy2
 * @since 2020-08-15
 */
public class JumpConditionInfo {
	private final Opcodes opc;
	private final int opcIdx;
	private final int targetOffset;
	private final IntArrayList codeFlow;
	public final int codeFlowMaxIndex;
	private boolean finished;


	public JumpConditionInfo(Opcodes opc, int opcIdx, int targetOffset, int codeFlowMaxIndex, IntArrayList codeFlow) {
		this.opc = opc;
		this.opcIdx = opcIdx;
		this.targetOffset = targetOffset;
		this.codeFlowMaxIndex = codeFlowMaxIndex;
		this.codeFlow = codeFlow;
	}


	public Opcodes getOpcode() {
		return opc;
	}


	public int getOpcodeIndex() {
		return opcIdx;
	}


	public IntArrayList getCodeFlow() {
		return codeFlow;
	}


	public int getTargetIndex() {
		return opcIdx + targetOffset;
	}


	public void finish() {
		this.finished = true;
	}


	public boolean isFinished() {
		return finished;
	}


	/** Analyze a switch case and return helpful information about it's bytecode layout.
	 * Used by {@link #loadTableSwitch(int, byte[], List, AtomicReference) and {@link #loadLookupSwitch(int, byte[], List, AtomicReference)}
	 * @param caseMatch the value to match for this case in the switch
	 * @param targetIdx the target {@code instr} index at which the case's code begins
	 * @param instr the method bytecode array
	 * @return the analyzed switch information
	 */
	public static JumpConditionInfo loadConditionFlow(Opcodes opc, int idx, int targetOffset, byte[] instr) {
		// analyze code flow path
		var condFlowPath = new IntArrayList();
		condFlowPath.add(~idx);
		CodeFlow.getFlowPaths(idx, instr, condFlowPath);

		// potential end index (probably redundant once code flow is working)
		var maxCodeFlowIndex = CodeFlow.maxIndex(condFlowPath);

		return new JumpConditionInfo(opc, idx, targetOffset, maxCodeFlowIndex, condFlowPath);
	}


	public static int findLoopStart(int curIdx, int jumpRelative, List<JumpConditionInfo> loops) {
		// Loops are generally compiled using a GOTO and an IF_* instruction
		// form 1: [..., GOTO <setup_if[0]>, instructions[], setup_if[], IF_* <instructions[0]>, ...]
		if(jumpRelative < 0) {
			var jumpToIdx = curIdx + jumpRelative - 3; // GOTO has a 2 byte operand so -3 is the GOTO instruction index right before the jump destination (which is the first instruction in a loop)
			for(int i = loops.size() - 1; i >= 0; i--) {
				var cond = loops.get(i);
				if(cond.getOpcodeIndex() == jumpToIdx) {
					return i;
				}
			}
		}
		return -1;
	}


	public static int findLoopEnd(int curIdx, int numOperands, int jumpRelative, List<JumpConditionInfo> loops) {
		if(jumpRelative > 0) {
			var instAfterJumpIdx = curIdx + numOperands + 1;
			for(int i = loops.size() - 1; i >= 0; i--) {
				var cond = loops.get(i);
				if(cond.getTargetIndex() == instAfterJumpIdx) {
					return i;
				}
			}
		}
		return -1;
	}

}
