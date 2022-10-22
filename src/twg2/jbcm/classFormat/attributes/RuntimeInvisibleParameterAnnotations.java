package twg2.jbcm.classFormat.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.ClassFileAttributes;
import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.Settings;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Utf8;
import twg2.jbcm.modify.CpIndexChanger;

/** A Java class file format Attribute of type <code>RuntimeInvisibleParameterAnnotations</code>
 * @author TeamworkGuy2
 * @since 2013-12-3
 */
public class RuntimeInvisibleParameterAnnotations implements Attribute_Type {
	public static final String ATTRIBUTE_NAME = "RuntimeInvisibleParameterAnnotations";
	ClassFile resolver;
	/** The value of the attribute_name_index item must be a valid index into the constant_pool table.
	 * The constant_pool entry at that index must be a CONSTANT_Utf8_info structure (§4.4.7) representing
	 * the string "RuntimeInvisibleParameterAnnotations". 
	 */
	CpIndex<CONSTANT_Utf8> attribute_name_index;
	/** The value of the attribute_length indicates the length of the attribute, excluding the initial six bytes.
	 * The value of the attribute_length item is thus dependent on the number of parameters,
	 * the number of run-time-invisible annotations on each parameter, and their values. 
	 */
	int attribute_length;
	/** The value of the num_parameters item gives the number of parameters of the method represented
	 * by the method_info structure on which the annotation occurs. (This duplicates information that
	 * could be extracted from the method descriptor (§4.3.3)).
	 */
	byte num_parameters;
	/** Each value of the parameter_annotations table represents all of the run-time-invisible annotations
	 * on a single parameter. The sequence of values in the table corresponds to the sequence of parameters
	 * in the method descriptor. Each parameter_annotations entry contains the following two items:<pre>
	 * num_annotations
	 * annotations[]</pre>
	 */
	Parameter_Annotations[] parameter_annotations;


	public RuntimeInvisibleParameterAnnotations(ClassFile resolver, short attributeNameIndex) {
		this.attribute_name_index = resolver.getAttributeNameIndex(attributeNameIndex);
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
	public void changeCpIndex(CpIndexChanger indexChanger) {
		indexChanger.indexChange(attribute_name_index);
		indexChanger.indexChange(parameter_annotations);
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		attribute_name_index.writeData(out);
		out.writeInt(attribute_length);
		out.writeByte(num_parameters);
		for(int i = 0; i < num_parameters; i++) {
			parameter_annotations[i].writeData(out);
		}
	}


	@Override
	public void readData(DataInput in) throws IOException {
		if(Settings.readAttributeName) {
			attribute_name_index = ClassFileAttributes.readAttributeNameIndex(in, resolver, ATTRIBUTE_NAME);
		}
		attribute_length = in.readInt();
		num_parameters = in.readByte();
		parameter_annotations = new Parameter_Annotations[num_parameters];
		for(int i = 0; i < num_parameters; i++) {
			parameter_annotations[i] = new Parameter_Annotations(resolver);
			parameter_annotations[i].readData(in);
		}
	}


	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append(ATTRIBUTE_NAME);
		str.append("([");
		for(int i = 0; i < num_parameters - 1; i++) {
			str.append(parameter_annotations[i]);
			str.append(", ");
		}
		if(num_parameters > 0) { str.append(parameter_annotations[num_parameters - 1]); }
		str.append("])");
		return str.toString();
	}

}
