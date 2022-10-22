package twg2.jbcm;

import twg2.jbcm.modify.CodeCpIndexChanger;
import twg2.jbcm.modify.CodeOffsetChanger;
import twg2.jbcm.modify.CodeOffsetGetter;

/**
 * @author TeamworkGuy2
 * @since 2014-4-20
 */
public class Op {
	private CodeOffsetGetter offsetGetter;
	private CodeCpIndexChanger cpIndex;
	private CodeOffsetChanger codeOffset;

	private Op() {}


	public Op add(CodeCpIndexChanger cpIndex) {
		this.cpIndex = cpIndex;
		// detect if index change implementation is also an offset getter and save it for that purpose
		if(cpIndex instanceof CodeOffsetGetter) {
			this.offsetGetter = (CodeOffsetGetter)cpIndex;
		}
		return this;
	}


	public Op add(CodeOffsetChanger codeOffset) {
		this.codeOffset = codeOffset;
		// detect if index change implementation is also an offset getter and save it for that purpose
		if(codeOffset instanceof CodeOffsetGetter) {
			this.offsetGetter = (CodeOffsetGetter)codeOffset;
		}
		return this;
	}


	public OpcodeOperations create() {
		OpcodeOperations oper = new OpcodeOperations(offsetGetter, cpIndex, codeOffset);
		return oper;
	}


	public static Op of(CodeCpIndexChanger cpIndex) {
		Op op = new Op();
		return op.add(cpIndex);
	}


	public static Op of(CodeOffsetChanger codeOffset) {
		Op op = new Op();
		return op.add(codeOffset);
	}

}
