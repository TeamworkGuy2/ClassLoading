package twg2.jbcm.modify;

/** An operation that accepts a single input and returns no result.
 * This is for compatibility with Java 7 or older versions.
 * TODO Java 8
 * @author TeamworkGuy2
 * @since 2014-4-19
 * @param <T> the type of input to this operation
 */
public interface Consumer<T> {

	/** Performs this operation on the given argument.
	 * @param t the input argument
	 */
	void accept(T t);

}

