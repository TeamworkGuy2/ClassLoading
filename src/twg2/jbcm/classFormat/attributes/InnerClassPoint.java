package twg2.jbcm.classFormat.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.ReadWritable;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Class;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Utf8;
import twg2.jbcm.modify.IndexUtility;

/** A Java class file format InnerClasses Attribute for an inner class
 * @author TeamworkGuy2
 * @since 2013-7-7
 */
public class InnerClassPoint implements ReadWritable {
	ClassFile resolver;
	/* Every CONSTANT_Class_info entry in the constant_pool table which represents a class or interface C that is not
	 * a package member must have exactly one corresponding entry in the classes array.
	 * If a class has members that are classes or interfaces, its constant_pool table
	 * (and hence its InnerClasses attribute) must refer to each such member, even if that member is not otherwise
	 * mentioned by the class. These rules imply that a nested class or interface member will have InnerClasses
	 * information for each enclosing class and for each immediate member.
	 * Each classes array entry contains the following four items:
	 * inner_class_info_index
	 * The value of the inner_class_info_index item must be zero or a valid index into the constant_pool table.
	 * The constant_pool entry at that index must be a CONSTANT_Class_info (§4.4.1) structure representing C.
	 * The remaining items in the classes array entry give information about C.
	 */
	CpIndex<CONSTANT_Class> inner_class_info_index;
	/* outer_class_info_index
	 * If C is not a member, the value of the outer_class_info_index item must be zero. Otherwise, the value of the
	 * outer_class_info_index item must be a valid index into the constant_pool table, and the entry at that index
	 * must be a CONSTANT_Class_info (§4.4.1) structure representing the class or interface of which C is a member.
	 */
	CpIndex<CONSTANT_Class> outer_class_info_index;
	/* inner_name_index
	 * If C is anonymous, the value of the inner_name_index item must be zero. Otherwise, the value of the
	 * inner_name_index item must be a valid index into the constant_pool table, and the entry at that index must
	 * be a CONSTANT_Utf8_info (§4.4.7) structure that represents the original simple name of C, as given in the
	 * source code from which this class file was compiled.
	 */
	CpIndex<CONSTANT_Utf8> inner_name_index;
	/* inner_class_access_flags
	 * The value of the inner_class_access_flags item is a mask of flags used to denote access permissions to and
	 * properties of class or interface C as declared in the source code from which this class file was compiled.
	 * It is used by compilers to recover the original information when source code is not available. The flags are
	 * shown in Table 4.7.
	 * Flag Name 	Value 	Meaning
	 * ACC_PUBLIC 	0x0001 	Marked or implicitly public in source.
	 * ACC_PRIVATE 	0x0002 	Marked private in source.
	 * ACC_PROTECTED 	0x0004 	Marked protected in source.
	 * ACC_STATIC 	0x0008 	Marked or implicitly static in source.
	 * ACC_FINAL 	0x0010 	Marked final in source.
	 * ACC_INTERFACE 	0x0200 	Was an interface in source.
	 * ACC_ABSTRACT 	0x0400 	Marked or implicitly abstract in source.
	 * 
	 * All bits of the inner_class_access_flags item not assigned in Table 4.7 are reserved for future use.
	 * They should be set to zero in generated class files and should be ignored by Java virtual machine implementations.
	 */
	short inner_class_access_flags;


	public InnerClassPoint(ClassFile resolver) {
		this.resolver = resolver;
	}


	@Override
	public void changeCpIndex(short oldIndex, short newIndex) {
		IndexUtility.indexChange(inner_class_info_index, oldIndex, newIndex);
		IndexUtility.indexChange(outer_class_info_index, oldIndex, newIndex);
		IndexUtility.indexChange(inner_name_index, oldIndex, newIndex);
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		inner_class_info_index.writeData(out);
		outer_class_info_index.writeData(out);
		inner_name_index.writeData(out);
		out.writeShort(inner_class_access_flags);
	}


	@Override
	public void readData(DataInput in) throws IOException {
		inner_class_info_index = resolver.getCheckCpIndex(in.readShort(), CONSTANT_Class.class);
		outer_class_info_index = resolver.getCheckCpIndex(in.readShort(), CONSTANT_Class.class);
		inner_name_index = resolver.getCheckCpIndex(in.readShort(), CONSTANT_Utf8.class);
		inner_class_access_flags = in.readShort();
	}


	@Override
	public String toString() {
		return "InnerClass(access=" + inner_class_access_flags + ", name=" + ((inner_name_index.getIndex() > 0) ? inner_name_index.getCpObject().getString() : "") + ", inner_class=" + ((inner_class_info_index.getIndex() > 0) ? inner_class_info_index.getCpObject() : "") + ", outer_class=" + ((outer_class_info_index.getIndex() > 0) ? outer_class_info_index.getCpObject() : "") + ")";
	}

}
