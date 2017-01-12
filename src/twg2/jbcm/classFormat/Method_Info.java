package twg2.jbcm.classFormat;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import twg2.jbcm.classFormat.attributes.Attribute_Type;
import twg2.jbcm.classFormat.attributes.Code;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Utf8;
import twg2.jbcm.modify.IndexUtility;

/** Java class file format <code>Method</code> info type
 * @author TeamworkGuy2
 * @since 2013-7-7
 */
public class Method_Info implements Externalizable, ReadWritable {
	ClassFile resolver;
	/* The value of the access_flags item is a mask of flags used to denote access permission to and properties of
	 * this method. The interpretation of each flag, when set, is as shown in Table 4.5.
	 * Flag Name 	Value 	Interpretation
	 * ACC_PUBLIC 	0x0001 	Declared public; may be accessed from outside its package.
	 * ACC_PRIVATE 	0x0002 	Declared private; accessible only within the defining class.
	 * ACC_PROTECTED 	0x0004 	Declared protected; may be accessed within subclasses.
	 * ACC_STATIC 	0x0008 	Declared static.
	 * ACC_FINAL 	0x0010 	Declared final; may not be overridden.
	 * ACC_SYNCHRONIZED 	0x0020 	Declared synchronized; invocation is wrapped in a monitor lock.
	 * ACC_NATIVE 	0x0100 	Declared native; implemented in a language other than Java.
	 * ACC_ABSTRACT 	0x0400 	Declared abstract; no implementation is provided.
	 * ACC_STRICT 	0x0800 	Declared strictfp; floating-point mode is FP-strict 
	 */
	short access_flags;
	/* The value of the name_index item must be a valid index into the constant_pool table. The constant_pool entry
	 * at that index must be a CONSTANT_Utf8_info (§4.4.7) structure representing either one of the special method
	 * names (§3.9), <init> or <clinit>, or a valid method name in the Java programming language (§2.7), stored
	 * as a simple name (§2.7.1).
	 */
	CpIndex<CONSTANT_Utf8> name_index;
	/* The value of the descriptor_index item must be a valid index into the constant_pool table.
	 * The constant_pool entry at that index must be a CONSTANT_Utf8_info (§4.4.7) structure
	 * representing a valid method descriptor (§4.3.3).
	 */
	CpIndex<CONSTANT_Utf8> descriptor_index;
	/* The value of the attributes_count item indicates the number of additional attributes (§4.7) of this method. */
	short attributes_count;
	// Each value of the attributes table must be an attribute structure (§4.7). A method can have any number of optional
	// attributes associated with it.
	// The only attributes defined by this specification as appearing in the attributes table of a method_info structure
	// are the Code (§4.7.3), Exceptions (§4.7.4), Synthetic (§4.7.6), and Deprecated (§4.7.10) attributes.
	// A Java virtual machine implementation must recognize and correctly read Code (§4.7.3) and Exceptions (§4.7.4)
	// attributes found in the attributes table of a method_info structure. A Java virtual machine implementation is
	// required to silently ignore any or all other attributes in the attributes table of a method_info structure
	// that it does not recognize. Attributes not defined in this specification are not allowed to affect the
	// semantics of the class file, but only to provide additional descriptive information (§4.7.1),
	// size [attributes_count], indexed from 0.
	Attribute_Type[] attributes;


	public Method_Info(ClassFile resolver) {
		super();
		this.resolver = resolver;
	}


	@Override
	public void changeCpIndex(short oldIndex, short newIndex) {
		IndexUtility.indexChange(name_index, oldIndex, newIndex);
		IndexUtility.indexChange(descriptor_index, oldIndex, newIndex);
		IndexUtility.indexChange(attributes, oldIndex, newIndex);
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


	/** TODO improve implementation
	 * @return this method's code attribute (if it has one), or null if the method is native or abstract
	 */
	public Code getCode() {
		Code code = null;
		for(int i = 0; i < attributes_count; i++) {
			if(Code.ATTRIBUTE_NAME.equals(attributes[i].getAttributeName())) {
				if(code == null) {
					code = (Code)attributes[i];
				}
				else { throw new IllegalStateException("a method should not have more than 1 code attribute"); }
			}
		}
		return code;
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
			attributes[i] = Settings.loadAttributeObject(in, resolver, null);
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
		str.append("Method_Info(access=" + Integer.toHexString(access_flags) + ", name=" + name_index.getCpObject() + ", descriptor=" + descriptor_index.getCpObject() + ", attributes " + attributes_count);
		if(attributes_count > 0) { str.append("=\n"); }
		for(int i = 0; i < attributes_count-1; i++) {
			str.append(attributes[i].toString() + ",\n");
		}
		if(attributes_count > 0) {
			str.append(attributes[attributes_count-1].toString() + "\n])");
		}
		return str.toString();
	}

}
