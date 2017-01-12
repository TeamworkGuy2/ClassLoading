package twg2.jbcm.classFormat.constantPool;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.Settings;
import twg2.jbcm.modify.IndexUtility;

/** Java class file format constant pool <code>MethodHandle</code> info type
 * @author TeamworkGuy2
 * @since 2013-10-6
 */
public class CONSTANT_MethodHandle implements CONSTANT_CP_Info {
	public static final int CONSTANT_MethodHandle_info = 15;
	ClassFile resolver;

	byte tag = CONSTANT_MethodHandle_info;

	/* The value of the reference_kind item must be in the range 1 to 9. The value denotes the
	 * kind of this method handle, which characterizes its bytecode behavior (§5.4.3.5).
	 * §5.4.3.5. Method Type and Method Handle Resolution:
	 * Kind 	Description 	Interpretation
	 * 1 	REF_getField 	getfield C.f:T
	 * 2 	REF_getStatic 	getstatic C.f:T
	 * 3 	REF_putField 	putfield C.f:T
	 * 4 	REF_putStatic 	putstatic C.f:T
	 * 5 	REF_invokeVirtual 	invokevirtual C.m:(A*)T
	 * 6 	REF_invokeStatic 	invokestatic C.m:(A*)T
	 * 7 	REF_invokeSpecial 	invokespecial C.m:(A*)T
	 * 8 	REF_newInvokeSpecial 	new C; dup; invokespecial C.<init>:(A*)void
	 * 9 	REF_invokeInterface 	invokeinterface C.m:(A*)T
	 */
	byte reference_kind;
	// One of: CONSTANT_Fieldref.class, CONSTANT_Methodref.class, CONSTANT_InterfaceMethodref.class
	Class<? extends CONSTANT_CP_Info> referenceClass;
	/* The value of the reference_index item must be a valid index into the constant_pool table.
	 * If the value of the reference_kind item is 1 (REF_getField), 2 (REF_getStatic), 3 (REF_putField),
	 * or 4 (REF_putStatic), then the constant_pool entry at that index must be a CONSTANT_Fieldref_info (§4.4.2)
	 * structure representing a field for which a method handle is to be created.
	 * If the value of the reference_kind item is 5 (REF_invokeVirtual), 6 (REF_invokeStatic), 7 (REF_invokeSpecial),
	 * or 8 (REF_newInvokeSpecial), then the constant_pool entry at that index must be a CONSTANT_Methodref_info
	 * structure (§4.4.2) representing a class's method or constructor (§2.9) for which a method handle is to be
	 * created.
	 * If the value of the reference_kind item is 9 (REF_invokeInterface), then the constant_pool entry at that
	 * index must be a CONSTANT_InterfaceMethodref_info (§4.4.2) structure representing an interface's method for
	 * which a method handle is to be created.
	 * If the value of the reference_kind item is 5 (REF_invokeVirtual), 6 (REF_invokeStatic), 7 (REF_invokeSpecial),
	 * or 9 (REF_invokeInterface), the name of the method represented by a CONSTANT_Methodref_info structure must
	 * not be <init> or <clinit>.
	 * If the value is 8 (REF_newInvokeSpecial), the name of the method represented by a CONSTANT_Methodref_info
	 * structure must be <init>. 
	 */
	CpIndex<? extends CONSTANT_CP_Info> reference_index;


	public CONSTANT_MethodHandle(ClassFile resolver) {
		this.resolver = resolver;
	}


	@Override
	public int getTag() {
		return tag;
	}


	@Override
	public void changeCpIndex(short oldIndex, short newIndex) {
		IndexUtility.indexChange(reference_index, oldIndex, newIndex);
	}


	public int getReferenceKind() {
		return reference_kind;
	}


	public CONSTANT_CP_Info getReference() {
		return reference_index.getCpObject();
	}


	public void setReferenceKind(int referenceKind) {
		if(referenceClass == null) {
			throw new IllegalArgumentException(referenceKind + " is not a valid MethodHandle reference kind");
		}
		this.reference_kind = (byte)referenceKind;
	}


	public void setReferenceIndex(CpIndex<? extends CONSTANT_CP_Info> index) {
		resolver.checkCpIndex(index, referenceClass);
		this.reference_index = index;
	}


	/** Convert a method handle reference kind to its corresponding class type
	 * @param referenceKind the reference kind value between 1 and 9
	 * @return {@link CONSTANT_Fieldref} if reference kind is 1, 2, 3, or 4.
	 * {@link CONSTANT_Methodref} if reference kind is 5, 6, 7, or 8.
	 * {@link CONSTANT_InterfaceMethodref} if reference kind is 9.
	 */
	private Class<? extends CONSTANT_CP_Info> referenceKindType(int referenceKind) {
		switch(referenceKind) {
		case 1:
		case 2:
		case 3:
		case 4:
			return CONSTANT_Fieldref.class;
		case 5:
		case 6:
		case 7:
		case 8:
			return CONSTANT_Methodref.class;
		case 9:
			return CONSTANT_InterfaceMethodref.class;
		default:
			return null;
		}
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		out.write(CONSTANT_MethodHandle_info);
		out.write(reference_kind);
		reference_index.writeData(out);
	}


	@Override
	public void readData(DataInput in) throws IOException {
		if(!Settings.cpTagRead) {
			int tagV = in.readByte();
			if(tagV != CONSTANT_MethodHandle_info) { throw new IllegalStateException("illegal CONSTANT_MethodHandle tag: " + tagV); }
		}
		reference_kind = in.readByte();
		referenceClass = referenceKindType(reference_kind);
		reference_index = resolver.getCheckCpIndex(in.readShort(), referenceClass);
	}


	@Override
	public String toString() {
		return "MethodHandle(15, type=" + referenceClass.getSimpleName() + ", reference_index=" + reference_index.getCpObject() + ")";
	}

}
