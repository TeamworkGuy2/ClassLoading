package twg2.jbcm.ir;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import twg2.jbcm.Opcodes;

/** Contains the beginning and end opcodes and targets for a goto/if condition pair
 * @author TeamworkGuy2
 * @since 2020-08-15
 */
public class JumpConditionInfo {

	/**
	 * @author TeamworkGuy2
	 * @since 2022-09-30
	 */
	public enum UsageHint {
		FOR_OR_WHILE_LOOP(16),
		DO_WHILE_LOOP(16),
		IF(1),
		IF_WITHIN_LOOP_AND_WITH_LOOP_ENDS(1);

		/** Usage hint type/category:
		 * 1 = IF/ELSE<br>
		 * 16 = LOOP<br>
		 */
		private final byte type;

		UsageHint(int type) {
			this.type = (byte)type;
		}

		public static boolean isLoop(UsageHint hint) {
			return hint != null && hint.type == 16;
		}
	}


	/** Sort jump conditions based on lower index and greatest range between lower and upper.<br>
	 * 1. the lower of the {@link JumpConditionInfo#getLowerIndex()}<br>
	 * 2. the greater of the {@link JumpConditionInfo#getUpperIndex()}<br>
	 */
	public static final Comparator<? super JumpConditionInfo> LOWER_INDEX_SORTER = (JumpConditionInfo o1, JumpConditionInfo o2) -> {
		int lowerIdxDiff = o1.getLowerIndex() - o2.getLowerIndex();
		return lowerIdxDiff == 0 ? o2.getUpperIndex() - o1.getUpperIndex() : lowerIdxDiff;
	};

	public final Opcodes opc;
	public final int opcIdx;
	public final int targetOffset;
	/** It is not always clear which IF_* instruction belongs to a loop (since loops are commonly detected via backward jumps (GOTOs).
	 * This value is a best guess of the code index of an IF_* instruction that belongs to the setup code for this loop.
	 * This index will be between the {@link #getTargetIndex()} and {@code opcIdx}
	 */
	public final int potentialIfIndex;
	/** If {@code usageHint ==}{@link UsageHint#IF_WITHIN_LOOP_AND_WITH_LOOP_ENDS} then this index should be set with the
	 * end index of the loop that this condition resides within. This condition and the loop end at the same index.
	 */
	public final int loopEndIndexForIf;
	/** A {@link UsageHint} best guess as to the structural usage of this condition within the code, i.e. whether it is a
	 * {@code for} loop or <code>do { } while()</code> loop or {@code if} statement.
	 */
	public final UsageHint usageHint;


	public JumpConditionInfo(Opcodes opc, int opcIdx, int targetOffset,
			int potentialIfIndex, int loopEndIndexForIf, UsageHint usageHint) {
		this.opc = opc;
		this.opcIdx = opcIdx;
		this.targetOffset = targetOffset;
		this.potentialIfIndex = potentialIfIndex;
		this.loopEndIndexForIf = loopEndIndexForIf;
		this.usageHint = usageHint;
	}


	/** The jump cause opcode, i.e. GOTO or IF*
	 */
	public Opcodes getOpcode() {
		return opc;
	}


	/**
	 * @return the index of the opcode within it's method code array
	 */
	public int getOpcodeIndex() {
		return opcIdx;
	}


	/** The jump target index in the method's code array
	 */
	public int getTargetIndex() {
		return opcIdx + targetOffset;
	}


	/** Get the lower index of {@link #getOpcodeIndex()} and {@link #getTargetIndex()}.<br>
	 * For most if-statements the opcode index is lower.<br>
	 * For most loops the target index is lower.
	 */
	public int getLowerIndex() {
		return opcIdx + (targetOffset < 0 ? targetOffset : 0);
	}


	/** Get the higher index of {@link #getOpcodeIndex()} and {@link #getTargetIndex()}.<br>
	 * For most if-statements the jump/target index index is higher.<br>
	 * For most loops the opcode index is higher.
	 */
	public int getUpperIndex() {
		return opcIdx + (targetOffset >= 0 ? targetOffset : 0);
	}


	/** Create a clone of this object with a new {@link #potentialIfIndex}
	 * @param potentialIfIndex potential index of the IF_* opcode at the beginning of this loop, if it is a loop, otherwise -1
	 */
	public JumpConditionInfo withPotentialIfIndex(int index) {
		return new JumpConditionInfo(opc, opcIdx, targetOffset, index, loopEndIndexForIf, usageHint);
	}


	public JumpConditionInfo withLoopEndIndexForIf(int loopEndTarget) {
		return new JumpConditionInfo(opc, opcIdx, targetOffset, potentialIfIndex, loopEndTarget, UsageHint.IF_WITHIN_LOOP_AND_WITH_LOOP_ENDS);
	}


	/** Create a clone of this object with a new {@link #usageHint}
	 */
	public JumpConditionInfo withUsageHint(UsageHint hint) {
		return new JumpConditionInfo(opc, opcIdx, targetOffset, potentialIfIndex, loopEndIndexForIf, hint);
	}


	@Override
	public String toString() {
		return "condition at " + this.opcIdx + " (" + this.opc + ")";
	}


	/** Analyze the flow of a sub-section of code and return information about its bytecode layout.<br>
	 * Used by {@link #loadTableSwitch(int, byte[], List, AtomicReference) and {@link #loadLookupSwitch(int, byte[], List, AtomicReference)}
	 * @param opc the instruction op-code
	 * @param idx the {@code instr} index at which the {@code opc} instruction is located 
	 * @param targetOffset offset from {@code idx} into the {@code instr} array that this condition points to
	 * @param code the method bytecode array
	 * @param usageHint a best guess hint of the jump condition's usage
	 * @return the analyzed code flow information
	 */
	public static JumpConditionInfo loadConditionFlow(Opcodes opc, int idx, int targetOffset, byte[] code, UsageHint usageHint) {
		// follow an array of jump points found in the code
		//var condFlowPath = CodeFlow.getFlowPaths(code, idx);

		// potential end index (probably redundant once code flow is working)
		//var maxCodeFlowIndex = CodeFlow.maxIndex(condFlowPath);

		return new JumpConditionInfo(opc, idx, targetOffset, -1, -1, usageHint);
	}

}
