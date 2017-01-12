package twg2.jbcm.classFormat.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.ReadWritable;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Utf8;
import twg2.jbcm.modify.IndexUtility;

/**
 * @author TeamworkGuy2
 * @since 2014-3-19
 */
public class TypeAnnotation implements ReadWritable {
	ClassFile resolver;
	/* The value of the target_type item denotes the kind of target on which the annotation appears.
	 * The various kinds of target correspond to the type contexts of the Java programming language
	 * where types are used in declarations and expressions (JLS §4.11).
	 * The legal values of target_type are specified in Table 4.7.20-A and Table 4.7.20-B.
	 * Each value is a one-byte tag indicating which item of the target_info union follows the target_type
	 * item to give more information about the target.
	 * The kinds of target in Table 4.7.20-A and Table 4.7.20-B correspond to the type contexts in JLS §4.11.
	 * Namely, target_type values 0x10-0x17 and 0x40-0x42 correspond to type contexts 1-10, while target_type
	 * values 0x43-0x4B correspond to type contexts 11-15.
	 * The value of the target_type item determines whether the type_annotation structure appears in a
	 * RuntimeVisibleTypeAnnotations attribute in a ClassFile structure, a field_info structure, a
	 * method_info structure, or a Code attribute. Table 4.7.20-C gives the location of the
	 * RuntimeVisibleTypeAnnotations attribute for a type_annotation structure with each legal target_type value.
	 * 
	 * Table 4.7.20-A. Interpretation of target_type values (Part 1)
	 * Value 	Kind of target 				target_info item
	 * 0x00 	type_parameter_target 		type parameter declaration of generic class or interface
	 * 0x01 	type_parameter_target 		type parameter declaration of generic method or constructor
	 * 0x10 	supertype_target 			type in extends clause of class or interface declaration (including the direct superclass of an anonymous class declaration), or in implements clause of interface declaration
	 * 0x11 	type_parameter_bound_target 	type in bound of type parameter declaration of generic class or interface
	 * 0x12 	type_parameter_bound_target 	type in bound of type parameter declaration of generic method or constructor
	 * 0x13 	empty_target 				type in field declaration
	 * 0x14 	empty_target 				return type of method, or type of newly constructed object
	 * 0x15 	empty_target 				receiver type of method or constructor
	 * 0x16 	formal_parameter_target 	type in formal parameter declaration of method, constructor, or lambda expression
	 * 0x17 	throws_target 				type in throws clause of method or constructor
	 * 
	 * Table 4.7.20-B. Interpretation of target_type values (Part 2)
	 * Value 	Kind of target 		target_info item
	 * 0x40 	localvar_target 	type in local variable declaration
	 * 0x41 	localvar_target 	type in resource variable declaration
	 * 0x42 	catch_target 		type in exception parameter declaration
	 * 0x43 	offset_target 		type in instanceof expression
	 * 0x44 	offset_target 		type in new expression
	 * 0x45 	offset_target 		type in method reference expression using ::new
	 * 0x46 	offset_target 		type in method reference expression using ::IdentifierParser
	 * 0x47 	type_argument_target 	type in cast expression
	 * 0x48 	type_argument_target 	type argument for generic constructor in new expression or explicit constructor invocation statement
	 * 0x49 	type_argument_target 	type argument for generic method in method invocation expression
	 * 0x4A 	type_argument_target 	type argument for generic constructor in method reference expression using ::new
	 * 0x4B 	type_argument_target 	type argument for generic method in method reference expression using ::IdentifierParser
	 * 
	 * Table 4.7.20-C. Location of enclosing attribute for target_type values
	 * Value 	Kind of target 	Location
	 * 0x00 	ClassFile 		type parameter declaration of generic class or interface
	 * 0x01 	method_info 	type parameter declaration of generic method or constructor
	 * 0x10 	ClassFile 		type in extends clause of class or interface declaration, or in implements clause of interface declaration
	 * 0x11 	ClassFile 		type in bound of type parameter declaration of generic class or interface
	 * 0x12 	method_info 	type in bound of type parameter declaration of generic method or constructor
	 * 0x13 	field_info 		type in field declaration
	 * 0x14 	method_info 	return type of method or constructor
	 * 0x15 	method_info 	receiver type of method or constructor
	 * 0x16 	method_info 	type in formal parameter declaration of method, constructor, or lambda expression
	 * 0x17 	method_info 	type in throws clause of method or constructor
	 * 0x40-0x4B 	Code 		types in local variable declarations, resource variable declarations, exception parameter declarations, expressions
	 */
	byte target_type;

