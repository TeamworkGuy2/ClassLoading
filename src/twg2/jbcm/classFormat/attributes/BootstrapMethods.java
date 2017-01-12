package twg2.jbcm.classFormat.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.Settings;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Utf8;
import twg2.jbcm.modify.IndexUtility;

/** A Java class file format Attribute of type <code>BootstrapMethods</code>
 * @author TeamworkGuy2
 * @since 2013-10-6
 */
public class BootstrapMethods implements Attribute_Type {
	public static final String ATTRIBUTE_NAME = "BootstrapMethods";
	ClassFile resolver;
	/* The value of the attribute_name_index item must be a valid index into the constant_pool table.
	 * The constant_pool entry at that index must be a CONSTANT_Utf8_info structure (§4.4.7)
	 * representing the string "BootstrapMethods". 
	 */
	CpIndex<CONSTANT_Utf8> attribute_name_index;
	/* The value of the attribute_length item indicates the length of the attribute, excluding the initial six bytes.
	 * The value of the attribute_length item is thus dependent on the number of invokedynamic
	 * instructions in this ClassFile structure. 
	 */
	int attribute_length;
	/* The value of the num_bootstrap_methods item determines the number of bootstrap method
	 * specifiers in the bootstrap_methods array. 
	 */
	short num_bootstrap_methods;
	/* Each entry in the bootstrap_methods array contains an index to a CONSTANT_MethodHandle_info
	 * structure (§4.4.8) which specifies a bootstrap method, and a sequence (perhaps empty) of indexes
	 * to static arguments for the bootstrap method.
	 * Each bootstrap_methods entry must contain the following three items:
	 * 	bootstrap_method_ref
	 * 	num_bootstrap_arguments
	 * 	bootstrap_arguments
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
		StringBuilder strB = new StringBuilder();
		strB.append(ATTRIBUTE_NAME + "(methods=" + num_bootstrap_methods + "\n");
		for(int i = 0; i < num_bootstrap_methods; i++) {
			strB.append("\t" + bootstrap_methods[i] + ",\n");
		}
		strB.append(")");
		return strB.toString();
	}

}
