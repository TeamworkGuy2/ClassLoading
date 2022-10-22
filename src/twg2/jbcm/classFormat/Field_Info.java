package twg2.jbcm.classFormat;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import twg2.jbcm.classFormat.attributes.Attribute_Type;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Utf8;
import twg2.jbcm.modify.CpIndexChangeable;
import twg2.jbcm.modify.CpIndexChanger;

/** Java class file format <code>Field</code> info type
 * @author TeamworkGuy2
 * @since 2013-7-7
 */
public class Field_Info implements Externalizable, ReadWritable, CpIndexChangeable {
	/** Declared public; may be accessed from outside its package. */
	public static final int ACC_PUBLIC = 0x0001;
	/** Declared private; usable only within the defining class. */
	public static final int ACC_PRIVATE = 0x0002;
	/** Declared protected; may be accessed within subclasses. */
	public static final int ACC_PROTECTED = 0x0004;
	/** Declared static. */
	public static final int ACC_STATIC = 0x0008;
	/** Declared final; no further assignment after initialization. */
	public static final int ACC_FINAL = 0x0010;
	/** Declared volatile; cannot be cached. */
	public static final int ACC_VOLATILE = 0x0040;
	/** Declared transient; not written or read by a persistent object manager. */
	public static final int ACC_TRANSIENT = 0x0080;
	/** Declared synthetic; not present in the source code. */
	public static final int ACC_SYNTHETIC = 0x1000;
	/** Declared as an element of an enum. */
	public static final int ACC_ENUM = 0x4000;

	ClassFile resolver;
	/* The value of the access_flags item is a mask of flags used to denote access permission to and properties of
	 * this field. The interpretation of each flag, when set, is as shown in Table 4.4.
	 * Fields of classes may set any of the flags in Table 4.4. However, a specific field of a class may have at most
	 * one of its ACC_PRIVATE, ACC_PROTECTED, and ACC_PUBLIC flags set (§2.7.4) and may not have both its ACC_FINAL
	 * and ACC_VOLATILE flags set (§2.9.1).
	 * Flag Name 	Value 	Interpretation
	 * ACC_PUBLIC 	0x0001 	Declared public; may be accessed from outside its package.
	 * ACC_PRIVATE 	0x0002 	Declared private; usable only within the defining class.
	 * ACC_PROTECTED 	0x0004 	Declared protected; may be accessed within subclasses.
	 * ACC_STATIC 	0x0008 	Declared static.
	 * ACC_FINAL 	0x0010 	Declared final; no further assignment after initialization.
	 * ACC_VOLATILE 	0x0040 	Declared volatile; cannot be cached.
	 * ACC_TRANSIENT 	0x0080 	Declared transient; not written or read by a persistent object manager.
	 * ACC_SYNTHETIC 	0x1000 	Declared synthetic; not present in the source code.
	 * ACC_ENUM 	0x4000 	Declared as an element of an enum.
	 */
	short access_flags;
	/* The value of the name_index item must be a valid index into the constant_pool table.
	 * The constant_pool entry at that index must be a CONSTANT_Utf8_info (§4.4.7) structure
	 * which must represent a valid field name (§2.7) stored as a simple name (§2.7.1), that is,
	 * as a Java programming language identifier (§2.2).
	 */
	CpIndex<CONSTANT_Utf8> name_index;
	/* The value of the descriptor_index item must be a valid index into the constant_pool table.
	 * The constant_pool entry at that index must be a CONSTANT_Utf8_info (§4.4.7) structure
	 * that must represent a valid field descriptor (§4.3.2).
	 */
	CpIndex<CONSTANT_Utf8> descriptor_index;
	// The value of the attributes_count item indicates the number of additional attributes (§4.7) of this field.
	short attributes_count;
	//Each value of the attributes table must be an attribute structure (§4.7). A field can have any number of
	// attributes associated with it.
	// The attributes defined by this specification as appearing in the attributes table of a field_info structure
	// are the ConstantValue (§4.7.2), Synthetic (§4.7.6), and Deprecated (§4.7.10) attributes.
	// A Java virtual machine implementation must recognize and correctly read ConstantValue (§4.7.2) attributes
	// found in the attributes table of a field_info structure. A Java virtual machine implementation is required
	// to silently ignore any or all other attributes in the attributes table that it does not recognize. Attributes
	// not defined in this specification are not allowed to affect the semantics of the class file, but only to
	// provide additional descriptive information (§4.7.1).
	Attribute_Type[] attributes; // size [attributes_count], indexed from 0


	public Field_Info(ClassFile resolver) {
		super();
		this.resolver = resolver;
	}


	@Override
	public void changeCpIndex(CpIndexChanger indexChanger) {
		indexChanger.indexChange(name_index);
		indexChanger.indexChange(descriptor_index);
		indexChanger.indexChange(attributes);
	}


	public CONSTANT_Utf8 getName() {
		return name_index.getCpObject();
	}


	public CONSTANT_Utf8 getDescriptor() {
		return descriptor_index.getCpObject();
	}


	public Attribute_Type getAttribute(int index) {
		return attributes[index];
	}


	public int getAttributeCount() {
		return attributes_count;
	}


	public int getAccessFlags() {
		return access_flags & 0xFFFF;
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		out.writeShort(access_flags);
		name_index.writeData(out);
		descriptor_index.writeData(out);
		out.writeShort(attributes_count);
		for(int i = 0; i < attributes_count; i++) {
			attributes[i].writeData(out);
		}
	}


	@Override
	public void readData(DataInput in) throws IOException {
		access_flags = in.readShort();
		name_index = resolver.getCheckCpIndex(in.readShort(), CONSTANT_Utf8.class);
		descriptor_index = resolver.getCheckCpIndex(in.readShort(), CONSTANT_Utf8.class);
		attributes_count = in.readShort();
		attributes = new Attribute_Type[attributes_count];
		for(int i = 0; i < attributes_count; i++) {
			attributes[i] = ClassFileAttributes.loadAttributeObject(in, resolver, null);
		}
	}


	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		writeData(out);
	}


	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		readData(in);
	}


	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("Field_Info(access=" + Integer.toHexString(access_flags) + ", name=" + name_index.getCpObject() + ", descriptor=" + descriptor_index.getCpObject() + ", attributes " + attributes_count);
		if(attributes_count > 0) { str.append("=\n\t"); }
		for(int i = 0; i < attributes_count-1; i++) {
			str.append(attributes[i].toString() + ",\n\t");
		}
		if(attributes_count > 0) { str.append(attributes[attributes_count-1].toString()); }
		str.append(")");
		return str.toString();
	}

}
