package twg2.jbcm.classFormat.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.ConstantPoolAllowZero;
import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.ReadWritable;
import twg2.jbcm.classFormat.Settings;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Utf8;
import twg2.jbcm.modify.IndexUtility;

/** A Java class file format Attribute of type <code>MethodParameters</code>
 * @author TeamworkGuy2
 * @since 2014-3-19
 */
public class MethodParameters implements Attribute_Type {
	public static final String ATTRIBUTE_NAME = "MethodParameters";
	ClassFile resolver;
	/** The value of the attribute_name_index item must be a valid index into the constant_pool table.
	 * The constant_pool entry at that index must be a CONSTANT_Utf8_info structure representing the
	 * string "MethodParameters".
	 */
	CpIndex<CONSTANT_Utf8> attribute_name_index;
	/** The value of the attribute_length item indicates the length of the attribute,
	 * excluding the initial six bytes. */
	int attribute_length;
	/** The value of the parameters_count item indicates the number of parameter descriptors in the
	 * method descriptor (§4.3.3) referenced by the descriptor_index of the attribute's enclosing
	 * method_info structure.
	 * This is not a constraint which a Java Virtual Machine implementation must enforce during
	 * format checking (§4.8). The task of matching parameter descriptors in a method descriptor
	 * against the items in the parameters array below is done by the reflection libraries of the
	 * Java SE platform.
	 */ 
	byte parameters_count;
	/** Each entry in the parameters array contains the following pair of items:
	 * name_index<br/>
	 * 	The value of the name_index item must either be zero or a valid index into the constant_pool table.<br/>
	 * 	If the value of the name_index item is zero, then this parameters element indicates a formal parameter with no name.<br/>
	 * 	If the value of the name_index item is nonzero, the constant_pool entry at that index must be a CONSTANT_Utf8_info structure representing a valid unqualified name denoting a formal parameter (§4.2.2).<br/>
	 * <br/>
	 * access_flags<br/>
	 * 	The value of the access_flags item is as follows:<br/>
	 * 	0x0010 (ACC_FINAL)<br/>
	 * 		Indicates that the formal parameter was declared final.<br/>
	 * 	0x1000 (ACC_SYNTHETIC)<br/>
	 * 		Indicates that the formal parameter was not explicitly or implicitly declared in source code, according to the specification of the language in which the source code was written (JLS §13.1). (The formal parameter is an implementation artifact of the compiler which produced this class file.)<br/>
	 * 	0x8000 (ACC_MANDATED)<br/>
	 * 		Indicates that the formal parameter was implicitly declared in source code, according to the specification of the language in which the source code was written (JLS §13.1). (The formal parameter is mandated by a language specification, so all compilers for the language must emit it.)<br/>
	 * <br/>
	 * The i'th entry in the parameters array corresponds to the i'th parameter descriptor in the enclosing
	 * method's descriptor. (The parameters_count item is one byte because a method descriptor is limited to
	 * 255 parameters.) Effectively, this means the parameters array stores information for all the parameters
	 * of the method. One could imagine other schemes, where entries in the parameters array specify their
	 * corresponding parameter descriptors, but it would unduly complicate the MethodParameters attribute.<br/>
	 * The i'th entry in the parameters array may or may not correspond to the i'th type in the enclosing method's
	 * Signature attribute (if present), or to the i'th annotation in the enclosing method's parameter annotations. 
	 */
	Parameter_Entry[] parameters;


	public MethodParameters(ClassFile resolver, short attributeNameIndex) {
		this.attribute_name_index = Settings.initAttributeNameIndex(attributeNameIndex, resolver);
		this.resolver = resolver;
	}


	@Override
	public String getAttributeName() {
		return ATTRIBUTE_NAME;
	}


	@Override
	public int getAttributeLength() {
		return attribute_length;
	}


