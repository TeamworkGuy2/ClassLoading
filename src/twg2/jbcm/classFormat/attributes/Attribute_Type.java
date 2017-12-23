package twg2.jbcm.classFormat.attributes;

import twg2.jbcm.classFormat.ClassFileAttributes;
import twg2.jbcm.classFormat.ReadWritable;

/** Interface for all Attribute types in a Java class file format.<br/>
 * Remember to add new attribute types to the {@link ClassFileAttributes} enum.
 * @author TeamworkGuy2
 * @since 2013-7-7
 */
public interface Attribute_Type extends ReadWritable {

	public String getAttributeName();

	public int getAttributeLength();

	@Override
	public String toString();

}
