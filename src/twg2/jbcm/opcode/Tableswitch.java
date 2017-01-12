package twg2.jbcm.opcode;

import twg2.jbcm.IoUtility;
import twg2.jbcm.OffsetOpcode;
import twg2.jbcm.modify.OpcodeChangeOffset;

/**
 * Stack:<br/>
 * before: ..., index {@literal ->}<br/>
 * after : ...
 * @author TeamworkGuy2
 * @since 2014-4-19
 */
public class Tableswitch implements OffsetOpcode {
	private static final int OPCODE = 170; // 0xAA

	private static final OpcodeChangeOffset offsetAction = new OpcodeChangeOffset() {
		/** Add an offset to all of the tableswitch instructions in the
		 * specified chunk of code
		 * @param offset the offset to adjust all tableswitch offsets by.
		 * TODO this must be divisible by 4 at the moment.
		 * @param code the array of instructions to manipulate
		 */
		@Override public void shiftIndex(byte[] code, int location, int offset) {
			byte op = code[location];
			if(op == OPCODE) {
				location++;
				// Skip padding
				int padding = (location % 4);
				location+=padding;
				// Default 32bit offset
				int defaultOffset = IoUtility.readInt(code, location);
				defaultOffset += offset;
				IoUtility.writeInt(defaultOffset, code, location);
				location += 4;
				// low
				int low = IoUtility.readInt(code, location);
				location += 4;
				// high
				int high = IoUtility.readInt(code, location);
				location += 4;
				// For each jump-offset 32bit value
				for(int ii = 0; ii < (high-low+1); ii++, location += 4) {
					int matchOffset = IoUtility.readInt(code, location);
					matchOffset += offset;
					IoUtility.writeInt(matchOffset, code, location);
				}
			}
		}
	};


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
