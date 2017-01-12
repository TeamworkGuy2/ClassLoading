package twg2.jbcm;

import twg2.jbcm.modify.OpcodeChangeCpIndex;
import twg2.jbcm.modify.OpcodeChangeOffset;

/**
 * @author TeamworkGuy2
 * @since 2014-4-20
 */
public final class Op {
	private OpcodeChangeCpIndex cpIndex;
	private OpcodeChangeOffset codeOffset;

	private Op() {}


	public final Op add(OpcodeChangeCpIndex cpIndex) {
		this.cpIndex = cpIndex;
		return this;
	}


	public final Op add(OpcodeChangeOffset codeOffset) {
		this.codeOffset = codeOffset;
		return this;
	}


	public final OpcodeOperations create() {
		OpcodeOperations oper = new OpcodeOperations(cpIndex, codeOffset);
		return oper;
	}


	public static final Op of(OpcodeChangeCpIndex cpIndex) {
		Op op = new Op();
		return op.add(cpIndex);
	}


	public static final Op of(OpcodeChangeOffset codeOffset) {
		Op op = new Op();
		return op.add(codeOffset);
	}

}
