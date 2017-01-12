package twg2.jbcm.classFormat.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ReadWritable;
import twg2.jbcm.modify.IndexUtility;

/** A container class for all {@link Target_Info} classes
 * which are a member type in {@link TypeAnnotation}.<br/>
 * TODO move this classes into their own class files... (2013-3-19)
 * The items of the target_info union (except for the first) specify precisely which type in a
 * declaration or expression is annotated. The first item specifies not which type, but rather
 * which declaration of a type parameter is annotated. The items are as follows:<br/>
 * {@link Type_Parameter_Target}<br/>
 * {@link Supertype_Target}<br/>
 * {@link Type_Parameter_Bound_Target}<br/>
 * {@link Empty_Target}<br/>
 * {@link Formal_Parameter_Target}<br/>
 * {@link Throws_Target}<br/>
 * {@link Localvar_Target}<br/>
 * {@link Catch_Target}<br/>
 * {@link Offset_Target}<br/>
 * {@link Type_Argument_Target}<br/>
 * @author TeamworkGuy2
 * @since 2014-3-19
 */
public final class Target_Info_Type {

	private Target_Info_Type() {
		throw new AssertionError("may not instantiate Target_Info_Type, only a subtype");
	}


	/** The type_parameter_target item indicates that an annotation appears on the declaration of
	 * the i'th type parameter of a generic class, generic interface, generic method, or generic constructor.
	 * <pre>type_parameter_target {
	 *   u1 type_parameter_index;
	 * }</pre>
	 * The value of the type_parameter_index item specifies which type parameter declaration is annotated.
	 * A type_parameter_index value of 0 specifies the first type parameter declaration. 
	 * @author TeamworkGuy2
	 * @since 2014-3-19
	 */
	public static class Type_Parameter_Target implements ReadWritable, Target_Info {
		byte type_parameter_index;

		@Override
		public void changeCpIndex(short oldIndex, short newIndex) {
		}

		@Override
		public void writeData(DataOutput out) throws IOException {
			out.writeByte(type_parameter_index);
		}

		@Override
		public void readData(DataInput in) throws IOException {
			type_parameter_index = in.readByte();
		}

		@Override
		public String toString() {
			return "target_info.type_parameter_target(type_parameter_index=" + type_parameter_index + ")";
		}
	}


	/** The supertype_target item indicates that an annotation appears on a type in the extends or implements
	 * clause of a class or interface declaration.
	 * <pre>supertype_target {
	 *   u2 supertype_index;
	 * }</pre>
	 * A supertype_index value of 65535 specifies that the annotation appears on the superclass in an
	 * extends clause of a class declaration.
	 * Any other supertype_index value is an index into the interfaces array of the enclosing ClassFile structure,
	 * and specifies that the annotation appears on that superinterface in either the implements clause
	 * of a class declaration or the extends clause of an interface declaration. 
	 * @author TeamworkGuy2
	 * @since 2014-3-19
	 */
	public static class Supertype_Target implements ReadWritable, Target_Info {
		short supertype_index;

		@Override
		public void changeCpIndex(short oldIndex, short newIndex) {
		}

		@Override
		public void writeData(DataOutput out) throws IOException {
			out.writeShort(supertype_index);
		}

		@Override
		public void readData(DataInput in) throws IOException {
			supertype_index = in.readShort();
		}

		@Override
		public String toString() {
			return "target_info.supertype_target(supertype_index=" + supertype_index + ")";
		}
	}


	/** The type_parameter_bound_target item indicates that an annotation appears on the i'th bound of
	 * the j'th type parameter declaration of a generic class, interface, method, or constructor.
	 * <pre>type_parameter_bound_target {
	 *   u1 type_parameter_index;
	 *   u1 bound_index;
	 * }</pre>
	 * The value of the of type_parameter_index item specifies which type parameter declaration
	 * has an annotated bound. A type_parameter_index value of 0 specifies the first type parameter
	 * declaration.
	 * The value of the bound_index item specifies which bound of the type parameter declaration indicated
	 * by type_parameter_index is annotated. A bound_index value of 0 specifies the first bound of a
	 * type parameter declaration.
	 * The type_parameter_bound_target item records that a bound is annotated, but does not record the
	 * type which constitutes the bound. The type may be found by inspecting the class signature or method
	 * signature stored in the appropriate Signature attribute. 
	 * @author TeamworkGuy2
	 * @since 2014-3-19
	 */
	public static class Type_Parameter_Bound_Target implements ReadWritable, Target_Info {
		byte type_parameter_index;
		byte bound_index;

		@Override
		public void changeCpIndex(short oldIndex, short newIndex) {
		}

		@Override
		public void writeData(DataOutput out) throws IOException {
			out.writeByte(type_parameter_index);
			out.writeByte(bound_index);
		}

		@Override
		public void readData(DataInput in) throws IOException {
			type_parameter_index = in.readByte();
			bound_index = in.readByte();
		}

		@Override
		public String toString() {
			return "target_info.type_parameter_bound_target(type_parameter_index=" + type_parameter_index
					+ ", bound_index=" + bound_index + ")";
		}
	}


