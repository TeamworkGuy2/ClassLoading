package twg2.jbcm.opcode;

import twg2.jbcm.IoUtility;
import twg2.jbcm.OffsetOpcode;
import twg2.jbcm.modify.OpcodeChangeOffset;

/** Branch if {@code value <= value2}<br/>
 * Stack:<br/>
 * before: ..., value {@literal ->}<br/>
 * after : ...
 * @author TeamworkGuy2
 * @since 2014-4-19
 */
public final class Ifle implements OffsetOpcode {
	private static final int OPCODE = 158; // 0x9E

	private static final OpcodeChangeOffset offsetAction = IoUtility.offsetModifier(OPCODE, 1, 2);


	@Override
	public final OpcodeChangeOffset getOffsetModifier() {
		return offsetAction;
	}


	public static final OpcodeChangeOffset getCodeOffsetModifier() {
		return offsetAction;
	}


	@Override
	public final int opcode() {
		return OPCODE;
	}


	public static final int getOpcode() {
		return OPCODE;
	}

}
