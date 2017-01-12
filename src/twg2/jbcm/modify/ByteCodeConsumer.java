package twg2.jbcm.modify;

import twg2.jbcm.Opcodes;

/** An operation that accepts a code block and location as input and returns no result.
 * @author TeamworkGuy2
 * @since 2014-4-19
 */
@FunctionalInterface
public interface ByteCodeConsumer {

	/** Performs this operation on the given argument.
	 * @param opcode the opcode of the instruction at the specified location
	 * @param code the array of code instructions
	 * @param location the location in the array at which the current instruction is location
	 */
	void accept(Opcodes opcode, byte[] code, int location);

}