	/** The empty_target item indicates that an annotation appears on either the type in a field declaration,
	 * the return type of a method, the type of a newly constructed object, or the receiver type of a method
	 * or constructor.
	 * <pre>empty_target {
	 * }</pre>
	 * Only one type appears in each of these locations, so there is no per-type information to represent
	 * in the target_info union. 
	 * @author TeamworkGuy2
	 * @since 2014-3-19
	 */
	public static class Empty_Target implements ReadWritable, Target_Info {
		;

		@Override
		public void changeCpIndex(short oldIndex, short newIndex) {
		}

		@Override
		public void writeData(DataOutput out) throws IOException {
		}

		@Override
		public void readData(DataInput in) throws IOException {
		}

		@Override
		public String toString() {
			return "target_info.empty_target()";
		}
	}


	/** The formal_parameter_target item indicates that an annotation appears on the type in a formal
	 * parameter declaration of a method, constructor, or lambda expression.
	 * <pre>formal_parameter_target {
	 *   u1 formal_parameter_index;
	 * }</pre>
	 * The value of the formal_parameter_index item specifies which formal parameter declaration has
	 * an annotated type. A formal_parameter_index value of 0 specifies the first formal parameter
	 * declaration.
	 * The formal_parameter_target item records that a formal parameter's type is annotated, but does
	 * not record the type itself. The type may be found by inspecting the method descriptor (§4.3.3)
	 * of the method_info structure enclosing the RuntimeVisibleTypeAnnotations attribute. A formal_parameter_index
	 * value of 0 indicates the first parameter descriptor in the method descriptor.
	 * @author TeamworkGuy2
	 * @since 2014-3-19
	 */
	public static class Formal_Parameter_Target implements ReadWritable, Target_Info {
		byte formal_parameter_index;

		@Override
		public void changeCpIndex(short oldIndex, short newIndex) {
		}

		@Override
		public void writeData(DataOutput out) throws IOException {
			out.writeByte(formal_parameter_index);
		}

		@Override
		public void readData(DataInput in) throws IOException {
			formal_parameter_index = in.readByte();
		}

		@Override
		public String toString() {
			return "target_info.formal_parameter_target(formal_parameter_index=" + formal_parameter_index + ")";
		}
	}


	/** The throws_target item indicates that an annotation appears on the i'th type in the throws clause of
	 * a method or constructor declaration.
	 * <pre>throws_target {
	 *   u2 throws_type_index;
	 * }</pre>
	 * The value of the throws_type_index item is an index into the exception_index_table array of the
	 * Exceptions attribute of the method_info structure enclosing the RuntimeVisibleTypeAnnotations attribute. 
	 * @author TeamworkGuy2
	 * @since 2014-3-19
	 */
	public static class Throws_Target implements ReadWritable, Target_Info {
		short throws_type_index;

		@Override
		public void changeCpIndex(short oldIndex, short newIndex) {
		}

		@Override
		public void writeData(DataOutput out) throws IOException {
			out.writeShort(throws_type_index);
		}

		@Override
		public void readData(DataInput in) throws IOException {
			throws_type_index = in.readShort();
		}

		@Override
		public String toString() {
			return "target_info.throws_target(throws_type_index=" + throws_type_index + ")";
		}
	}


	/** The localvar_target item indicates that an annotation appears on the type in a local variable
	 * declaration, including a variable declared as a resource in a try-with-resources statement.
	 * <pre>localvar_target {
	 *   u2 table_length;
	 *   {
	 *     u2 start_pc;
	 *     u2 length;
	 *     u2 index;
	 *   } table[table_length];
	 * }</pre>
	 * The value of the table_length item gives the number of entries in the table array. Each entry
	 * indicates a range of code array offsets within which a local variable has a value. It also indicates
	 * the index into the local variable array of the current frame at which that local variable can be found.
	 * Each entry contains the following three items:<br/>
	 * start_pc, length
	 * <blockquote>The given local variable has a value at indices into the code array in the
	 * interval [start_pc, start_pc + length), that is, between start_pc inclusive and start_pc + length
	 * exclusive.</blockquote>
	 * <br/>
	 * index
	 * <blockquote>The given local variable must be at index in the local variable array of the current frame.
	 * If the local variable at index is of type double or long, it occupies both index and index + 1.</blockquote>
	 * A table is needed to fully specify the local variable whose type is annotated, because a single
	 * local variable may be represented with different local variable indices over multiple live ranges.
	 * The start_pc, length, and index items in each table entry specify the same information as a
	 * LocalVariableTable attribute.
	 * The localvar_target item records that a local variable's type is annotated, but does not record
	 * the type itself. The type may be found by inspecting the appropriate LocalVariableTable attribute. 
	 * @author TeamworkGuy2
	 * @since 2014-3-19
	 */
	public static class Localvar_Target implements ReadWritable, Target_Info {
		short table_length;
		Localvar_Target_Table_Entry[] table;

		@Override
		public void changeCpIndex(short oldIndex, short newIndex) {
			IndexUtility.indexChange(table, oldIndex, newIndex);
		}

