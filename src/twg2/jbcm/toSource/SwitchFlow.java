package twg2.jbcm.toSource;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import twg2.collections.primitiveCollections.IntArrayList;
import twg2.jbcm.CodeFlow;
import twg2.jbcm.Opcodes;
import twg2.jbcm.Opcodes.Type;
import twg2.jbcm.ir.SwitchCase;

import static twg2.jbcm.CodeUtility.loadOperands;

/** Analyze the flow of a switch statement through bytecode
 * https://docs.oracle.com/javase/specs/jls/se11/html/jls-14.html#jls-14.11
 * https://docs.oracle.com/javase/specs/jvms/se11/html/jvms-3.html#jvms-3.10
 * @author TeamworkGuy2
 * @since 2020-12-01
 */
public class SwitchFlow {

	/**
	 * @param curIdx the current code instruction index
	 * @param cases the cases, pairs of values, the first is the case match value the second is the target index
	 * @param offset
	 * @return
	 */
	public static int findSwitchCase(int curIdx, List<SwitchCase> cases, int offset) {
		for(int i = offset, size = cases.size(); i < size; i++) {
			if(cases.get(i).caseTarget == curIdx) {
				return i;
			}
		}
		return -1;
	}


	/** Reads a 'tableswitch' instruction statement starting at the specified index
	 * @param i the 'tableswitch' instruction index
	 * @param instr the method bytecode array
	 * @param dstCases extracted {@link SwitchCase} info is stored in this list
	 * @param dstSwitchDefault the switch default case info is stored in this reference
	 * @return the number of {@code instr} bytes contained within this switch statement,
	 * i.e. how many {@code instr} indices to skip to get to the next instruction after the switch statement
	 */
	public static int loadTableSwitch(int i, byte[] instr, List<SwitchCase> dstCases, AtomicReference<SwitchCase> dstSwitchDefault) {
		dstCases.clear();
		var padding = 4 - ((i + 1) % 4);
		var defaultTargetIdx = i + loadOperands(4, instr, i + padding);
		var lower = loadOperands(4, instr, i + padding + 4);
		var upper = loadOperands(4, instr, i + padding + 8);
		for(int j = 0, cnt = upper - lower; j <= cnt; j++) {
			var caseTarget = i + loadOperands(4, instr, i + padding + 12 + j * 4);
			dstCases.add(loadSwitchCase(lower + j, caseTarget, instr));
		}
		dstSwitchDefault.set(loadSwitchCase(0, defaultTargetIdx, instr));
		return padding + 12 + (upper - lower + 1) * 4;
	}


	/** Reads a 'lookupswitch' instruction statement starting at the specified index
	 * @param i the 'lookupswitch' instruction index
	 * @param instr the method bytecode array
	 * @param dstCases extracted {@link SwitchCase} info is stored in this list
	 * @param dstSwitchDefault the switch default case info is stored in this reference
	 * @return the number of {@code instr} bytes contained within this switch statement,
	 * i.e. how many {@code instr} indices to skip to get to the next instruction after the switch statement
	 */
	public static int loadLookupSwitch(int i, byte[] instr, List<SwitchCase> dstCases, AtomicReference<SwitchCase> dstSwitchDefault) {
		dstCases.clear();
		var padding = 4 - ((i + 1) % 4);
		var defaultTargetIdx = i + loadOperands(4, instr, i + padding);
		var pairs = loadOperands(4, instr, i + padding + 4);
		for(int j = 0; j < pairs; j++) {
			var match = loadOperands(4, instr, i + padding + 8 + j * 8);
			var caseTarget = i + loadOperands(4, instr, i + padding + 8 + j * 8 + 4);
			dstCases.add(loadSwitchCase(match, caseTarget, instr));
		}
		dstSwitchDefault.set(loadSwitchCase(0, defaultTargetIdx, instr));
		return padding + 8 + pairs * 8;
	}