	@Override
	public void changeCpIndex(short oldIndex, short newIndex) {
		IndexUtility.indexChange(attribute_name_index, oldIndex, newIndex);
		IndexUtility.indexChange(parameters, oldIndex, newIndex);
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		attribute_name_index.writeData(out);
		out.writeInt(attribute_length);
		out.writeByte(parameters_count);
		for(int i = 0; i < parameters_count; i++) {
			parameters[i].writeData(out);
		}
	}


	@Override
	public void readData(DataInput in) throws IOException {
		if(Settings.doReadAttributeName()) {
			attribute_name_index = Settings.readAttributeNameIndex(in, resolver, ATTRIBUTE_NAME);
		}
		attribute_length = in.readInt();
		parameters_count = in.readByte();
		parameters = new Parameter_Entry[parameters_count];
		for(int i = 0; i < parameters_count; i++) {
			parameters[i] = new Parameter_Entry(resolver);
			parameters[i].readData(in);
		}
	}


	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append(ATTRIBUTE_NAME);
		str.append("(parameters[");
		for(int i = 0; i < parameters_count-1; i++) {
			str.append(parameters[i]);
			str.append(", ");
		}
		if(parameters_count > 0) { str.append(parameters[parameters_count-1]); }
		str.append("])");
		return str.toString();
	}



	/** A member of {@link MethodParameters}.
	 * @author TeamworkGuy2
	 * @since 2014-3-19
	 */
	public static class Parameter_Entry implements ReadWritable {
		ClassFile resolver;
		/** The value of the name_index item must either be zero or a valid index into the constant_pool table.<br/>
		 * If the value of the name_index item is zero, then this parameters element indicates a formal
		 * parameter with no name.<br/>
		 * If the value of the name_index item is nonzero, the constant_pool entry at that index must be
		 * a CONSTANT_Utf8_info structure representing a valid unqualified name denoting a formal parameter (§4.2.2).
		 */
		@ConstantPoolAllowZero
		CpIndex<CONSTANT_Utf8> name_index;
		/** The value of the access_flags item is as follows:<br/>
		 * 0x0010 (ACC_FINAL) - Indicates that the formal parameter was declared final.<br/>
		 * <br/>
		 * 0x1000 (ACC_SYNTHETIC) - Indicates that the formal parameter was not explicitly or implicitly
		 * declared in source code, according to the specification of the language in which the source code
		 * was written (JLS §13.1). (The formal parameter is an implementation artifact of the compiler which
		 * produced this class file.)<br/>
		 * <br/>
		 * 0x8000 (ACC_MANDATED) - Indicates that the formal parameter was implicitly declared in source code,
		 * according to the specification of the language in which the source code was written (JLS §13.1).
		 * (The formal parameter is mandated by a language specification, so all compilers for the language must emit it.)
		 */
		short access_flags;


		public Parameter_Entry(ClassFile resolver) {
			this.resolver = resolver;
		}


		public Parameter_Entry(ClassFile resolver, short nameIndex, short accessFlags) {
			this.resolver = resolver;
			this.name_index = resolver.getCheckCpIndex(nameIndex, CONSTANT_Utf8.class);
			this.access_flags = accessFlags;
		}


		@Override
		public void changeCpIndex(short oldIndex, short newIndex) {
			if(name_index.getIndex() != 0) {
				IndexUtility.indexChange(name_index, oldIndex, newIndex);
			}
		}


		@Override
		public void writeData(DataOutput out) throws IOException {
			name_index.writeData(out);
			out.writeShort(access_flags);
		}


		@Override
		public void readData(DataInput in) throws IOException {
			name_index = resolver.getCheckCpIndex(in.readShort(), CONSTANT_Utf8.class, true);
			access_flags = in.readShort();
		}


		@Override
		public String toString() {
			return "parameter(name=" + (name_index.getIndex() > 0 ? name_index.getCpObject().getString() : "") + ", access=" + access_flags + ")";
		}

	}

}
