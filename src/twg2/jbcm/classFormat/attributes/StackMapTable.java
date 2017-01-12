package twg2.jbcm.classFormat.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.Settings;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Utf8;
import twg2.jbcm.modify.IndexUtility;

/** A Java class file format Attribute of type <code>StackMapTable</code>
 * @author TeamworkGuy2
 * @since 2013-10-6
 */
public class StackMapTable implements Attribute_Type {
	public static final String ATTRIBUTE_NAME = "StackMapTable";
	ClassFile resolver;
	/* The value of the attribute_name_index item must be a valid index into the constant_pool table. The constant_pool
	 * entry at that index must be a CONSTANT_Utf8_info (§4.4.7) structure representing the string "StackMapTable".
	 */
	CpIndex<CONSTANT_Utf8> attribute_name_index;
	// The value of the attribute_length item of a ConstantValue_attribute structure must be 2.
	int attribute_length;
	/* The value of the number_of_entries item gives the number of stack_map_frame entries in the entries table.
	 */
	short number_of_entries;
	/* The entries array gives the method's stack_map_frame structures.
	 * Each stack_map_frame structure specifies the type state at a particular bytecode offset.
	 * Each frame type specifies (explicitly or implicitly) a value, offset_delta, that is used
	 * to calculate the actual bytecode offset at which a frame applies. The bytecode offset at
	 * which a frame applies is calculated by adding offset_delta + 1 to the bytecode offset of
	 * the previous frame, unless the previous frame is the initial frame of the method, in which
	 * case the bytecode offset is offset_delta.
	 * By using an offset delta rather than the actual bytecode offset we ensure, by definition,
	 * that stack map frames are in the correctly sorted order. Furthermore, by consistently
	 * using the formula offset_delta + 1 for all explicit frames, we guarantee the absence
	 * of duplicates. 
	 */
	StackMapFrame[] entries;


	public StackMapTable(ClassFile resolver, short attributeNameIndex) {
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
		IndexUtility.indexChange(entries, oldIndex, newIndex);
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		attribute_name_index.writeData(out);
		out.writeInt(attribute_length);
		out.writeShort(number_of_entries);
		for(int i = 0; i < number_of_entries; i++) {
			entries[i].writeData(out);
		}
	}


	@Override
	public void readData(DataInput in) throws IOException {
		if(Settings.doReadAttributeName()) {
			attribute_name_index = Settings.readAttributeNameIndex(in, resolver, ATTRIBUTE_NAME);
		}
		attribute_length = in.readInt();
		number_of_entries = in.readShort();
		entries = new StackMapFrame[number_of_entries];
		for(int i = 0; i < number_of_entries; i++) {
			entries[i] = new StackMapFrame(resolver);
			entries[i].readData(in);
		}
	}


	@Override
	public String toString() {
		StringBuilder strB = new StringBuilder();
		strB.append(ATTRIBUTE_NAME + "(entries=" + number_of_entries + ",\n");
		for(int i = 0; i < number_of_entries; i++) {
			strB.append("\t" + entries[i] + ",\n");
		}
		strB.append(")");
		return strB.toString();
	}

}

