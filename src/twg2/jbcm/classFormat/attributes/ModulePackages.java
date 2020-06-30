package twg2.jbcm.classFormat.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.ClassFileAttributes;
import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.Settings;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Package;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Utf8;
import twg2.jbcm.modify.IndexUtility;

/** A Java class file format Attribute of type <code>ModulePackages</code>
 * @author TeamworkGuy2
 * @since 2017-12-22
 */
public class ModulePackages implements Attribute_Type {
	public static final String ATTRIBUTE_NAME = "ModulePackages";
	ClassFile resolver;
	/** The value of the attribute_name_index item must be a valid index into the constant_pool table.
	 * The constant_pool entry at that index must be a CONSTANT_Utf8_info structure representing the
	 * string "ModulePackages".
	 */
	CpIndex<CONSTANT_Utf8> attribute_name_index;
	/** The value of the attribute_length item indicates the length of the attribute,
	 * excluding the initial six bytes. */
	int attribute_length;
	/** The value of the package_count item indicates the number of entries in the package_index table.
	 */
	short package_count;
	/** The value of each entry in the package_index table must be a valid index into the constant_pool table. The constant_pool
	 * entry at that index must be a CONSTANT_Package_info structure representing a package in the current module.
	 * At most one entry in the package_index table may specify a package of a given name.
	 */
	CpIndex<CONSTANT_Package>[] package_index;


	public ModulePackages(ClassFile resolver, short attributeNameIndex) {
		this.attribute_name_index = resolver.getAttributeNameIndex(attributeNameIndex);
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
		IndexUtility.indexChange(package_index, oldIndex, newIndex);
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		attribute_name_index.writeData(out);
		out.writeInt(attribute_length);
		out.writeShort(package_count);
		for(int i = 0, size = package_index.length; i < size; i++) {
			package_index[i].writeData(out);
		}
	}


	@SuppressWarnings("unchecked")
	@Override
	public void readData(DataInput in) throws IOException {
		if(Settings.readAttributeName) {
			attribute_name_index = ClassFileAttributes.readAttributeNameIndex(in, resolver, ATTRIBUTE_NAME);
		}
		attribute_length = in.readInt();
		package_count = in.readShort();
		package_index = new CpIndex[package_count];
		for(int i = 0, size = package_index.length; i < size; i++) {
			package_index[i] = resolver.getCheckCpIndex(in.readShort(), CONSTANT_Package.class);
		}
	}


	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append(ATTRIBUTE_NAME);

		// TODO implement
		str.append("(packages[");
		for(int i = 0; i < package_count-1; i++) {
			str.append(package_index[i].getCpObject());
			str.append(", ");
		}
		if(package_count > 0) { str.append(package_index[package_count-1].getCpObject()); }
		str.append("])");
		return str.toString();
	}

}
