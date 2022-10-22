package twg2.jbcm.toSource.structures;

/**
 * @author TeamworkGuy2
 * @since 2022-10-09
 */
public enum EmitterResponse {
	/** The emitter processed the input and does not wish to signal any change */
	CONTINUE,
	/** The emitter consumed the input and no other emitter should be given the same input to process */
	CONSUMED,
	/** The emitter is done and should be deregistered, it should not be given any further inputs */
	DEREGISTER,
}
