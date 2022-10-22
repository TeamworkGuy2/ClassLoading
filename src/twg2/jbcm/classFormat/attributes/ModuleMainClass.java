package twg2.jbcm.classFormat.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.ClassFileAttributes;
import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.Settings;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Class;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Utf8;
import twg2.jbcm.modify.CpIndexChanger;

/** A Java class file format Attribute of type <code>Module</code>
 * @author TeamworkGuy2
 * @since 2017-12-22
 */
public class ModuleMainClass implements Attribute_Type {
	public static final String ATTRIBUTE_NAME = "ModuleMainClass";
	ClassFile resolver;
	/** The value of the attribute_name_index item must be a valid index into the constant_pool table.
	 * The constant_pool entry at that index must be a CONSTANT_Utf8_info structure representing the
	 * string "ModuleMainClass".
	 */
	CpIndex<CONSTANT_Utf8> attribute_name_index;
	/** The value of the attribute_length item indicates the length of the attribute,
	 * excluding the initial six bytes. */
	int attribute_length;
	/** The value of the main_class_index item must be a valid index into the constant_pool table. The constant_pool entry
	 * at that index must be a CONSTANT_Class_info structure representing the binary name of the main class of the current module.
	 */
	CpIndex<CONSTANT_Class> main_class_index;


	public ModuleMainClass(ClassFile resolver, short attributeNameIndex) {
		this.attribute_name_index = resolver.getAttributeNameIndex(attributeNameIndex);
		this.resolver = resolver;
	}


	public ModuleMainClass(ClassFile resolver) {
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
	public void changeCpIndex(CpIndexChanger indexChanger) {
		indexChanger.indexChange(main_class_index);
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		attribute_name_index.writeData(out);
		out.writeInt(attribute_length);
		main_class_index.writeData(out);
	}


	@Override
	public void readData(DataInput in) throws IOException {
		if(Settings.readAttributeName) {
			attribute_name_index = ClassFileAttributes.readAttributeNameIndex(in, resolver, ATTRIBUTE_NAME);
		}
		attribute_length = in.readInt();
		main_class_index = resolver.getCheckCpIndex(in.readShort(), CONSTANT_Class.class);
	}


	@Override
	public String toString() {
		return "ModuleMainClass(main_class=" + main_class_index.getCpObject() + ")";
	}

}
