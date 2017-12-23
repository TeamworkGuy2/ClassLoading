package twg2.jbcm.classFormat.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.ReadWritable;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Utf8;
import twg2.jbcm.modify.IndexUtility;

/** A Java class file format Annotation subtype of type <code>Annotation</code>
 * @author TeamworkGuy2
 * @since 2013-12-3
 */
public class Annotation implements ReadWritable {
	ClassFile resolver;
	/** The value of the type_index item must be a valid index into the constant_pool table.
	 * The constant_pool entry at that index must be a CONSTANT_Utf8_info (§4.4.7) structure
	 * representing a field descriptor representing the annotation type corresponding to
	 * the annotation represented by this annotation structure. 
	 */
	CpIndex<CONSTANT_Utf8> type_index;
	/** The value of the num_element_value_pairs item gives the number of element-value pairs of
	 * the annotation represented by this annotation structure.
	 * Note that a maximum of 65535 element-value pairs may be contained in a single annotation. 
	 */
	short num_element_value_pairs;
	/** Each value of the element_value_pairs table represents a single element-value pair in the
	 * annotation represented by this annotation structure. Each element_value_pairs entry contains
	 * the following two items:
	 * element_name_index,
	 * value
	 */
	Element_Value_Pair[] element_value_pairs;


	public Annotation(ClassFile resolver) {
		this.resolver = resolver;
	}


	@Override
	public void changeCpIndex(short oldIndex, short newIndex) {
		IndexUtility.indexChange(type_index, oldIndex, newIndex);
		IndexUtility.indexChange(element_value_pairs, oldIndex, newIndex);
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		type_index.writeData(out);
		out.writeInt(num_element_value_pairs);
		for(int i = 0; i < num_element_value_pairs; i++) {
			element_value_pairs[i].writeData(out);
		}
	}


	@Override
	public void readData(DataInput in) throws IOException {
		type_index = resolver.getCheckCpIndex(in.readShort(), CONSTANT_Utf8.class);
		num_element_value_pairs = in.readShort();
		element_value_pairs = new Element_Value_Pair[num_element_value_pairs];
		for(int i = 0; i < num_element_value_pairs; i++) {
			element_value_pairs[i] = new Element_Value_Pair(resolver);
			element_value_pairs[i].readData(in);
		}
	}


	@Override
	public String toString() {
		StringBuilder str = new StringBuilder(64);
		str.append("Annotation(type=");
		str.append(type_index.getCpObject().getString());
		str.append(", pairs=[");
		for(int i = 0; i < num_element_value_pairs-1; i++) {
			str.append(element_value_pairs[i].toString());
			str.append(", ");
		}
		if(num_element_value_pairs > 0) { str.append(element_value_pairs[num_element_value_pairs-1].toString()); }
		str.append("])");
		return str.toString();
	}

}
