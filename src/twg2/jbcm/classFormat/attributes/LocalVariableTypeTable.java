package twg2.jbcm.classFormat.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.Settings;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Utf8;
import twg2.jbcm.modify.IndexUtility;

/** A Java class file format Attribute of type {@code LocalVariableTable}
 * @author TeamworkGuy2
 * @since 2013-12-3
 */
public class LocalVariableTypeTable implements Attribute_Type {
	public static final String ATTRIBUTE_NAME = "LocalVariableTypeTable";
	ClassFile resolver;
	Code parent;
	/* The value of the attribute_name_index item must be a valid index into the constant_pool table. The constant_pool
	 * entry at that index must be a CONSTANT_Utf8_info (§4.4.7) structure representing the string
	 * "LocalVariableTypeTable".
	 */
	CpIndex<CONSTANT_Utf8> attribute_name_index;
	// The value of the attribute_length item indicates the attribute length, excluding the initial six bytes.
	int attribute_length;
	// The value of the local_variable_table_length item indicates the number of entries in the local_variable_table array.
	short local_variable_type_table_length;
	/* Each entry in the local_variable_table array indicates a range of code array offsets within which a local
	 * variable has a value. It also indicates the index into the local variable array of the current frame at which
	 * that local variable can be found.
	 */
	LocalVariableTypePoint[] local_variable_type_table;


	public LocalVariableTypeTable(ClassFile resolver, Code codeParent, short attributeNameIndex) {
		this.attribute_name_index = Settings.initAttributeNameIndex(attributeNameIndex, resolver);
		this.resolver = resolver;
		this.parent = codeParent;
	}


	@Override
	public String getAttributeName() {
		return attribute_name_index.getCpObject().getString();
	}


	@Override
	public int getAttributeLength() {
		return attribute_length;
	}


	@Override
	public void changeCpIndex(short oldIndex, short newIndex) {
		IndexUtility.indexChange(attribute_name_index, oldIndex, newIndex);
		IndexUtility.indexChange(local_variable_type_table, oldIndex, newIndex);
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		attribute_name_index.writeData(out);
		out.writeInt(attribute_length);
		out.writeShort(local_variable_type_table_length);
		for(int i = 0; i < local_variable_type_table_length; i++) {
			local_variable_type_table[i].writeData(out);
		}
	}


	@Override
	public void readData(DataInput in) throws IOException {
		if(Settings.doReadAttributeName()) {
			attribute_name_index = Settings.readAttributeNameIndex(in, resolver, ATTRIBUTE_NAME);
		}
		attribute_length = in.readInt();
		local_variable_type_table_length = in.readShort();
		local_variable_type_table = new LocalVariableTypePoint[local_variable_type_table_length];
		for(int i = 0; i < local_variable_type_table_length; i++) {
			local_variable_type_table[i] = new LocalVariableTypePoint(parent);
			local_variable_type_table[i].readData(in);
		}
	}


	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append(ATTRIBUTE_NAME + "(variables: " + local_variable_type_table_length + " [");
		for(int i = 0; i < local_variable_type_table_length-1; i++) {
			str.append(local_variable_type_table[i] + ", ");
		}
		if(local_variable_type_table_length > 0) { str.append(local_variable_type_table[local_variable_type_table_length-1]); }
		str.append("])");
		return str.toString();
	}

}
