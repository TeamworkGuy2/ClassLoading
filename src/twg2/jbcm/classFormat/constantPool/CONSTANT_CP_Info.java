package twg2.jbcm.classFormat.constantPool;

import twg2.jbcm.classFormat.ReadWritable;
import twg2.jbcm.modify.CpIndexChangeable;

/** Interface for all Constant Pool info types in a Java class file format
 * @author TeamworkGuy2
 * @since 2013-7-7
 */
public interface CONSTANT_CP_Info extends ReadWritable, CpIndexChangeable {

	public int getTag();

	public String toShortString();

	@Override
	public String toString();

}
