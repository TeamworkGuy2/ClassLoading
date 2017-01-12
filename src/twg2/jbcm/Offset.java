package twg2.jbcm;

import twg2.jbcm.modify.OpcodeChangeOffset;

/** An interface that is implemented by attributes or instructions that
 * have an offset value in their metadata or opcodes.
 * @author TeamworkGuy2
 * @since 2014-4-20
 */
public interface Offset {

	public OpcodeChangeOffset getOffsetModifier();

}
