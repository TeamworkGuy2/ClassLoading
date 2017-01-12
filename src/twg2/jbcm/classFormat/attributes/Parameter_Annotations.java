package twg2.jbcm.classFormat.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.ReadWritable;
import twg2.jbcm.modify.IndexUtility;

/** A Java class file format Annotation subtype of type <code>Parameter_Annotation</code>
 * @author TeamworkGuy2
 * @since 2013-12-3
 */
public class Parameter_Annotations implements ReadWritable {
	ClassFile resolver;
	/* The value of the num_annotations item indicates the number of run-time-invisible annotations
	 * on the parameter corresponding to the sequence number of this parameter_annotations element.
	 */
	short num_annotations;
	/* Each value of the annotations table represents a single run-time-invisible annotation on the
	 * parameter corresponding to the sequence number of this parameter_annotations element.
	 */
	Annotation[] annotations;


	public Parameter_Annotations(ClassFile resolver) {
		this.resolver = resolver;
	}


	@Override
	public void changeCpIndex(short oldIndex, short newIndex) {
		IndexUtility.indexChange(annotations, oldIndex, newIndex);
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		out.writeShort(num_annotations);
		for(int i = 0; i < num_annotations; i++) {
			annotations[i].writeData(out);
		}
	}


	@Override
	public void readData(DataInput in) throws IOException {
		num_annotations = in.readShort();
		annotations = new Annotation[num_annotations];
		for(int i = 0; i < num_annotations; i++) {
			annotations[i] = new Annotation(resolver);
			annotations[i].readData(in);
		}
	}


	@Override
	public String toString() {
		StringBuilder strB = new StringBuilder(64);
		strB.append("Parameter_Annotations([");
		for(int i = 0; i < num_annotations-1; i++) {
			strB.append(annotations[i].toString());
			strB.append(", ");
		}
		if(num_annotations > 0) {
			strB.append(annotations[num_annotations-1].toString());
		}
		strB.append("])");
		return strB.toString();
	}

}
