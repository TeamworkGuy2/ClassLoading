package twg2.jbcm.modify;

/** An operation that accepts a code range and a shift value as input and shifts
 * instructions/attributes in that range the specified shift distance.
 * TODO implementation makes no sense, need to modify
 * @author TeamworkGuy2
 * @since 2014-4-19
 */
public interface AttributeOffsetFunction {

	/** Performs this operation on the given argument.
	 * @param offset the offset in the method's code at which to start shifting offsets
	 * @param length the number of instruction bytes this offset shift applies to
	 * @param shift the number of bytes to shift instruction offsets by
	 */
	void changeOffset(int offset, int length, int shift);

}