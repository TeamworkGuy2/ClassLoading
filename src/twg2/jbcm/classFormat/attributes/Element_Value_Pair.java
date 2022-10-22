package twg2.jbcm.classFormat.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.ReadWritable;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Utf8;
import twg2.jbcm.modify.CpIndexChangeable;
import twg2.jbcm.modify.CpIndexChanger;

/** A Java class file format Annotation subtype of type <code>Element_Value_Pair</code>.
 * Represents a single element-value pair in an annotation structure.
 * @author TeamworkGuy2
 * @since 2013-12-3
 */
public class Element_Value_Pair implements ReadWritable, CpIndexChangeable {
	ClassFile resolver;
	/** The value of the element_name_index item must be a valid index into the constant_pool table.
	 * The constant_pool entry at that index must be a CONSTANT_Utf8_info structure (§4.4.7)
	 * representing a valid field descriptor (§4.3.2) that denotes the name of the annotation
	 * type element represented by this element_value_pairs entry. 
	 */
	CpIndex<CONSTANT_Utf8> element_name_index;
	/** The value of the value item represents the value of the element-value pair represented by
	 * this element_value_pairs entry.
	 */
	Element_Value value;


	public Element_Value_Pair(ClassFile resolver) {
		this.resolver = resolver;
	}


	@Override
	public void changeCpIndex(CpIndexChanger indexChanger) {
		indexChanger.indexChange(element_name_index);
		indexChanger.indexChange(value);
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		element_name_index.writeData(out);
		value.writeData(out);
	}


	@Override
	public void readData(DataInput in) throws IOException {
		element_name_index = resolver.getCheckCpIndex(in.readShort(), CONSTANT_Utf8.class);
		value = new Element_Value(resolver);
		value.readData(in);
	}


	@Override
	public String toString() {
		return value.toString();
	}

}
