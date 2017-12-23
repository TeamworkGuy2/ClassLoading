package twg2.jbcm.classFormat.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.ReadWritable;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Class;
import twg2.jbcm.modify.IndexUtility;

/** A Java class file format Attribute of type <code>VerificationTypeInfo</code> for a {@link StackMapTable.StackMapFrame}
 * @author TeamworkGuy2
 * @since 2013-10-6
 */
public class VerificationTypeInfo implements ReadWritable {
	ClassFile resolver;
	/** The verification type info:
	 * 0 = TOP
	 * 1 = INTEGER
	 * 2 = FLOAT
	 * 3 = DOUBLE
	 * 4 = LONG
	 * 5 = NULL
	 * 6 = UNINITIALIZED_THIS
	 * 7 = OBJECT
	 * 8 = UNINITIALIZED
	 */
	byte tag;
	/** Used by the OBJECT verification type
	 * The Object_variable_info item indicates that the location has the verification type which is the class represented by
	 * the CONSTANT_Class_info structure (§4.4.1) found in the constant_pool table at the index given by cpool_index.
	 */ 
	CpIndex<CONSTANT_Class> cpool_index;
	/** Used by the UNINITIALIZED verification type
	 * The Uninitialized_variable_info item indicates that the location has the verification type uninitialized(Offset).
	 * The Offset item indicates the offset, in the code array of the Code attribute that contains this StackMapTable
	 * attribute, of the new instruction (§new) that created the object being stored in the location.
	 */
	short offset;


	public VerificationTypeInfo(ClassFile resolver) {
		this.resolver = resolver;
	}


	/** Create a StackMapFrame verification type with the specified tag and value
	 * @param resolver the class file resolver to resolve constant pool indices against
	 * @param tag the verification type tag (between 0 and 8)
	 * @param value if the <code>tag</code> parameter is 7, this value is assigned
	 * to created <code>Object</code> verification type's <code>cpool_index</code>.<br/>
	 * If the <code>tag</code> parameter is 8, this value is assigned to the created
	 * <code>Uninitialized</code> verification type's <code>offset</code> value.
	 */
	public VerificationTypeInfo(ClassFile resolver, byte tag, short value) {
		this.resolver = resolver;
		this.tag = tag;
		if(tag == 7) {
			cpool_index = resolver.getCheckCpIndex(value, CONSTANT_Class.class);
		}
		if(tag == 8) {
			offset = value;
		}
	}


	@Override
	public void changeCpIndex(short oldIndex, short newIndex) {
		if(tag == 7) {
			IndexUtility.indexChange(cpool_index, oldIndex, newIndex);
		}
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		out.writeByte(tag);
		switch(tag) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
			// Write nothing, these verification types have no other data
			break;
		case 7:
			cpool_index.writeData(out);
			break;
		case 8:
			out.writeShort(offset);
			break;
		default:
			throw new IllegalStateException("invalid StackMapFrame VerificationType of " + tag + ", cannot write");
		}
	}


	@Override
	public void readData(DataInput in) throws IOException {
		tag = in.readByte();
		switch(tag) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
			// Read nothing, these verification types have no other data
			break;
		case 7:
			cpool_index = resolver.getCheckCpIndex(in.readShort(), CONSTANT_Class.class);
			break;
		case 8:
			offset = in.readShort();
			break;
		default:
			throw new IllegalStateException("invalid StackMapFrame VerificationType of " + tag + ", cannot read");
		}
	}


	@Override
	public String toString() {
		return "VerificationType(" + tagName(tag) + ((tag == 7 || tag == 8) ? ((tag == 7) ? (", index=" + cpool_index) : (", offset=" + offset)) : "") + ")";
	}


	private static final String tagName(int tag) {
		switch(tag) {
		case 0:
			return "top";
		case 1:
			return "integer";
		case 2:
			return "float";
		case 3:
			return "double";
		case 4:
			return "long";
		case 5:
			return "null";
		case 6:
			return "uninitialized_this";
		case 7:
			return "object";
		case 8:
			return "uninitialized";
		default:
			throw new IllegalStateException("invalid StackMapFrame VerificationType of " + tag + ", cannot print");
		}
	}

}
