package twg2.jbcm.classFormat.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.ReadWritable;
import twg2.jbcm.classFormat.Settings;
import twg2.jbcm.classFormat.constantPool.CONSTANT_CP_Info;
import twg2.jbcm.classFormat.constantPool.CONSTANT_MethodHandle;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Utf8;
import twg2.jbcm.modify.IndexUtility;

/** A Java class file format Attribute of type <code>BootstrapMethods</code>
 * @author TeamworkGuy2
 * @since 2013-10-6
 */
public class BootstrapMethods implements Attribute_Type {
	public static final String ATTRIBUTE_NAME = "BootstrapMethods";
	ClassFile resolver;
	/** The value of the attribute_name_index item must be a valid index into the constant_pool table.
	 * The constant_pool entry at that index must be a CONSTANT_Utf8_info structure (§4.4.7)
	 * representing the string "BootstrapMethods". 
	 */
	CpIndex<CONSTANT_Utf8> attribute_name_index;
	/** The value of the attribute_length item indicates the length of the attribute, excluding the initial six bytes.
	 * The value of the attribute_length item is thus dependent on the number of invokedynamic
	 * instructions in this ClassFile structure. 
	 */
	int attribute_length;
	/** The value of the num_bootstrap_methods item determines the number of bootstrap method
	 * specifiers in the bootstrap_methods array. 
	 */
	short num_bootstrap_methods;
	/** Each entry in the bootstrap_methods array contains an index to a CONSTANT_MethodHandle_info
	 * structure (§4.4.8) which specifies a bootstrap method, and a sequence (perhaps empty) of indexes
	 * to static arguments for the bootstrap method.<br>
	 * Each bootstrap_methods entry must contain the following three items:<pre>
	 * 	bootstrap_method_ref
	 * 	num_bootstrap_arguments
	 * 	bootstrap_arguments</pre>
	 */
	BootstrapMethod[] bootstrap_methods;


	public BootstrapMethods(ClassFile resolver, short attributeNameIndex) {
		this.attribute_name_index = Settings.initAttributeNameIndex(attributeNameIndex, resolver);
		this.resolver = resolver;
	}


	@Override
	public String getAttributeName() {
		return attribute_name_index.getCpObject().getString();
	}


	@Override
	public int getAttributeLength() {
		return attribute_length;
	}


	@Override
	public void changeCpIndex(short oldIndex, short newIndex) {
		IndexUtility.indexChange(attribute_name_index, oldIndex, newIndex);
		IndexUtility.indexChange(bootstrap_methods, oldIndex, newIndex);
	}


	public int getBootstrapMethodCount() {
		return num_bootstrap_methods;
	}


	public BootstrapMethod getBootstrapMethod(int index) {
		return bootstrap_methods[index];
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		attribute_name_index.writeData(out);
		out.writeInt(attribute_length);
		out.writeShort(num_bootstrap_methods);
		for(int i = 0; i < num_bootstrap_methods; i++) {
			bootstrap_methods[i].writeData(out);
		}
	}


	@Override
	public void readData(DataInput in) throws IOException {
		if(Settings.doReadAttributeName()) {
			attribute_name_index = Settings.readAttributeNameIndex(in, resolver, ATTRIBUTE_NAME);
		}
		attribute_length = in.readInt();
		num_bootstrap_methods = in.readShort();
		bootstrap_methods = new BootstrapMethod[num_bootstrap_methods];
		for(int i = 0; i < num_bootstrap_methods; i++) {
			bootstrap_methods[i] = new BootstrapMethod(resolver);
			bootstrap_methods[i].readData(in);
		}
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(ATTRIBUTE_NAME).append("(methods=").append(num_bootstrap_methods).append("\n");
		for(int i = 0; i < num_bootstrap_methods; i++) {
			sb.append("\t").append(bootstrap_methods[i]).append(",\n");
		}
		sb.append(")");
		return sb.toString();
	}



	/** A Java class file format Attribute of type <code>BootstrapMethod</code> for {@link BootstrapMethods}
	 * @author TeamworkGuy2
	 * @since 2013-10-6
	 */
	public static class BootstrapMethod implements ReadWritable {
		ClassFile resolver;
		/** The value of the bootstrap_method_ref item must be a valid index into the constant_pool table.
		 * The constant_pool entry at that index must be a CONSTANT_MethodHandle_info structure (§4.4.8).
		 * The reference_kind item of the CONSTANT_MethodHandle_info structure should have the
		 * value 6 (REF_invokeStatic) or 8 (REF_newInvokeSpecial) (§5.4.3.5) or else invocation
		 * of the bootstrap method handle during call site specifier resolution for an
		 * invokedynamic instruction will complete abruptly.
		 */
		CpIndex<CONSTANT_MethodHandle> bootstrap_method_ref;
		/** The value of the num_bootstrap_arguments item gives the number of items in the bootstrap_arguments array.
		 */
		short num_bootstrap_arguments;
		/** Each entry in the bootstrap_arguments array must be a valid index into the constant_pool table.
		 * The constant_pool entry at that index must be a CONSTANT_String_info, CONSTANT_Class_info,
		 * CONSTANT_Integer_info, CONSTANT_Long_info, CONSTANT_Float_info, CONSTANT_Double_info,
		 * CONSTANT_MethodHandle_info, or CONSTANT_MethodType_infostructure
		 * (§4.4.3, §4.4.1, §4.4.4, §4.4.5, §4.4.8, §4.4.9).
		 */
		CpIndex<? extends CONSTANT_CP_Info>[] bootstrap_arguments;


		public BootstrapMethod(ClassFile resolver) {
			this.resolver = resolver;
		}


		@Override
		public void changeCpIndex(short oldIndex, short newIndex) {
			IndexUtility.indexChange(bootstrap_method_ref, oldIndex, newIndex);
			IndexUtility.indexChange(bootstrap_arguments, oldIndex, newIndex);
		}


		@Override
		public void writeData(DataOutput out) throws IOException {
			bootstrap_method_ref.writeData(out);
			out.writeShort(num_bootstrap_arguments);
			for(int i = 0; i < num_bootstrap_arguments; i++) {
				bootstrap_arguments[i].writeData(out);
			}
		}


		@SuppressWarnings("unchecked")
		@Override
		public void readData(DataInput in) throws IOException {
			bootstrap_method_ref = resolver.getCheckCpIndex(in.readShort(), CONSTANT_MethodHandle.class);
			num_bootstrap_arguments = in.readShort();
			bootstrap_arguments = new CpIndex[num_bootstrap_arguments];
			for(int i = 0; i < num_bootstrap_arguments; i++) {
				bootstrap_arguments[i] = Settings.getBootstrap_ArgumentType(in, resolver);
			}
		}


		@Override
		public String toString() {
			StringBuilder strB = new StringBuilder();
			strB.append("BootstrapMethod(" + bootstrap_method_ref.getCpObject());
			for(int i = 0; i < num_bootstrap_arguments; i++) {
				strB.append(", " + bootstrap_arguments[i].getCpObject());
			}
			strB.append(")");
			return strB.toString();
		}

	}

}
