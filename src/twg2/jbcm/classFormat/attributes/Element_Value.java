package twg2.jbcm.classFormat.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.ReadWritable;
import twg2.jbcm.classFormat.constantPool.CONSTANT_CP_Info;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Double;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Float;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Integer;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Long;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Utf8;
import twg2.jbcm.modify.CpIndexChangeable;
import twg2.jbcm.modify.CpIndexChanger;

/** A Java class file format Annotation subtype of type <code>element_value</code>
 * TODO class needs cleaning up, is currently using multiple fields for a union type (I think, 2014-3-19)
 * @author TeamworkGuy2
 * @since 2013-12-3
 */
public class Element_Value implements ReadWritable, CpIndexChangeable {
	ClassFile resolver;
	/** The tag item indicates the type of this annotation element-value pair.
	 * The letters B, C, D, F, I, J, S, and Z indicate a primitive type. These letters are
	 * interpreted as if they were field descriptors (§4.3.2).
	 * The other legal values for tag are listed with their interpretations in Table 4.9.
	 * Table 4.9. Interpretation of additional tag values
	 * tag Value 	Element Type
	 * s 	String
	 * e 	enum constant
	 * c 	class
	 * @ 	annotation type
	 * [ 	array
	 */
	// valid values: B, C, D, F, I, J, S, Z, s, e, c, @, [
	// B,C,D,F,I,J,S,Z=primitives, s=string, e=enum constant, c=class, @=annotation type, [=array
	byte tag;

	// If tag is: B, C, D, F, I, J, S, Z, or s
	/** The const_value_index item is used if the tag item is one of B, C, D, F, I, J, S, Z, or s.
	 * The value of the const_value_index item must be a valid index into the constant_pool table.
	 * The constant_pool entry at that index must be of the correct entry type for the field type
	 * designated by the tag item, as specified in Table 4.9.
	 * Table 4.7.16.1-A. 
	 */
	// TODO not sure how this works... which types belong to which constant pool entries
	// CONSTANT_Integer.class, CONSTANT_Long.class, CONSTANT_Float.class, CONSTANT_Double.class, CONSTANT_Utf8.class
	CpIndex<CONSTANT_CP_Info> const_value_index;

	// If the tag is: e
	/** The value of the type_name_index item must be a valid index into the constant_pool table.
	 * The constant_pool entry at that index must be a CONSTANT_Utf8_info structure (§4.4.7) representing
	 * a valid field descriptor (§4.3.2) that denotes the internal form of the binary name (§4.2.1)
	 * of the type of the enum constant represented by this element_value structure. 
	 */
	CpIndex<CONSTANT_Utf8> type_name_index;
	/** The value of the const_name_index item must be a valid index into the constant_pool table.
	 * The constant_pool entry at that index must be a CONSTANT_Utf8_info structure (§4.4.7) representing
	 * the simple name of the enum constant represented by this element_value structure. 
	 */
	CpIndex<CONSTANT_Utf8> const_name_index;

	// If the tag is: c
	/** The class_info_index item is used if the tag item is c.
	 * The class_info_index item must be a valid index into the constant_pool table.
	 * The constant_pool entry at that index must be a CONSTANT_Utf8_info (§4.4.7) structure
	 * representing the return descriptor (§4.3.3) of the type that is reified by the class
	 * represented by this element_value structure.
	 * For example, V for Void.class, Ljava/lang/Object; for Object, etc.
	 */
	CpIndex<CONSTANT_Utf8> class_info_index;

	// If the tag is: @
	/** The annotation_value item is used if the tag item is @.
	 * The element_value structure represents a "nested" annotation. 
	 */
	Annotation annotation_value;

	// If the tag is: [
	/** The value of the num_values item gives the number of elements in the array-typed value represented
	 * by this element_value structure.
	 * Note that a maximum of 65535 elements are permitted in an array-typed element value. 
	 */
	short num_values;
	/** Each value of the values table gives the value of an element of the array-typed value
	 * represented by this element_value structure. 
	 */
	Element_Value[] values;


	public Element_Value(ClassFile resolver) {
		this.resolver = resolver;
	}


	@Override
	public void changeCpIndex(CpIndexChanger indexChanger) {
		if(const_value_index != null) indexChanger.indexChange(const_value_index);
		if(const_name_index != null) indexChanger.indexChange(const_name_index);
		if(type_name_index != null) indexChanger.indexChange(type_name_index);
		if(class_info_index != null) indexChanger.indexChange(class_info_index);
		if(annotation_value != null) indexChanger.indexChange(annotation_value);
		if(values != null) indexChanger.indexChange(values);
	}


	public byte getTag() {
		return tag;
	}


	public void setTag(byte tag) {
		this.tag = tag;
	}


