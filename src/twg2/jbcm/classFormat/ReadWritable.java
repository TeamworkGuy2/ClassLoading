package twg2.jbcm.classFormat;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/** An interface for objects that can save themselves to a binary stream, similar
 * to {@link java.io.Externalizable Externalizable}.
 * @author TeamworkGuy2
 * @since 2013-7-7
 */
public interface ReadWritable {

	public void writeData(DataOutput out) throws IOException;

	public void readData(DataInput in) throws IOException;

}
