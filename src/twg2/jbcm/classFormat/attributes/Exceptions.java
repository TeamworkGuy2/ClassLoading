package twg2.jbcm.classFormat.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.Settings;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Class;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Utf8;
import twg2.jbcm.modify.IndexUtility;

/** A Java class file format Attribute of type <code>Exceptions</code>
 * @author TeamworkGuy2
 * @since 2013-7-7
 */
public class Exceptions implements Attribute_Type {
	public static final String ATTRIBUTE_NAME = "Exceptions";
	ClassFile resolver;
	/* The value of the attribute_name_index item must be a valid index into the constant_pool table. The constant_pool
	 * entry at that index must be a CONSTANT_Utf8_info (§4.4.7) structure representing the string "Exceptions".
	 */
	CpIndex<CONSTANT_Utf8> attribute_name_index;
	// The value of the attribute_length item indicates the attribute length, excluding the initial six bytes.
	int attribute_length;
	// The value of the number_of_exceptions item indicates the number of entries in the exception_index_table.
	short number_of_exceptions;
	/* Each value in the exception_index_table array must be a valid index into the constant_pool table. The constant_pool
	 * entry referenced by each table item must be a CONSTANT_Class_info (§4.4.1) structure representing a
	 * class type that this method is declared to throw, size [number_of_exceptions], 0 indexed.
	 */
	CpIndex<CONSTANT_Class>[] exception_index_table;


	public Exceptions(ClassFile resolver, short attributeNameIndex) {
		this.attribute_name_index = Settings.initAttributeNameIndex(attributeNameIndex, resolver);
		this.resolver = resolver;
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
		IndexUtility.indexChange(exception_index_table, oldIndex, newIndex);
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		attribute_name_index.writeData(out);
		out.writeInt(attribute_length);
		out.writeShort(number_of_exceptions);
		for(int i = 0; i < number_of_exceptions; i++) {
			exception_index_table[i].writeData(out);
		}
	}


	@Override
	public void readData(DataInput in) throws IOException {
		if(Settings.doReadAttributeName()) {
			attribute_name_index = Settings.readAttributeNameIndex(in, resolver, ATTRIBUTE_NAME);
		}
		attribute_length = in.readInt();
		number_of_exceptions = in.readShort();
		exception_index_table = new CpIndex[number_of_exceptions];
		for(int i = 0; i < number_of_exceptions; i++) {
			exception_index_table[i] = resolver.getCheckCpIndex(in.readShort(), CONSTANT_Class.class);
		}
	}


	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append(ATTRIBUTE_NAME + "(exceptions: " + number_of_exceptions + " [");
		for(int i = 0; i < number_of_exceptions-1; i++) {
			str.append(exception_index_table[i].getCpObject() + ", ");
		}
		if(number_of_exceptions > 0) { str.append(exception_index_table[number_of_exceptions-1].getCpObject()); }
		str.append("])");
		return str.toString();
	}

}
