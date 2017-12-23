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
	/** The value of the target_type item denotes the kind of target on which the annotation appears.
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

	/** The value of the target_info item denotes precisely which type in a declaration or expression is annotated.
	 * The items of the target_info union are specified in §4.7.20.1. 
	 */
	Target_Info target_info;

	/** The value of the target_path item denotes precisely which part of the type indicated by target_info is annotated.
	 * The format of the type_path structure is specified in §4.7.20.2. 
	 */
	Type_Path target_path;

	/** The meaning of these items in the type_annotation structure is the same as their meaning
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
			target_info = new Target_Info_Union.Type_Parameter_Target();
			break;
		case 0x10:
			target_info = new Target_Info_Union.Supertype_Target(); 
			break;
		case 0x11:
			// Fall through
		case 0x12:
			target_info = new Target_Info_Union.Type_Parameter_Bound_Target();
			break;
		case 0x13:
			// Fall through
		case 0x14:
			// Fall through
		case 0x15:
			target_info = new Target_Info_Union.Empty_Target();
			break;
		case 0x16:
			target_info = new Target_Info_Union.Formal_Parameter_Target();
			break;
		case 0x17:
			target_info = new Target_Info_Union.Throws_Target();
			break;
		// Table 4.7.20-B
		case 0x40:
			// Fall through
		case 0x41:
			target_info = new Target_Info_Union.Localvar_Target();
			break;
		case 0x42:
			target_info = new Target_Info_Union.Catch_Target();
			break;
		case 0x43:
			// Fall through
		case 0x44:
			// Fall through
		case 0x45:
			// Fall through
		case 0x46:
			target_info = new Target_Info_Union.Offset_Target();
			break;
		case 0x47:
			// Fall through
		case 0x48:
			// Fall through
		case 0x49:
			// Fall through
		case 0x4A:
			// Fall through
		case 0x4B:
			target_info = new Target_Info_Union.Type_Argument_Target();
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
		StringBuilder sb = new StringBuilder();
		sb.append("TypeAnnotation(target_type=").append(target_type);
		sb.append(", target_info=").append(target_info);
		sb.append(", target_path=").append(target_path);
		sb.append(", type=").append(type_index.getCpObject());
		sb.append(", element_value_pairs[");
		for(int i = 0, size = num_element_value_pairs - 1; i < size; i++) {
			sb.append(element_value_pairs[i]).append(", ");
		}
		if(num_element_value_pairs > 0) { sb.append(element_value_pairs[num_element_value_pairs - 1]); }
		sb.append("])");
		return sb.toString();
	}



	/** A member of {@link TypeAnnotation} and used in {@link Target_Info_Union}.
	 * @author TeamworkGuy2
	 * @since 2014-3-19
	 */
	public static class Type_Path implements ReadWritable {
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
			StringBuilder sb = new StringBuilder();
			sb.append("type_path(table_length=");
			sb.append(table_length);
			sb.append(", table[");
			for(int i = 0, size = table_length - 1; i < size; i++) {
				sb.append(table[i]).append(", ");
			}
			if(table_length > 0) { sb.append(table[table_length - 1]); }
			sb.append("])");
			return sb.toString();
		}

	}




	/** A member of {@link TypeAnnotation} and used in {@link Target_Info_Union}.
	 * Each entry in the path array represents an iterative, left-to-right step towards the precise
	 * location of the annotation in an array type, nested type, or parameterized type. (In an array
	 * type, the iteration visits the array type itself, then its component type, then the component
	 * type of that component type, and so on, until the element type is reached.)
	 * Each entry contains the following two items:<br/>
	 * type_path_kind
	 * 	The legal values for the type_path_kind item are listed in Table 4.7.20.2-A.
	 * 	<b>Table 4.7.20.2-A. Interpretation of type_path_kind values</b>
	 * 	<table border="1">
	 * 		<tr><td>Value</td><td>Interpretation</td></tr>
	 * 		<tr><td>0</td><td>Annotation is deeper in an array type</td></tr>
	 * 		<tr><td>1</td><td>Annotation is deeper in a nested type</td></tr>
	 * 		<tr><td>2</td><td>Annotation is on the bound of a wildcard type argument of a parameterized type</td></tr>
	 * 		<tr><td>3</td><td>Annotation is on a type argument of a parameterized type</td></tr>
	 * 	</table>
	 * <br/>
	 * type_argument_index
	 * 	If the value of the type_path_kind item is 0, 1, or 2, then the value of the type_argument_index item is 0.
	 * 	If the value of the type_path_kind item is 3, then the value of the type_argument_index item specifies which type argument of a parameterized type is annotated, where 0 indicates the first type argument of a parameterized type.
	 * 	<br/>
	 * 	<b>{@literal Table 4.7.20.2-B. type_path structures for @A Map<@B ? extends @C String, @D List<@E Object>>}</b>
	 * 	<table border="1">
	 * 		<tr><td>Annotation</td><td>path_length</td><td>path</tr>
	 * 		<tr><td>@A</td><td>0</td><td>[]</td></tr>
	 * 		<tr><td>@B</td><td>1</td><td>[{type_path_kind: 3; type_argument_index: 0}]</td></tr>
	 * 		<tr><td>@C</td><td>2</td><td>[{type_path_kind: 3; type_argument_index: 0}, {type_path_kind: 2; type_argument_index: 0}]</td></tr>
	 * 		<tr><td>@D</td><td>1</td><td>[{type_path_kind: 3; type_argument_index: 1}]</td></tr>
	 * 		<tr><td>@E</td><td>2</td><td>[{type_path_kind: 3; type_argument_index: 1}, {type_path_kind: 3; type_argument_index: 0}]</td></tr>
	 * 	</table>
	 * 	<br/>
	 * 	<b>Table 4.7.20.2-C. type_path structures for @I String @F [] @G [] @H []</b>
	 * 	<table border="1">
	 * 		<tr><td>Annotation</td><td>path_length</td><td>path</td></tr>
	 * 		<tr><td>@F</td><td>0</td><td>[]</td></tr>
	 * 		<tr><td>@G</td><td>1</td><td>[{type_path_kind: 0; type_argument_index: 0}]</td></tr>
	 * 		<tr><td>@H</td><td>2</td><td>[{type_path_kind: 0; type_argument_index: 0}, {type_path_kind: 0; type_argument_index: 0}]</td></tr>
	 * 		<tr><td>@I</td><td>3</td><td>[{type_path_kind: 0; type_argument_index: 0}, {type_path_kind: 0; type_argument_index: 0}, {type_path_kind: 0; type_argument_index: 0}]</td></tr>
	 * 	</table>
	 *	<br/>
	 * 	<b>{@literal Table 4.7.20.2-D. type_path structures for @A List<@B Comparable<@F Object @C [] @D [] @E []>>}</b>
	 * 	<table border="1">
	 * 		<tr><td>Annotation</td><td>path_length</td><td>path</td></tr>
	 * 		<tr><td>@A</td><td>0</td><td>[]</tr>
	 * 		<tr><td>@B</td><td>1</td><td>[{type_path_kind: 3; type_argument_index: 0}]</td></tr>
	 * 		<tr><td>@C</td><td>2</td><td>[{type_path_kind: 3; type_argument_index: 0}, {type_path_kind: 3; type_argument_index: 0}]</td></tr>
	 * 		<tr><td>@D</td><td>3</td><td>[{type_path_kind: 3; type_argument_index: 0}, {type_path_kind: 3; type_argument_index: 0}, {type_path_kind: 0; type_argument_index: 0}]</td></tr>
	 * 		<tr><td>@E</td><td>4</td><td>[{type_path_kind: 3; type_argument_index: 0}, {type_path_kind: 3; type_argument_index: 0}, {type_path_kind: 0; type_argument_index: 0}, {type_path_kind: 0; type_argument_index: 0}]</td></tr>
	 * 		<tr><td>@F</td><td>5</td><td>[{type_path_kind: 3; type_argument_index: 0}, {type_path_kind: 3; type_argument_index: 0}, {type_path_kind: 0; type_argument_index: 0}, {type_path_kind: 0; type_argument_index: 0}, {type_path_kind: 0; type_argument_index: 0}]</td></tr>
	 * 	</table>
	 * 	<br/>	
	 * 	<b>Table 4.7.20.2-E. type_path structures for @C Outer . @B Middle . @A Inner</b>
	 * 	<table border="1">
	 * 		<tr><td>Annotation</td><td>path_length</td><td>path</td></tr>
	 * 		<tr><td>@A</td><td>2</td><td>[{type_path_kind: 1; type_argument_index: 0}, {type_path_kind: 1; type_argument_index: 0}]</td></tr>
	 * 		<tr><td>@B</td><td>1</td><td>[{type_path_kind: 1; type_argument_index: 0}]</td></tr>
	 * 		<tr><td>@C</td><td>0</td><td>[]</tr>
	 * 	</table>
	 * 	<br/>
	 * 	<b>{@literal Table 4.7.20.2-F. type_path structures for Outer . Middle<@D Foo . @C Bar> . Inner<@B String @A []>}</b>
	 * 	<table border="1">
	 * 		<tr><td>Annotation</td><td>path_length</td><td>path</td></tr>
	 * 		<tr><td>@A</td><td>3</td><td>[{type_path_kind: 1; type_argument_index: 0}, {type_path_kind: 1; type_argument_index: 0}, {type_path_kind: 3; type_argument_index: 0}]</td></tr>
	 * 		<tr><td>@B</td><td>4</td><td>[{type_path_kind: 1; type_argument_index: 0}, {type_path_kind: 1; type_argument_index: 0}, {type_path_kind: 3; type_argument_index: 0}, {type_path_kind: 0; type_argument_index: 0}]</td></tr>
	 * 		<tr><td>@C</td><td>3</td><td>[{type_path_kind: 1; type_argument_index: 0}, {type_path_kind: 3; type_argument_index: 0}, {type_path_kind: 1; type_argument_index: 0}]</td></tr>
	 * 		<tr><td>@D</td><td>2</td><td>[{type_path_kind: 1; type_argument_index: 0}, {type_path_kind: 3; type_argument_index: 0}]</td></tr>
	 * 	</table>
	 * <br/>
	 * @author TeamworkGuy2
	 * @since 2014-3-19
	 */
	public static class Type_Path_Table_Entry implements ReadWritable {
		byte type_path_kind;
		byte type_argument_index;


		public Type_Path_Table_Entry() {
		}


		public Type_Path_Table_Entry(byte typePathKind, byte typeArgumentIndex) {
			type_path_kind = typePathKind;
			type_argument_index = typeArgumentIndex;
		}


		@Override
		public void changeCpIndex(short oldIndex, short newIndex) {
		}


		@Override
		public void writeData(DataOutput out) throws IOException {
			out.writeByte(type_path_kind);
			out.writeByte(type_argument_index);
		}


		@Override
		public void readData(DataInput in) throws IOException {
			type_path_kind = in.readByte();
			type_argument_index = in.readByte();

			if(type_path_kind < 0 || type_path_kind > 3) {
				throw new IllegalStateException("a type_path's kind must be within the range [0, 3]");
			}
			if(type_path_kind != 3 && type_argument_index != 0) {
				throw new IllegalStateException("a type_path's index must be zero if it's path is 0, 1, or 2.");
			}
		}


		@Override
		public String toString() {
			return "type_path_table_entry(kind=" + type_path_kind + ", type_argument_index=" + type_argument_index + ")";
		}

	}

}
