package twg2.jbcm.ir;

import twg2.jbcm.Opcodes;

/** Contains the beginning and end opcodes and targets for a goto/if condition pair
 * @author TeamworkGuy2
 * @since 2020-08-15
 */
public class JumpConditionInfo {
	private final Opcodes opc;
	private final int opcIdx;
	private final int targetOffsetIdx;
	private boolean finished;


	public JumpConditionInfo(Opcodes opc, int opcIdx, int targetOffsetIdx) {
		this.opc = opc;
		this.opcIdx = opcIdx;
		this.targetOffsetIdx = targetOffsetIdx;
	}


	public Opcodes getOpcode() {
		return opc;
	}


	public int getOpcodeIndex() {
		return opcIdx;
	}


	public int getTargetOffset() {
		return targetOffsetIdx;
	}


	public int getTargetIndex() {
		return opcIdx + targetOffsetIdx;
	}


	public void finish() {
		this.finished = true;
	}


	public boolean isFinished() {
		return finished;
	}

}
