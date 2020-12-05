package twg2.jbcm.modify;

import twg2.jbcm.IoUtility;

public class TableswitchOffsetModifier implements CodeOffsetChanger {

	public static final CodeOffsetChanger defaultInst = new TableswitchOffsetModifier();


	/** Add an offset to all of the tableswitch instructions in the
	 * specified chunk of code.
	 * @param offset the offset to adjust all tableswitch offsets by.
	 * TODO this must be divisible by 4 at the moment.
	 * @param code the array of instructions to manipulate
	 * TODO return the next opcode location following this instruction
	 */
	@Override
	public void shiftIndex(byte[] code, int location, int offset) {
		byte op = code[location];
		if(op == 170) {
			location++;
			// Skip padding
			int padding = (location % 4);
			location += padding;
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
		else {
			throw new IllegalStateException("Expected opcode 170 at location " + location + ", found opcode " + code[location] + " instead");
		}
	}
}