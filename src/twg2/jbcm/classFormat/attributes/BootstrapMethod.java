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
import twg2.jbcm.modify.IndexUtility;

/** A Java class file format Attribute of type <code>BootstrapMethod</code> for {@link BootstrapMethods}
 * @author TeamworkGuy2
 * @since 2013-10-6
 */
public class BootstrapMethod implements ReadWritable {
	ClassFile resolver;
	/* The value of the bootstrap_method_ref item must be a valid index into the constant_pool table.
	 * The constant_pool entry at that index must be a CONSTANT_MethodHandle_info structure (§4.4.8).
	 * The reference_kind item of the CONSTANT_MethodHandle_info structure should have the
	 * value 6 (REF_invokeStatic) or 8 (REF_newInvokeSpecial) (§5.4.3.5) or else invocation
	 * of the bootstrap method handle during call site specifier resolution for an
	 * invokedynamic instruction will complete abruptly.
	 */
	CpIndex<CONSTANT_MethodHandle> bootstrap_method_ref;
	/* The value of the num_bootstrap_arguments item gives the number of items in the bootstrap_arguments array.
	 */
	short num_bootstrap_arguments;
	/* Each entry in the bootstrap_arguments array must be a valid index into the constant_pool table.
	 * The constant_pool entry at that index must be a CONSTANT_String_info, CONSTANT_Class_info,
	 * CONSTANT_Integer_info, CONSTANT_Long_info, CONSTANT_Float_info, CONSTANT_Double_info,
	 * CONSTANT_MethodHandle_info, or CONSTANT_MethodType_infostructure
	 * (§4.4.3, §4.4.1, §4.4.4, §4.4.5, §4.4.8, §4.4.9).
	 */
	// One of: CONSTANT_String.class, CONSTANT_Class.class, CONSTANT_Integer.class, CONSTANT_Long.class,
	// CONSTANT_Float.class, CONSTANT_Double.class, CONSTANT_MethodHandle.class, CONSTANT_MethodType.class
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
