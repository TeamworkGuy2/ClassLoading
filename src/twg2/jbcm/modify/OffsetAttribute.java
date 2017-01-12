package twg2.jbcm.modify;

import twg2.jbcm.classFormat.attributes.Code_Attribute;

/** An interface that is implemented by attributes that
 * have a code related offset value in their metadata.
 * @author TeamworkGuy2
 * @since 2014-4-20
 */
public interface OffsetAttribute extends Code_Attribute {

	/**
	 * @return the {@link AttributeOffsetFunction} that can be used to modify
	 * this attribute's offsets
	 */
	public AttributeOffsetFunction getAttributeOffsetModifier();

}
