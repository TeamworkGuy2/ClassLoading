package twg2.jbcm.classFormat.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.ConstantPoolAllowZero;
import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.ReadWritable;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Utf8;
import twg2.jbcm.modify.IndexUtility;

/** A member of {@link MethodParameters}.
 * @author TeamworkGuy2
 * @since 2014-3-19
 */
public class Parameter_Entry implements ReadWritable {
	ClassFile resolver;
	/** The value of the name_index item must either be zero or a valid index into the constant_pool table.<br/>
	 * If the value of the name_index item is zero, then this parameters element indicates a formal
	 * parameter with no name.<br/>
	 * If the value of the name_index item is nonzero, the constant_pool entry at that index must be
	 * a CONSTANT_Utf8_info structure representing a valid unqualified name denoting a formal parameter (§4.2.2).
	 */
	// Can be zero (0)
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
		name_index = resolver.getCheckCpIndex(in.readShort(), CONSTANT_Utf8.class);
		access_flags = in.readShort();
	}


	@Override
	public String toString() {
		return "parameter(name=" + name_index.getCpObject().getString() + ", access=" + access_flags + ")";
	}

}
