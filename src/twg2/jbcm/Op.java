package twg2.jbcm;

import twg2.jbcm.modify.CpIndexChanger;
import twg2.jbcm.modify.CodeOffsetChanger;

/**
 * @author TeamworkGuy2
 * @since 2014-4-20
 */
public final class Op {
	private CpIndexChanger cpIndex;
	private CodeOffsetChanger codeOffset;

	private Op() {}


	public final Op add(CpIndexChanger cpIndex) {
		this.cpIndex = cpIndex;
		return this;
	}


	public final Op add(CodeOffsetChanger codeOffset) {
		this.codeOffset = codeOffset;
		return this;
	}


	public final OpcodeOperations create() {
		OpcodeOperations oper = new OpcodeOperations(cpIndex, codeOffset);
		return oper;
	}


	public static final Op of(CpIndexChanger cpIndex) {
		Op op = new Op();
		return op.add(cpIndex);
	}


	public static final Op of(CodeOffsetChanger codeOffset) {
		Op op = new Op();
		return op.add(codeOffset);
	}

}
