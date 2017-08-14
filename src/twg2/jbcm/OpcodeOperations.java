package twg2.jbcm;

import twg2.jbcm.modify.CpIndexChanger;
import twg2.jbcm.modify.CodeOffsetChanger;

/** Contains change operations
 * @author TeamworkGuy2
 * @since 2014-4-20
 */
public class OpcodeOperations {
	public static final OpcodeOperations EMPTY =
			new OpcodeOperations(IoUtility.EMPTY_OPCODE_CP_INDEX, IoUtility.EMPTY_OPCODE_OFFSET);
	private CpIndexChanger cpIndex;
	private CodeOffsetChanger codeOffset;


	public OpcodeOperations(CpIndexChanger cpIndex, CodeOffsetChanger codeOffset) {
		this.cpIndex = cpIndex;
		this.codeOffset = codeOffset;
	}


	public CpIndexChanger getCpIndexModifier() {
		return cpIndex;
	}


	public CodeOffsetChanger getAttributeOffsetModifier() {
		return codeOffset;
	}

}
