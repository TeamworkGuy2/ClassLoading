package twg2.jbcm.classFormat.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ReadWritable;
import twg2.jbcm.modify.IndexUtility;

/** A member of {@link TypeAnnotation} and used in {@link Target_Info_Type}.
 * @author TeamworkGuy2
 * @since 2014-3-19
 */
public class Type_Path implements ReadWritable {
	byte table_length;
	Type_Path_Table_Entry[] table;


	@Override
	public void changeCpIndex(short oldIndex, short newIndex) {
		IndexUtility.indexChange(table, oldIndex, newIndex);
	}

	@Override
	public void writeData(DataOutput out) throws IOException {
		out.writeByte(table_length);
		for(int i = 0; i < table_length; i++) {
			table[i].writeData(out);
		}
	}


	@Override
	public void readData(DataInput in) throws IOException {
		table_length = in.readByte();
		table = new Type_Path_Table_Entry[table_length];
		for(int i = 0; i < table_length; i++) {
			table[i] = new Type_Path_Table_Entry();
			table[i].readData(in);
		}
	}


	@Override
	public String toString() {
		StringBuilder strB = new StringBuilder();
		strB.append("type_path(table_length=");
		strB.append(table_length);
		strB.append(", table[");
		for(int i = 0, size = table_length-1; i < size; i++) {
			strB.append(table[table_length-1] + ", ");
		}
		strB.append(table[table_length-1]);
		strB.append("])");
		return strB.toString();
	}

}
