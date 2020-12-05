package twg2.jbcm.modify;

/** An operation that accepts a code block, location, and offset as input and returns or modifies the offset location.
 * @author TeamworkGuy2
 * @since 2014-4-19
 */
public interface CodeOffsetChanger {

	public static final CodeOffsetChanger NO_OP = new CodeOffsetChanger() {
		@Override public void shiftIndex(byte[] code, int location, int offset) {
		}
	};


	/** Add an offset to code location or constant_pool index operands used by the instruction at {@code location}.
	 * @param code the bytecode array
	 * @param location the location in the bytecode array at which the instruction to check and modify is located
	 * @param offset the offset to add to the instruction's code location(s) or constant_pool index(es)
	 */
	void shiftIndex(byte[] code, int location, int offset);

}