	/* The value of the target_info item denotes precisely which type in a declaration or expression is annotated.
	 * The items of the target_info union are specified in §4.7.20.1. 
	 */
	Target_Info target_info;

	/* The value of the target_path item denotes precisely which part of the type indicated by target_info is annotated.
	 * The format of the type_path structure is specified in §4.7.20.2. 
	 */
	Type_Path target_path;

	/* The meaning of these items in the type_annotation structure is the same as their meaning
	 * in the annotation structure (§4.7.16). 
	 */
	CpIndex<CONSTANT_Utf8> type_index;
	short num_element_value_pairs;
	Element_Value_Pair[] element_value_pairs;


	public TypeAnnotation(ClassFile resolver) {
		this.resolver = resolver;
	}


	@Override
	public void changeCpIndex(short oldIndex, short newIndex) {
		IndexUtility.indexChange(target_info, oldIndex, newIndex);
		IndexUtility.indexChange(target_path, oldIndex, newIndex);
		IndexUtility.indexChange(type_index, oldIndex, newIndex);
		IndexUtility.indexChange(element_value_pairs, oldIndex, newIndex);
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		out.writeByte(target_type);
		target_info.writeData(out);
		target_path.writeData(out);
		type_index.writeData(out);
		out.writeShort(num_element_value_pairs);
		for(int i = 0; i < num_element_value_pairs; i++) {
			element_value_pairs[i].writeData(out);
		}
	}


	@Override
	public void readData(DataInput in) throws IOException {
		target_type = in.readByte();

		// Create the target_info based on the target_type
		switch(target_type) {
		// Table 4.7.20-A
		case 0x00:
			// Fall through
		case 0x01:
			target_info = new Target_Info_Type.Type_Parameter_Target();
			break;
		case 0x10:
			target_info = new Target_Info_Type.Supertype_Target(); 
			break;
		case 0x11:
			// Fall through
		case 0x12:
			target_info = new Target_Info_Type.Type_Parameter_Bound_Target();
			break;
		case 0x13:
			// Fall through
		case 0x14:
			// Fall through
		case 0x15:
			target_info = new Target_Info_Type.Empty_Target();
			break;
		case 0x16:
			target_info = new Target_Info_Type.Formal_Parameter_Target();
			break;
		case 0x17:
			target_info = new Target_Info_Type.Throws_Target();
			break;
		// Table 4.7.20-B
		case 0x40:
			// Fall through
		case 0x41:
			target_info = new Target_Info_Type.Localvar_Target();
			break;
		case 0x42:
			target_info = new Target_Info_Type.Catch_Target();
		case 0x43:
			// Fall through
		case 0x44:
			// Fall through
		case 0x45:
			// Fall through
		case 0x46:
			target_info = new Target_Info_Type.Offset_Target();
		case 0x47:
			// Fall through
		case 0x48:
			// Fall through
		case 0x49:
			// Fall through
		case 0x4A:
			// Fall through
		case 0x4B:
			target_info = new Target_Info_Type.Type_Argument_Target();
		}

		target_info.readData(in);
		target_path = new Type_Path();
		target_path.readData(in);
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
		StringBuilder strB = new StringBuilder();
		strB.append("TypeAnnotation(target_type=" + target_type);
		strB.append(", target_info=" + target_info);
		strB.append(", target_path=" + target_path);
		strB.append(", type=" + type_index.getCpObject().getString());
		strB.append(", element_value_pairs[");
		if(num_element_value_pairs > 0) {
			for(int i = 0, size = num_element_value_pairs-1; i < size; i++) {
				strB.append(element_value_pairs[i] + ", ");
			}
			strB.append(element_value_pairs[num_element_value_pairs-1]);
		}
		strB.append("])");
		return strB.toString();
	}

}
