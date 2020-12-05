package twg2.jbcm.modify;

import twg2.jbcm.IoUtility;

/**
 * @author TeamworkGuy2
 * @since 2020-12-3
 */
public class LookupswitchOffsetModifier implements CodeOffsetChanger {

	public static final CodeOffsetChanger defaultInst = new LookupswitchOffsetModifier();


	/** Add an offset to all of the lookupswitch instructions in the
	 * specified chunk of code.
	 * @param offset the offset to adjust all lookupswitch offsets by.
	 * TODO this must be divisible by 4 at the moment.
	 * @param code the array of instructions to manipulate
	 * TODO return the next opcode location following this instruction
	 */
	@Override
	public void shiftIndex(byte[] code, int location, int offset) {
		byte op = code[location];
		if(op == 171) {
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
			for(int ii = 0; ii < npairs; ii++, location += 8) {
				int matchOffset = IoUtility.readInt(code, location + 4);
				matchOffset += offset;
				IoUtility.writeInt(matchOffset, code, location + 4);
			}
		}
		else {
			throw new IllegalStateException("Expected opcode 171 at location " + location + ", found opcode " + code[location] + " instead");
		}
	}

}