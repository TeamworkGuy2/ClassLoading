package twg2.jbcm;

import twg2.jbcm.modify.CodeCpIndexChanger;
import twg2.jbcm.modify.CodeOffsetChanger;
import twg2.jbcm.modify.CodeOffsetGetter;

/** Contains change operations
 * @author TeamworkGuy2
 * @since 2014-4-20
 */
public class OpcodeOperations {
	public static final OpcodeOperations EMPTY = new OpcodeOperations(CodeOffsetGetter.RETURN_ZERO, CodeCpIndexChanger.NO_OP, CodeOffsetChanger.NO_OP);

	private CodeOffsetGetter codeOffsetGetter;
	private CodeCpIndexChanger codeCpIndexChanger;
	private CodeOffsetChanger codeOffsetChanger;


	public OpcodeOperations(CodeOffsetGetter codeOffsetGetter, CodeCpIndexChanger codeCpIndexChanger, CodeOffsetChanger codeOffsetChanger) {
		this.codeOffsetGetter = codeOffsetGetter;
		this.codeCpIndexChanger = codeCpIndexChanger;
		this.codeOffsetChanger = codeOffsetChanger;
	}


	public CodeOffsetGetter getCodeOffsetGetter() {
		return codeOffsetGetter;
	}


	public CodeCpIndexChanger getCpIndexModifier() {
		return codeCpIndexChanger;
	}


	public CodeOffsetChanger getOffsetModifier() {
		return codeOffsetChanger;
	}

}