		@Override
		public void writeData(DataOutput out) throws IOException {
			out.writeShort(table_length);
			for(int i = 0; i < table_length; i++) {
				table[i].writeData(out);
			}
		}

		@Override
		public void readData(DataInput in) throws IOException {
			table_length = in.readShort();
			table = new Localvar_Target_Table_Entry[table_length];
			for(int i = 0; i < table_length; i++) {
				table[i] = new Localvar_Target_Table_Entry();
				table[i].readData(in);
			}
		}

		@Override
		public String toString() {
			StringBuilder strB = new StringBuilder();
			strB.append("target_info.localvar_target(table_length=" + table_length + ", table[");
			for(int i = 0, size = table_length-1; i < size; i++) {
				strB.append(table[i].toString() + ", ");
			}
			strB.append(table[table_length-1].toString());
			strB.append("])");
			return strB.toString();
		}
	}


	/** The catch_target item indicates that an annotation appears on the i'th type in an exception parameter
	 * declaration.
	 * <pre>catch_target {
	 *   u2 exception_table_index;
	 * }</pre>
	 * The value of the exception_table_index item is an index into the exception_table array of the Code
	 * attribute enclosing the RuntimeVisibleTypeAnnotations attribute.
	 * The possibility of more than one type in an exception parameter declaration arises from the multi-catch
	 * clause of the try statement, where the type of the exception parameter is a union of types (JLS §14.20).
	 * A compiler usually creates one exception_table entry for each type in the union, which allows the
	 * catch_target item to distinguish them. This preserves the correspondence between a type and its annotations. 
	 * @author TeamworkGuy2
	 * @since 2014-3-19
	 */
	public static class Catch_Target implements ReadWritable, Target_Info {
		short exception_table_index;

		@Override
		public void changeCpIndex(short oldIndex, short newIndex) {
		}

		@Override
		public void writeData(DataOutput out) throws IOException {
			out.writeShort(exception_table_index);
		}

		@Override
		public void readData(DataInput in) throws IOException {
			exception_table_index = in.readShort();
		}

		@Override
		public String toString() {
			return "target_info.catch_target(exception_table_index=" + exception_table_index + ")";
		}
	}


	/** The offset_target item indicates that an annotation appears on either the type in an instanceof
	 * expression or a new expression, or the type before the :: in a method reference expression.
	 * <pre>offset_target {
	 *   u2 offset;
	 * }</pre>
	 * The value of the offset item specifies the code array offset of either the instanceof bytecode
	 * instruction corresponding to the instanceof expression, the new bytecode instruction corresponding
	 * to the new expression, or the bytecode instruction corresponding to the method reference expression. 
	 * @author TeamworkGuy2
	 * @since 2014-3-19
	 */
	public static class Offset_Target implements ReadWritable, Target_Info {
		short offset;

		@Override
		public void changeCpIndex(short oldIndex, short newIndex) {
		}

		@Override
		public void writeData(DataOutput out) throws IOException {
			out.writeShort(offset);
		}

		@Override
		public void readData(DataInput in) throws IOException {
			offset = in.readShort();
		}

		@Override
		public String toString() {
			return "target_info.offset_target(offset=" + offset + ")";
		}
	}


	/** The type_argument_target item indicates that an annotation appears either on the i'th type in a
	 * cast expression, or on the i'th type argument in the explicit type argument list for any of the
	 * following: a new expression, an explicit constructor invocation statement, a method invocation expression,
	 * or a method reference expression.
	 * <pre>type_argument_target {
	 *   u2 offset;
	 *   u1 type_argument_index;
	 * }</pre>
	 * The value of the offset item specifies the code array offset of either the bytecode instruction
	 * corresponding to the cast expression, the new bytecode instruction corresponding to the new expression,
	 * the bytecode instruction corresponding to the explicit constructor invocation statement, the bytecode
	 * instruction corresponding to the method invocation expression, or the bytecode instruction corresponding
	 * to the method reference expression.
	 * For a cast expression, the value of the type_argument_index item specifies which type in the cast operator
	 * is annotated. A type_argument_index value of 0 specifies the first (or only) type in the cast operator.
	 * The possibility of more than one type in a cast expression arises from a cast to an intersection type.
	 * For an explicit type argument list, the value of the type_argument_index item specifies which type
	 * argument is annotated. A type_argument_index value of 0 specifies the first type argument. 
	 * @author TeamworkGuy2
	 * @since 2014-3-19
	 */
	public static class Type_Argument_Target implements ReadWritable, Target_Info {
		short offset;
		byte type_argument_index;

		@Override
		public void changeCpIndex(short oldIndex, short newIndex) {
		}

		@Override
		public void writeData(DataOutput out) throws IOException {
			out.writeShort(offset);
			out.writeByte(type_argument_index);
		}

		@Override
		public void readData(DataInput in) throws IOException {
			offset = in.readShort();
			type_argument_index = in.readByte();
		}

		@Override
		public String toString() {
			return "target_info.type_argument_target(offset=" + offset
					+ ", type_argument_index=" + type_argument_index + ")";
		}
	}

}
