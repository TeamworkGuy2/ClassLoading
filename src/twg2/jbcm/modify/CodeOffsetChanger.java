package twg2.jbcm.modify;

/** An operation that accepts a code block, location, and offset as input and returns the location of the next instruction.
 * @author TeamworkGuy2
 * @since 2014-4-19
 */
public interface CodeOffsetChanger {

	/** Performs this operation on the given argument.
	 * @param code the array of code instructions
	 * @param location the location in the array at which the current instruction is located
	 * @param offset the constant_pool index offset to add to the instruction's index value(s)
	 */
	void shiftIndex(byte[] code, int location, int offset);

}

