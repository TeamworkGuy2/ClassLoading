package twg2.jbcm;

import twg2.jbcm.modify.OpcodeChangeCpIndex;
import twg2.jbcm.modify.OpcodeChangeOffset;

/**
 * @author TeamworkGuy2
 * @since 2014-4-20
 */
public class OpcodeOperations {
	public static final OpcodeOperations EMPTY =
			new OpcodeOperations(IoUtility.EMPTY_OPCODE_CP_INDEX, IoUtility.EMPTY_OPCODE_OFFSET);
	private OpcodeChangeCpIndex cpIndex;
	private OpcodeChangeOffset codeOffset;


	public OpcodeOperations(OpcodeChangeCpIndex cpIndex, OpcodeChangeOffset codeOffset) {
		this.cpIndex = cpIndex;
		this.codeOffset = codeOffset;
	}


	public OpcodeChangeCpIndex getCpIndexModifier() {
		return cpIndex;
	}


	public OpcodeChangeOffset getAttributeOffsetModifier() {
		return codeOffset;
	}

}
