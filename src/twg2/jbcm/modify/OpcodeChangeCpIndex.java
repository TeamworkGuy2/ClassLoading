package twg2.jbcm.modify;

/** An operation that accepts a code block, location, and index offset as input.
 * @author TeamworkGuy2
 * @since 2014-4-20
 */
public interface OpcodeChangeCpIndex {

	/** Performs this operation on the given argument.
	 * @param code the array of code instructions
	 * @param location the location in the array at which the current instruction is located
	 * @param offset the constant_pool index offset to add to the instruction's index value(s)
	 */
	void shiftIndex(byte[] code, int location, int offset);


	/** Change a constant_pool index if it matches the current given index.
	 * If the constant_pool index equals {@code currentIndex}, then set it to {@code newIndex} 
	 * @param code the array of code instructions
	 * @param location the location in the array at which the current instruction is located
	 * @param currentIndex the index to compare the current constant_pool index to
	 * @param newIndex if the current constant_pool index matches the {@code currentIndex}, set it to {@code newIndex}.
	 */
	void changeCpIndexIf(byte[] code, int location, int currentIndex, int newIndex);

}
