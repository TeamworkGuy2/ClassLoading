package twg2.jbcm.classFormat.constantPool;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.Settings;
import twg2.jbcm.modify.CpIndexChanger;
import twg2.jbcm.modify.TypeUtility;

/** Java class file format constant pool <code>Field reference</code> info type.<br>
 * Constant value = 9, class version = 45.3, Java SE = 1.0.2
 * @author TeamworkGuy2
 * @since 2013-7-7
 */
public class CONSTANT_Fieldref implements CONSTANT_CP_Info {
	public static final int TAG = 9;
	ClassFile resolver;

	/** The value of the class_index item must be a valid index into the constant_pool table. The constant_pool entry
	 * at that index must be a CONSTANT_Class_info (§4.4.1) structure representing the class or interface type that
	 * contains the declaration of the field or method.
	 * The class_index item of a CONSTANT_Fieldref_info structure may be either a class type or an interface type.
	 */
	CpIndex<CONSTANT_Class> class_index;
	/** The value of the name_and_type_index item must be a valid index into the constant_pool table. The constant_pool
	 * entry at that index must be a CONSTANT_NameAndType_info (§4.4.6) structure. This constant_pool entry indicates
	 * the name and descriptor of the field or method. In a CONSTANT_Fieldref_info the indicated descriptor must be a
	 * field descriptor (§4.3.2). Otherwise, the indicated descriptor must be a method descriptor (§4.3.3).
	 */
	CpIndex<CONSTANT_NameAndType> name_and_type_index;


	public CONSTANT_Fieldref(ClassFile resolver) {
		this.resolver = resolver;
	}


	@Override
	public int getTag() {
		return TAG;
	}


	@Override
	public void changeCpIndex(CpIndexChanger indexChanger) {
		indexChanger.indexChange(class_index);
		indexChanger.indexChange(name_and_type_index);
	}


	public CONSTANT_Class getClassType() {
		return class_index.getCpObject();
	}


	public CONSTANT_NameAndType getNameAndType() {
		return name_and_type_index.getCpObject();
	}


	public void setClassIndex(CpIndex<CONSTANT_Class> index) {
		this.class_index = index;
	}


	public void setNameAndTypeIndex(CpIndex<CONSTANT_NameAndType> index) {
		this.name_and_type_index = index;
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		out.writeByte(TAG);
		class_index.writeData(out);
		name_and_type_index.writeData(out);
	}


	@Override
	public void readData(DataInput in) throws IOException {
		if(!Settings.cpTagRead) {
			int tagV = in.readByte();
			if(tagV != TAG) { throw new IllegalStateException("Illegal CONSTANT_Fieldref tag: " + tagV); }
		}
		class_index = resolver.getExpectCpIndex(in.readShort(), CONSTANT_Class.class);
		name_and_type_index = resolver.getExpectCpIndex(in.readShort(), CONSTANT_NameAndType.class);
	}


	@Override
	public String toShortString() {
		CONSTANT_Class clazz = class_index.getCpObject();
		CONSTANT_NameAndType field = name_and_type_index.getCpObject();

		StringBuilder dst = new StringBuilder();
		dst.append(clazz.getName().getString()).append(".").append(field.getName().getString()).append(" : ");
		TypeUtility.typeDescriptorToSource(field.getDescriptor().getString(), dst);
		return dst.toString();
	}


	@Override
	public String toString() {
		CONSTANT_Class clazz = class_index.getCpObject();
		CONSTANT_NameAndType field = name_and_type_index.getCpObject();
		// null checks for malformed classes, for example, when a constant pool item references another item ahead of
		// it in the constant pool that hasn't been parsed yet
		return "Fieldref(9, class=" + (clazz != null ? clazz.getName() : class_index) +
				", name=" + (field != null ? field.getName() : name_and_type_index) +
				", type=" + (field != null ? field.getDescriptor() : null) + ")";
	}

}