	public final CpIndex<CONSTANT_CP_Info> getConstValueIndex() {
		return const_value_index;
	}


	public final void setConstValueIndex(CpIndex<CONSTANT_CP_Info> constValueIndex) {
		checkElementValue(constValueIndex);
		this.const_value_index = constValueIndex;
	}


	public final CpIndex<CONSTANT_Utf8> getTypeNameIndex() {
		return type_name_index;
	}


	public final void setTypeNameIndex(CpIndex<CONSTANT_Utf8> typeNameIndex) {
		this.type_name_index = typeNameIndex;
	}


	public final CpIndex<CONSTANT_Utf8> getConstNameIndex() {
		return const_name_index;
	}


	public final void setConstNameIndex(CpIndex<CONSTANT_Utf8> constNameIndex) {
		this.const_name_index = constNameIndex;
	}


	public final CpIndex<CONSTANT_Utf8> getClassInfoIndex() {
		return class_info_index;
	}


	public final void setClassInfoIndex(CpIndex<CONSTANT_Utf8> classInfoIndex) {
		this.class_info_index = classInfoIndex;
	}


	public final Annotation getAnnotation_value() {
		return annotation_value;
	}


	public final void setAnnotation_value(Annotation annotationValue) {
		this.annotation_value = annotationValue;
	}


	public final short getNumValues() {
		return num_values;
	}


	public final void setNumValues(short numValues) {
		this.num_values = numValues;
	}


	public final Element_Value[] getValues() {
		return values;
	}


	public final void setValues(Element_Value[] values) {
		this.values = values;
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		out.writeByte(tag);
		// TODO clean up
		if(tag == 'B' || tag == 'C' || tag == 'D' || tag == 'F' || tag == 'I' || tag == 'J' || tag == 'S' ||
				tag == 'Z' || tag == 's') {
			const_value_index.writeData(out);
		}
		else if(tag == 'e') {
			type_name_index.writeData(out);
			const_name_index.writeData(out);
		}
		else if(tag == 'c') {
			class_info_index.writeData(out);
		}
		else if(tag == '@') {
			annotation_value.writeData(out);
		}
		else if(tag == '[') {
			out.writeShort(num_values);
			for(int i = 0; i < num_values; i++) {
				values[i].writeData(out);
			}
		}
		else {
			throw new IllegalStateException("Unknown element_value of " + tag + ", valid values are: B, C, D, F, I, J, S, Z, s, e, c, @, [");
		}
	}


	@Override
	public void readData(DataInput in) throws IOException {
		tag = in.readByte();
		// TODO clean up
		if(tag == 'B' || tag == 'C' || tag == 'D' || tag == 'F' || tag == 'I' || tag == 'J' || tag == 'S' ||
				tag == 'Z' || tag == 's') {
			const_value_index = getElementValuePrimitiveOrString(in, tag, resolver);
		}
		else if(tag == 'e') {
			type_name_index = resolver.getCheckCpIndex(in.readShort(), CONSTANT_Utf8.class);
			const_name_index = resolver.getCheckCpIndex(in.readShort(), CONSTANT_Utf8.class);
		}
		else if(tag == 'c') {
			class_info_index = resolver.getCheckCpIndex(in.readShort(), CONSTANT_Utf8.class);
		}
		else if(tag == '@') {
			annotation_value = new Annotation(resolver);
			annotation_value.readData(in);
		}
		else if(tag == '[') {
			num_values = in.readShort();
			values = new Element_Value[num_values];
			for(int i = 0; i < num_values; i++) {
				values[i] = new Element_Value(resolver);
				values[i].readData(in);
			}
		}
		else {
			throw new IllegalStateException("Unknown element_value of " + tag + ", valid values are: B, C, D, F, I, J, S, Z, s, e, c, @, [");
		}
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(64);
		sb.append("element_value(tag=");
		sb.append((char)tag);
		sb.append(", ");

		if(tag == 'B' || tag == 'C' || tag == 'D' || tag == 'F' || tag == 'I' || tag == 'J' || tag == 'S' ||
				tag == 'Z' || tag == 's') {
			sb.append("primitive_or_String=");
			sb.append(const_value_index.getCpObject());
		}
		else if(tag == 'e') {
			sb.append("enum=[");
			sb.append(type_name_index.getCpObject());
			sb.append(", ");
			sb.append(const_name_index.getCpObject());
			sb.append("]");
		}
		else if(tag == 'c') {
			sb.append("class=");
			sb.append(class_info_index);
		}
		else if(tag == '@') {
			sb.append("annotation=");
			sb.append(annotation_value.toString());
		}
		else if(tag == '[') {
			sb.append("array=[");
			for(int i = 0; i < num_values-1; i++) {
				sb.append(values[i].toString());
				sb.append(", ");
			}
			if(num_values > 0) {
				sb.append(values[num_values-1].toString());
			}
			sb.append("]");
		}

		return sb.toString();
	}


