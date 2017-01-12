package twg2.jbcm.classFormat.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ReadWritable;

/** A member of {@link TypeAnnotation} and used in {@link Target_Info_Type}.
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
public class Type_Path_Table_Entry implements ReadWritable {
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

}