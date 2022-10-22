package twg2.jbcm.classFormat.constantPool;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.Settings;
import twg2.jbcm.classFormat.attributes.BootstrapMethods;
import twg2.jbcm.classFormat.attributes.BootstrapMethods.BootstrapMethod;
import twg2.jbcm.modify.CpIndexChanger;

/** Java class file format constant pool <code>InvokeDynamic reference</code> info type.<br>
 * Constant value = 18, class version = 51.0, Java SE = 7
 * @author TeamworkGuy2
 * @since 2013-10-6
 */
public class CONSTANT_InvokeDynamic implements CONSTANT_CP_Info {
	public static final byte TAG = 18;
	ClassFile resolver;

	/** The value of the bootstrap_method_attr_index item must be a valid index into the bootstrap_methods
	 * array of the bootstrap method table (§4.7.21) of this class file. 
	 */
	short bootstrap_method_attr_index;
	/** The value of the name_and_type_index item must be a valid index into the constant_pool table.
	 * The constant_pool entry at that index must be a CONSTANT_NameAndType_info structure (§4.4.6)
	 * representing a method name and method descriptor (§4.3.3). 
	 */
	CpIndex<CONSTANT_NameAndType> name_and_type_index;


	public CONSTANT_InvokeDynamic(ClassFile resolver) {
		this.resolver = resolver;
	}


	@Override
	public int getTag() {
		return TAG;
	}


	@Override
	public void changeCpIndex(CpIndexChanger indexChanger) {
		indexChanger.indexChange(name_and_type_index);
	}


	public BootstrapMethod getBootstrapMethod() {
		return resolver.getBootstrapMethods().getBootstrapMethod(bootstrap_method_attr_index);
	}


	public CONSTANT_NameAndType getNameAndType() {
		return name_and_type_index.getCpObject();
	}


	public void setBootstrapMethodAttrIndex(int index) {
		resolver.getBootstrapMethods().getBootstrapMethod(index);
		this.bootstrap_method_attr_index = (short)index;
	}

	public void setNameAndTypeIndex(CpIndex<CONSTANT_NameAndType> index) {
		this.name_and_type_index = index;
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		out.writeByte(TAG);
		out.writeShort(bootstrap_method_attr_index);
		name_and_type_index.writeData(out);
	}


	@Override
	public void readData(DataInput in) throws IOException {
		if(!Settings.cpTagRead) {
			int tagV = in.readByte();
			if(tagV != TAG) { throw new IllegalStateException("Illegal CONSTANT_Fieldref tag: " + tagV); }
		}
		bootstrap_method_attr_index = in.readShort();
		name_and_type_index = resolver.getExpectCpIndex(in.readShort(), CONSTANT_NameAndType.class);
	}


	@Override
	public String toShortString() {
		return toString();
	}


	@Override
	public String toString() {
		BootstrapMethods bootstrapMethod = ((BootstrapMethods)resolver.getBootstrapMethods());
		CONSTANT_NameAndType method = name_and_type_index.getCpObject();
		// null checks for malformed classes, for example, when a constant pool item references another item ahead of
		// it in the constant pool that hasn't been parsed yet
		return "InvokeDynamic(18, invoke_dynamic=" + bootstrapMethod.getBootstrapMethod(bootstrap_method_attr_index) +
				", name=" + (method != null ? method.getName() : name_and_type_index) +
				", type=" + (method != null ? method.getDescriptor() : null) + ")";
	}

}
