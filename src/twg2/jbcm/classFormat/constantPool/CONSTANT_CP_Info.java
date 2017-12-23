package twg2.jbcm.classFormat.constantPool;

import twg2.jbcm.classFormat.ReadWritable;

/** Interface for all Constant Pool info types in a Java class file format
 * @author TeamworkGuy2
 * @since 2013-7-7
 */
public interface CONSTANT_CP_Info extends ReadWritable {

	public int getTag();

	@Override
	public String toString();

}
