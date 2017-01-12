package twg2.jbcm.classFormat.attributes;

import twg2.jbcm.classFormat.ReadWritable;
import twg2.jbcm.classFormat.Settings;

/** Interface for all Attribute types in a Java class file format.<br/>
 * Remember to add new attribute types to the
 * {@link Settings#loadAttributeObject(java.io.DataInput, twg2.jbcm.classFormat.ClassFile, Code) Settings.loadAttributeObject()}
 * method.
 * @author TeamworkGuy2
 * @since 2013-7-7
 */
public interface Attribute_Type extends ReadWritable {

	public String getAttributeName();

	public int getAttributeLength();

	@Override
	public String toString();

}