	/** Read an {@link Element_Value}'s {@code const_value_index} constant pool index.
	 * Handle's tag types of B, C, D, F, I, J, S, Z, s.
	 * Does not handle e, c, @, [.
	 * @param in the data input stream to read the constant pool index from
	 * @param tag the tag constant pool type tag
	 * @param resolver the constant pool to use
	 * @return the constant pool object of the specified tag type at the index read from the input stream
	 * @throws IOException if there is an error reading from the input stream
	 * @throws IllegalArgumentException if the tag type is not one of B, C, D, F, I, J, S, Z, s.
	 */
	public static final CpIndex<CONSTANT_CP_Info> getElementValuePrimitiveOrString(DataInput in, byte tag, ClassFile resolver) throws IOException {
		switch(tag) {
		case 'B':
			@SuppressWarnings("unchecked")
			CpIndex<CONSTANT_CP_Info> objB = (CpIndex<CONSTANT_CP_Info>)(CpIndex<? extends CONSTANT_CP_Info>)
					resolver.getCheckCpIndex(in.readShort(), CONSTANT_Integer.class);
			return objB;
		case 'C':
			@SuppressWarnings("unchecked")
			CpIndex<CONSTANT_CP_Info> objC = (CpIndex<CONSTANT_CP_Info>)(CpIndex<? extends CONSTANT_CP_Info>)
					resolver.getCheckCpIndex(in.readShort(), CONSTANT_Integer.class);
			return objC;
		case 'D':
			@SuppressWarnings("unchecked")
			CpIndex<CONSTANT_CP_Info> objD = (CpIndex<CONSTANT_CP_Info>)(CpIndex<? extends CONSTANT_CP_Info>)
					resolver.getCheckCpIndex(in.readShort(), CONSTANT_Double.class);
			return objD;
		case 'F':
			@SuppressWarnings("unchecked")
			CpIndex<CONSTANT_CP_Info> objF = (CpIndex<CONSTANT_CP_Info>)(CpIndex<? extends CONSTANT_CP_Info>)
					resolver.getCheckCpIndex(in.readShort(), CONSTANT_Float.class);
			return objF;
		case 'I':
			@SuppressWarnings("unchecked")
			CpIndex<CONSTANT_CP_Info> objI = (CpIndex<CONSTANT_CP_Info>)(CpIndex<? extends CONSTANT_CP_Info>)
					resolver.getCheckCpIndex(in.readShort(), CONSTANT_Integer.class);
			return objI;
		case 'J':
			@SuppressWarnings("unchecked")
			CpIndex<CONSTANT_CP_Info> objJ = (CpIndex<CONSTANT_CP_Info>)(CpIndex<? extends CONSTANT_CP_Info>)
					resolver.getCheckCpIndex(in.readShort(), CONSTANT_Long.class);
			return objJ;
		case 'S':
			@SuppressWarnings("unchecked")
			CpIndex<CONSTANT_CP_Info> objS = (CpIndex<CONSTANT_CP_Info>)(CpIndex<? extends CONSTANT_CP_Info>)
					resolver.getCheckCpIndex(in.readShort(), CONSTANT_Integer.class);
			return objS;
		case 'Z':
			@SuppressWarnings("unchecked")
			CpIndex<CONSTANT_CP_Info> objZ = (CpIndex<CONSTANT_CP_Info>)(CpIndex<? extends CONSTANT_CP_Info>)
					resolver.getCheckCpIndex(in.readShort(), CONSTANT_Integer.class);
			return objZ;
		case 's':
			@SuppressWarnings("unchecked")
			CpIndex<CONSTANT_CP_Info> objs = (CpIndex<CONSTANT_CP_Info>)(CpIndex<? extends CONSTANT_CP_Info>)
					resolver.getCheckCpIndex(in.readShort(), CONSTANT_Utf8.class);
			return objs;
		default:
			throw new IllegalArgumentException("unknown element_value of " + tag + ", valid values are: B, C, D, F, I, J, S, Z, s, e, c, @, [");
		}
	}


	public static final CpIndex<CONSTANT_CP_Info> checkElementValue(CpIndex<CONSTANT_CP_Info> element) {
		CONSTANT_CP_Info obj = element.getCpObject();
		if(obj instanceof CONSTANT_Integer || obj instanceof CONSTANT_Long || obj instanceof CONSTANT_Float
				|| obj instanceof CONSTANT_Double || obj instanceof CONSTANT_Utf8) {
			return element;
		}
		throw new IllegalArgumentException("element value constant index is not a valid type: " + obj.getClass());
	}

}
