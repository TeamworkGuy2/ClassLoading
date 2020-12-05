package twg2.jbcm;

import twg2.jbcm.modify.CpIndexChanger;
import twg2.jbcm.modify.CodeOffsetChanger;
import twg2.jbcm.modify.CodeOffsetGetter;

/** Contains change operations
 * @author TeamworkGuy2
 * @since 2014-4-20
 */
public class OpcodeOperations {
	public static final OpcodeOperations EMPTY = new OpcodeOperations(CodeOffsetGetter.RETURN_ZERO, CpIndexChanger.NO_OP, CodeOffsetChanger.NO_OP);

	private CodeOffsetGetter codeOffsetGetter;
	private CpIndexChanger cpIndexChanger;
	private CodeOffsetChanger codeOffsetChanger;


	public OpcodeOperations(CodeOffsetGetter codeOffsetGetter, CpIndexChanger cpIndexChanger, CodeOffsetChanger codeOffsetChanger) {
		this.codeOffsetGetter = codeOffsetGetter;
		this.cpIndexChanger = cpIndexChanger;
		this.codeOffsetChanger = codeOffsetChanger;
	}


	public CodeOffsetGetter getCodeOffsetGetter() {
		return codeOffsetGetter;
	}


	public CpIndexChanger getCpIndexModifier() {
		return cpIndexChanger;
	}


	public CodeOffsetChanger getOffsetModifier() {
		return codeOffsetChanger;
	}

}