	/** Analyze a switch case and return helpful information about it's bytecode layout.
	 * Used by {@link #loadTableSwitch(int, byte[], List, AtomicReference) and {@link #loadLookupSwitch(int, byte[], List, AtomicReference)}
	 * @param caseMatch the value to match for this case in the switch
	 * @param targetIdx the target {@code instr} index at which the case's code begins
	 * @param instr the method bytecode array
	 * @return the analyzed switch information
	 */
	public static SwitchCase loadSwitchCase(int caseMatch, int targetIdx, byte[] instr) {
		// analyze code flow path
		var caseFlowPath = new IntArrayList();
		caseFlowPath.add(~targetIdx);
		CodeFlow.getFlowPaths(targetIdx, instr, caseFlowPath);

		// potential end index (probably redundant once code flow is working)
		var endJumpIdx = IterateCode.nextJumpOrEndIndex(targetIdx, instr);
		var endOpc = Opcodes.get(instr[endJumpIdx] & 0xFF);

		if(endOpc.hasBehavior(Type.JUMP)) {
			var jumpTarget = loadOperands(endOpc.getOperandCount(), instr, endJumpIdx);
			var caseEndTarget = endJumpIdx + (endOpc.getOperandCount() == 2 ? (short)jumpTarget : jumpTarget);
			return new SwitchCase(caseMatch, targetIdx, endJumpIdx, caseEndTarget, caseFlowPath);
		}
		else {
			return new SwitchCase(caseMatch, targetIdx, endJumpIdx, caseFlowPath);
		}
	}


	/** Analyze switch cases to find a common end index
	 * @param cases the switch cases
	 * @param defaultCase the default case
	 * @param instr the bytecode array
	 * @return an {@code instr} index which call case code flows eventually reach, likely the end of the switch statement, or -1 if no common code flow point is not found
	 */
	public static int commonSwitchEndIndex(List<SwitchCase> cases, SwitchCase defaultCase, byte[] instr) {
		int commonFlowPoint = Integer.MAX_VALUE;
		int caseCnt = cases.size();

		// the code flow for a switch case traces all the code paths starting from a case target throughout the rest of the method code by following jumps, branches, returns, and throw instructions
		var caseFlow = defaultCase.getCodeFlow();

		// loop over all the code flow points for the default switch case
		flow_points:
		for(int j = 0, flowSize = caseFlow.size(); j < flowSize; j++) {
			int flowPoint = caseFlow.get(j);
			// loop over all the switch cases and check if they all share this flow point
			for(int k = 0; k < caseCnt; k++) {
				var flowK = cases.get(k).getCodeFlow();
				if(!flowK.contains(flowPoint)) {
					continue flow_points;
				}
			}
			commonFlowPoint = Math.min(commonFlowPoint, flowPoint);
		}

		return commonFlowPoint < Integer.MAX_VALUE ? commonFlowPoint : -1;
	}


	// TODO old from before CodeFlow
	public static int commonSwitchEndIndex_OLD(List<SwitchCase> cases, byte[] instr) {
		int minEnd = Integer.MAX_VALUE;
		int maxEnd = -1;
		for(var caseObj : cases) {
			// TODO how to handle IF_* in switch case
			if(caseObj.hasEndTarget) {
				var jumpTarget = caseObj.caseEndTarget;
				minEnd = Math.min(minEnd, jumpTarget);
				maxEnd = Math.max(maxEnd, jumpTarget);
			}
		}
		return minEnd == maxEnd ? maxEnd : -1;
	}


	// TODO old from before CodeFlow
	public static boolean isSwitchPacked_OLD(List<SwitchCase> cases, byte[] instr) {
		int prevEnd = -1;
		int prevOperands = 0;
		for(var caseObj : cases) {
			var nextIndexAfterPrev = prevEnd + prevOperands + 1;
			if(prevEnd >= 0 && nextIndexAfterPrev < caseObj.caseTarget) {
				return false;
			}
			prevEnd = caseObj.caseEndIdx;
			var endOpc = Opcodes.get(instr[prevEnd] & 0xFF);
			var operands = endOpc.getOperandCount();
			prevOperands = operands;
		}
		return true;
	}

}
