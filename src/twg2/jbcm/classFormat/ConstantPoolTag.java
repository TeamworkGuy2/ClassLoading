package twg2.jbcm.classFormat;

import java.io.DataInput;
import java.io.IOException;

import twg2.jbcm.classFormat.constantPool.CONSTANT_CP_Info;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Class;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Double;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Fieldref;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Float;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Integer;
import twg2.jbcm.classFormat.constantPool.CONSTANT_InterfaceMethodref;
import twg2.jbcm.classFormat.constantPool.CONSTANT_InvokeDynamic;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Long;
import twg2.jbcm.classFormat.constantPool.CONSTANT_MethodHandle;
import twg2.jbcm.classFormat.constantPool.CONSTANT_MethodType;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Methodref;
import twg2.jbcm.classFormat.constantPool.CONSTANT_NameAndType;
import twg2.jbcm.classFormat.constantPool.CONSTANT_String;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Utf8;

/** A representation of all the Constant Pool object types in Java
 * @author TeamworkGuy2
 * @since 2014-1-31
 */
public enum ConstantPoolTag {
	CLASS				((byte)7)  { @Override public CONSTANT_Class create(ClassFile clazz) { return new CONSTANT_Class(clazz); } },
	FIELD_REF			((byte)9)  { @Override public CONSTANT_Fieldref create(ClassFile clazz) { return new CONSTANT_Fieldref(clazz); } },
	METHOD_REF			((byte)10) { @Override public CONSTANT_Methodref create(ClassFile clazz) { return new CONSTANT_Methodref(clazz); } },
	INTERFACE_METHOD_REF((byte)11) { @Override public CONSTANT_InterfaceMethodref create(ClassFile clazz) { return new CONSTANT_InterfaceMethodref(clazz); } },
	STRING				((byte)8)  { @Override public CONSTANT_String create(ClassFile clazz) { return new CONSTANT_String(clazz); } },
	INTEGER				((byte)3)  { @Override public CONSTANT_Integer create(ClassFile clazz) { return new CONSTANT_Integer(clazz); } },
	FLOAT				((byte)4)  { @Override public CONSTANT_Float create(ClassFile clazz) { return new CONSTANT_Float(clazz); } },
	LONG				((byte)5)  { @Override public CONSTANT_Long create(ClassFile clazz) { return new CONSTANT_Long(clazz); } },
	DOUBLE				((byte)6)  { @Override public CONSTANT_Double create(ClassFile clazz) { return new CONSTANT_Double(clazz); } },
	NAME_AND_TYPE		((byte)12) { @Override public CONSTANT_NameAndType create(ClassFile clazz) { return new CONSTANT_NameAndType(clazz); } },
	UTF8				((byte)1)  { @Override public CONSTANT_Utf8 create(ClassFile clazz) { return new CONSTANT_Utf8(clazz); } },
	METHOD_HANDLE		((byte)15) { @Override public CONSTANT_MethodHandle create(ClassFile clazz) { return new CONSTANT_MethodHandle(clazz); } },
	METHOD_TYPE			((byte)16) { @Override public CONSTANT_MethodType create(ClassFile clazz) { return new CONSTANT_MethodType(clazz); } },
	INVOKE_DYNAMIC		((byte)18) { @Override public CONSTANT_InvokeDynamic create(ClassFile clazz) { return new CONSTANT_InvokeDynamic(clazz); } };

	private static final ConstantPoolTag[] tagArray;

	private byte tag;


	static {
		ConstantPoolTag[] tags = ConstantPoolTag.values();
		int max = 0;

		for(ConstantPoolTag tag : tags) {
			int cpTag = tag.getTag();
			if(cpTag > max) { max = cpTag; }
		}

		// store the tag enum values in an array based on tag number
		tagArray = new ConstantPoolTag[max + 1];
		// Fill the array with the tags
		for(ConstantPoolTag tag : tags) {
			tagArray[tag.getTag()] = tag;
		}
	}


	ConstantPoolTag(byte tag) {
		this.tag = tag;
	}


	/** Create an uninitialized instance of the specific Constant Pool object type
	 * @param clazz the ClassFile instance that the Constant Pool object to create is inside of
	 * @return the newly created Constant Pool object based on the specified ClassFile
	 */
	public abstract CONSTANT_CP_Info create(ClassFile clazz);


	/** Check if this Constant Pool object type's tag is the same as the specified tag
	 * @param tagByte the tag to check against this object type's tag
	 * @return true if this object type's tag is the same as the specified tag, otherwise false
	 */
	public boolean isTag(int tagByte) {
		return this.tag == tagByte;
	}


	/**
	 * @return this object type's tag
	 */
	public int getTag() {
		return tag;
	}


	/** Java class file format <code>ConstantPool</code> item parser/loader.
	 * Uses this enum's tag values and {@link #create(ClassFile)} methods to parse constant pool
	 * entries from a {@link DataInput} stream.
	 */
	public static CONSTANT_CP_Info loadConstantPoolObject(DataInput in, int cpIndex, ClassFile resolver) throws IOException {
		CONSTANT_CP_Info cpObj = null;
		int tag = in.readByte() & 0xFF;

		try {
			cpObj = tagArray[tag].create(resolver);
		} catch(RuntimeException e) {
			if(Settings.checkCPTag) {
				throw new IllegalArgumentException("Unknown constant pool tag: " + tag);
			}
			else {
				// all constant pool entries will have a tag followed by at least 2 bytes of data, so even if invalid, read 2 bytes
				// https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.4
				int unknownData1 = cpObj == null ? in.readByte() : 0;
				int unknownData2 = cpObj == null ? in.readByte() : 0;
				System.err.println("Unknown constant pool tag: " + tag + " at index " + cpIndex + ", attempting to read required 2 byte info: [" + unknownData1 + ", " + unknownData2 + "]");
				return null;
			}
		}

		cpObj.readData(in);

		return cpObj;
	}

}
