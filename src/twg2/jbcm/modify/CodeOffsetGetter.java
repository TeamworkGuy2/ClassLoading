package twg2.jbcm.modify;

/**
 * @author TeamworkGuy2
 * @since 2020-12-03
 */
@FunctionalInterface
public interface CodeOffsetGetter {

	public static final CodeOffsetGetter RETURN_ZERO = new CodeOffsetGetter() {
		@Override public int getOffset(byte[] code, int location) {
			return 0;
		}
	};


	/** Get the offset associated with the instruction at the {@code location} in the {@code code}.
	 * @param code the bytecode array
	 * @param location the location in the bytecode array at which the instruction to load is located
	 * @return the offset location this instruction points to
	 */
	int getOffset(byte[] code, int location);

}
