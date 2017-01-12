package twg2.jbcm.opcode;

import twg2.jbcm.IoUtility;
import twg2.jbcm.OffsetOpcode;
import twg2.jbcm.modify.OpcodeChangeOffset;

/**
 * Stack<br/>
 * before: ..., key {@literal ->}<br/>
 * after : ...
 * @author TeamworkGuy2
 * @since 2014-4-19
 */
public class Lookupswitch implements OffsetOpcode {
	private static final int OPCODE = 171; // 0xAB

	private static final OpcodeChangeOffset offsetAction = new OpcodeChangeOffset() {
		/** Add an offset to all of the lookupswitch instructions in the
		 * specified chunk of code
		 * @param offset the offset to adjust all lookupswitch offsets by.
		 * TODO this must be divisible by 4 at the moment.
		 * @param code the array of instructions to manipulate
		 */
		@Override public void shiftIndex(byte[] code, int location, int offset) {
			byte op = code[location];
			if(op == OPCODE) {
				location++;
				// Skip padding
				int padding = (location % 4);
				location += padding;
				// Default 32bit offset
				int defaultOffset = IoUtility.readInt(code, location);
				defaultOffset += offset;
				IoUtility.writeInt(defaultOffset, code, location);
				location += 4;
				// Number of pairs
				int npairs = IoUtility.readInt(code, location);
				location += 4;
				// For each pair of match-offset 32bit values
				for(int ii = 0; ii < npairs; ii++, location+=8) {
					int matchOffset = IoUtility.readInt(code, location + 4);
					matchOffset += offset;
					IoUtility.writeInt(matchOffset, code, location + 4);
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
