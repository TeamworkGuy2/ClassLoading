package twg2.jbcm.classFormat.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.Settings;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Utf8;
import twg2.jbcm.modify.IndexUtility;

/** A Java class file format Attribute of type <code>SourceFile</code>
 * @author TeamworkGuy2
 * @since 2013-7-7
 */
public class SourceFile implements Attribute_Type {
	public static final String ATTRIBUTE_NAME = "SourceFile";
	ClassFile resolver;
	/* The value of the attribute_name_index item must be a valid index into the constant_pool table. The constant_pool
	 * entry at that index must be a CONSTANT_Utf8_info (§4.4.7) structure representing the string "SourceFile".
	 */
	CpIndex<CONSTANT_Utf8> attribute_name_index;
	// The value of the attribute_length item of a SourceFile_attribute structure must be 2.
	int attribute_length;
	/* The value of the sourcefile_index item must be a valid index into the constant_pool table. The constant pool
	 * entry at that index must be a CONSTANT_Utf8_info (§4.4.7) structure representing a string.
	 * The string referenced by the sourcefile_index item will be interpreted as indicating the name of the source
	 * file from which this class file was compiled. It will not be interpreted as indicating the name of a directory
	 * containing the file or an absolute path name for the file; such platform-specific additional information
	 * must be supplied by the runtime interpreter or development tool at the time the file name is actually used.
	 */
	CpIndex<CONSTANT_Utf8> sourcefile_index;


	public SourceFile(ClassFile resolver, short attributeNameIndex) {
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
		IndexUtility.indexChange(sourcefile_index, oldIndex, newIndex);
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		attribute_name_index.writeData(out);
		out.writeInt(attribute_length);
		sourcefile_index.writeData(out);
	}


	@Override
	public void readData(DataInput in) throws IOException {
		if(Settings.doReadAttributeName()) {
			attribute_name_index = Settings.readAttributeNameIndex(in, resolver, ATTRIBUTE_NAME);
		}
		attribute_length = in.readInt();
		sourcefile_index = resolver.getCheckCpIndex(in.readShort(), CONSTANT_Utf8.class);
	}


	@Override
	public String toString() {
		return ATTRIBUTE_NAME + "(name=" + sourcefile_index.getCpObject().getString() + ")";
	}

}
