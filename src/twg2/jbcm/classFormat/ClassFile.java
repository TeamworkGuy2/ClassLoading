package twg2.jbcm.classFormat;

import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.Externalizable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import twg2.jbcm.classFormat.attributes.Attribute_Type;
import twg2.jbcm.classFormat.attributes.BootstrapMethods;
import twg2.jbcm.classFormat.constantPool.CONSTANT_CP_Info;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Class;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Methodref;
import twg2.jbcm.classFormat.constantPool.CONSTANT_NameAndType;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Utf8;
import twg2.jbcm.modify.IndexUtility;
import twg2.jbcm.modify.TypeUtility;

/** Java class file format parent class
 * @author TeamworkGuy2
 * @since 2013-7-7
 */
public class ClassFile implements Externalizable, ReadWritable {
	public static final int HEADER_BYTES = 0xCAFEBABE;
	public static final short ACC_PUBLIC = 0x0001;
	public static final short ACC_FINAL = 0x0010;
	public static final short ACC_SUPER = 0x0020;
	public static final short ACC_INTERFACE = 0x0200;
	public static final short ACC_ABSTRACT = 0x0400;

	int magic; // Must be: 0xCAFEBABE
	short minor_version; // Minor 'm' version value of M.m
	short major_version; // Major 'M' version value of M.m
	// The value of the constant_pool_count item equals the number of entries in the constant_pool
	// table plus one. A constant_pool index is considered valid if it is greater than zero and less
	// than constant_pool_count, with the exception for constants of type long and double noted in §4.4.5
	short constant_pool_count;
	// The constant_pool is a table of structures (§4.4) representing various string constants, class and interface names,
	// field names, and other constants that are referred to within the ClassFile structure and its substructures.
	// The format of each constant_pool table entry is indicated by its first "tag" byte.
	// constant_pool table is indexed from 1 to constant_pool_count-1, size [constant_pool_count-1], indexed from 1.
	List<CpIndex<CONSTANT_CP_Info>> constant_pool;
	// The value of the access_flags item is a mask of flags used to denote access permissions to and properties of
	// this class or interface. The interpretation of each flag, when set, is as shown in Table 4.1.
	/* Flag Name 	Value 	Interpretation
	 * ACC_PUBLIC 	0x0001 	Declared public; may be accessed from outside its package.
	 * ACC_FINAL 	0x0010 	Declared final; no subclasses allowed.
	 * ACC_SUPER 	0x0020 	Treat superclass methods specially when invoked by the invokespecial instruction.
	 * ACC_INTERFACE 	0x0200 	Is an interface, not a class.
	 * ACC_ABSTRACT 	0x0400 	Declared abstract; may not be instantiated. 
	 */
	short access_flags;
	/* Value of the this_class must be a valid index into the constant_pool table.
	 * The constant_pool entry at that index must be a CONSTANT_Class_info (§4.4.1) structure
	 * representing the class or interface defined by this class file.
	 */
	CpIndex<CONSTANT_Class> this_class;
	/* For a class, the value of the super_class item either must be zero or must be a
	 * valid index into the constant_pool table. If the value of the super_class item is nonzero,
	 * the constant_pool entry at that index must be a CONSTANT_Class_info (§4.4.1) structure
	 * representing the direct superclass of the class defined by this class file.
	 * Neither the direct superclass nor any of its superclasses may be a final class.
	 */
	CpIndex<CONSTANT_Class> super_class;
	// The value of the interfaces_count item gives the number of direct superinterfaces of this class or interface type.
	short interfaces_count;
	/* Each value in the interfaces array must be a valid index into the constant_pool table.
	 * The constant_pool entry at each value of interfaces[i], where -1 < i < interfaces_count,
	 * must be a CONSTANT_Class_info (§4.4.1) structure representing an interface that is a
	 * direct superinterface of this class or interface type, in the left-to-right order given
	 * in the source for the type, size [interfaces_count], indexed from 0.
	 */
	// CONSTANT_Class.class
	CpIndex<CONSTANT_Class>[] interfaces;
	/* The value of the fields_count item gives the number of field_info structures in the fields table.
	 * The field_info (§4.5) structures represent all fields, both class variables and instance
	 * variables, declared by this class or interface type.
	 */
	short fields_count;
	/* Each value in the fields table must be a field_info (§4.5) structure giving a complete
	 * description of a field in this class or interface. The fields table includes only
	 * those fields that are declared by this class or interface. It does not include items
	 * representing fields that are inherited from superclasses or superinterfaces,
	 * size [fields_count], indexed from 0.
	 */
	Field_Info[] fields;
	// The value of the methods_count item gives the number of method_info structures in the methods table. 
	short methods_count;
	// Each value in the methods table must be a method_info (§4.6) structure giving a complete description of a method
	// in this class or interface. If the method is not native or abstract, the Java virtual machine instructions
	// implementing the method are also supplied.
	// The method_info structures represent all methods declared by this class or interface type, including instance
	// methods, class (static) methods, instance initialization methods (§3.9), and any class or interface initialization
	// method (§3.9). The methods table does not include items representing methods that are inherited from superclasses
	// or superinterfaces, size [methods_count], indexed from 0.
	Method_Info[] methods;
	// The value of the attributes_count item gives the number of attributes (§4.7) in the attributes table of this class.
	short attributes_count;
	// Each value of the attributes table must be an attribute structure (§4.7).
	// The only attributes defined by this specification as appearing in the attributes table of a ClassFile structure
	// are the SourceFile attribute (§4.7.7) and the Deprecated (§4.7.10) attribute.
	// A Java virtual machine implementation is required to silently ignore any or all attributes in the attributes
	// table of a ClassFile structure that it does not recognize. Attributes not defined in this specification are not
	// allowed to affect the semantics of the class file, but only to provide additional descriptive
	// information (§4.7.1), size [attributes_count], indexed from 0.
	Attribute_Type[] attributes;
	// Optional bootstrap methods reference if it exists
	BootstrapMethods bootstrapMethods;

	// non class fields
	private boolean requireJavaHeaderBytes = true;


	public ClassFile() {
		super();
	}


	public ClassFile(boolean requireJavaMagicBytes) {
		super();
		this.requireJavaHeaderBytes = requireJavaMagicBytes;
	}


	@Override
	public void changeCpIndex(short oldIndex, short newIndex) {
		IndexUtility.indexChange(this_class, oldIndex, newIndex);
		IndexUtility.indexChange(super_class, oldIndex, newIndex);
		IndexUtility.indexChange(interfaces, oldIndex, newIndex);
		for(int i = constant_pool_count-1; i > 0; i--) {
			IndexUtility.indexChange(constant_pool.get(i), oldIndex, newIndex);
		}
		IndexUtility.indexChange(fields, oldIndex, newIndex);
		IndexUtility.indexChange(methods, oldIndex, newIndex);
		IndexUtility.indexChange(attributes, oldIndex, newIndex);
		IndexUtility.indexChange(bootstrapMethods, oldIndex, newIndex);
	}


	public CpIndex<CONSTANT_Class> getClassIndex() {
		return this_class;
	}


	public CpIndex<CONSTANT_Class> getSuperClassIndex() {
		return super_class;
	}


	public int getConstantPoolCount() {
		return constant_pool_count;
	}


	public String getCpString(int attributeNameIndex) {
		CpIndex<CONSTANT_CP_Info> cpItem = constant_pool.get(attributeNameIndex);
		CONSTANT_CP_Info cpInfoObj = cpItem.getCpObject();
		if(!(cpInfoObj instanceof CONSTANT_Utf8)) {
			throw new IllegalStateException("constant pool string index not of type CONSTANT_Utf8");
		}
		String cpName = ((CONSTANT_Utf8)cpInfoObj).getString();
		return cpName;
	}


	public CpIndex<CONSTANT_CP_Info> getCpIndex(int index) {
		if(index > 0 && index < constant_pool_count) {
			CpIndex<CONSTANT_CP_Info> result = (CpIndex<CONSTANT_CP_Info>)constant_pool.get(index);
			return result;
		}
		if(Settings.checkCPIndex) { throw new IllegalStateException("constant pool index " + index + " out of constant pool size bounds"); }
		return null;
	}


	public <T extends CONSTANT_CP_Info> CpIndex<T> getCheckCpIndex(int index, Class<T> clazz) {
		if(index > 0 && index < constant_pool_count) {
			@SuppressWarnings("unchecked")
			CpIndex<T> result = (CpIndex<T>)constant_pool.get(index);
			if(!result.isInitialized()) {
				result.initialize(clazz);
			}
			else {
				checkCpIndex(result, clazz);
			}
			return result;
		}
		if(Settings.checkCPIndex) { throw new IllegalStateException("constant pool index " + index + " out of constant pool size bounds"); }
		return null;
	}


	public <T extends CONSTANT_CP_Info> CpIndex<T> checkCpIndex(CpIndex<T> item, Class<? extends CONSTANT_CP_Info> clazz) {
		if(!clazz.isInstance(item.getCpObject())) {
			throw new ClassCastException("constant pool item " + item + " does not match check type " + clazz);
		}
		return item;
	}


	/** Find the index of a specific string in the constant pool
	 * @param str the string to search for in the constant pool
	 * @return the index of the matching string in the constant pool, or -1 if the string is not found
	 */
	public CpIndex<CONSTANT_Utf8> findConstantPoolString(String str) {
		for(int i = 1; i < constant_pool_count; i++) {
			CpIndex<CONSTANT_CP_Info> cpItem = constant_pool.get(i);
			CONSTANT_CP_Info cpe = cpItem.getCpObject();
			if(cpe instanceof CONSTANT_Utf8) {
				if(((CONSTANT_Utf8)cpe).getString().equals(str)) {
					@SuppressWarnings({ "unchecked", "rawtypes" })
					CpIndex<CONSTANT_Utf8> result = (CpIndex<CONSTANT_Utf8>)(CpIndex)cpItem;
					return result;
				}
			}
		}
		return null;
	}


	public CpIndex<CONSTANT_Methodref> findConstantPoolMethod(Method method) {
		String className = TypeUtility.classNameInternal(method.getDeclaringClass());
		String methodName = method.getName();
		String methodDes = TypeUtility.methodDescriptor(method);
		for(int i = 1; i < constant_pool_count; i++) {
			CpIndex<CONSTANT_CP_Info> cpItem = constant_pool.get(i);
			CONSTANT_CP_Info cpe = cpItem.getCpObject();
			if(cpe instanceof CONSTANT_Methodref) {
				CONSTANT_Methodref cpm = ((CONSTANT_Methodref)cpe);
				if(cpm.getClassType().getName().getString().equals(className)
						&& cpm.getNameAndType().getName().getString().equals(methodName)
						&& cpm.getNameAndType().getDescriptor().getString().equals(methodDes)) {
					@SuppressWarnings({ "unchecked", "rawtypes" })
					CpIndex<CONSTANT_Methodref> result = (CpIndex<CONSTANT_Methodref>)(CpIndex)cpItem;
					return result;
				}
			}
		}
		return null;
	}


	/** Get the constant pool {@link CONSTANT_NameAndType} matching the method specified
	 * @param method the method to search for in this class' constant pool
	 * @return the {@link CONSTANT_NameAndType} matching the method specified, otherwise null
	 */
	public CpIndex<CONSTANT_NameAndType> findConstantPoolNameAndType(Method method) {
		String methodName = method.getName();
		String methodDes = TypeUtility.methodDescriptor(method);
		return findConstantPoolNameAndType(methodName, methodDes);
	}


	/** Get the constant pool {@link CONSTANT_NameAndType} matching the formatted method name
	 * and method descriptor specified.
	 * @param methodName the name of the method, for example {@code "main"}
	 * @param methodDescriptor the method's descriptor, for example {@code "()V"} for a parameterless, void return method.
	 * @return the {@link CONSTANT_NameAndType} matching the name and descriptor specified, otherwise null
	 * @see TypeUtility#methodDescriptor(Method)
	 * @see Method#getName()
	 */
	public CpIndex<CONSTANT_NameAndType> findConstantPoolNameAndType(String methodName, String methodDescriptor) {
		for(int i = 1; i < constant_pool_count; i++) {
			CpIndex<CONSTANT_CP_Info> cpItem = constant_pool.get(i);
			CONSTANT_CP_Info cpe = cpItem.getCpObject();
			if(cpe instanceof CONSTANT_NameAndType) {
				CONSTANT_NameAndType cpnt = ((CONSTANT_NameAndType)cpe);
				if(cpnt.getName().getString().equals(methodName)
						&& cpnt.getDescriptor().getString().equals(methodDescriptor)) {
					@SuppressWarnings({ "unchecked", "rawtypes" })
					CpIndex<CONSTANT_NameAndType> result = (CpIndex<CONSTANT_NameAndType>)(CpIndex)cpItem;
					return result;
				}
			}
		}
		return null;
	}


	public CpIndex<CONSTANT_Class> findConstantPoolClass(Class<?> clas) {
		String className = TypeUtility.classNameInternal(clas);
		for(int i = 1; i < constant_pool_count; i++) {
			CpIndex<CONSTANT_CP_Info> cpItem = constant_pool.get(i);
			CONSTANT_CP_Info cpe = cpItem.getCpObject();
			if(cpe instanceof CONSTANT_Class) {
				CONSTANT_Class cpc = ((CONSTANT_Class)cpe);
				if(cpc.getName().getString().equals(className)) {
					@SuppressWarnings({ "unchecked", "rawtypes" })
					CpIndex<CONSTANT_Class> result = (CpIndex<CONSTANT_Class>)(CpIndex)cpItem;
					return result;
				}
			}
		}
		return null;
	}


	/** Add the specified object to this class file's constant pool
	 * @param cpObj the constant pool object to add
	 * @return the index of the added constant pool object
	 */
	public <T extends CONSTANT_CP_Info> CpIndex<T> addToConstantPool(T cpObj) {
		@SuppressWarnings("unchecked")
		Class<T> cpClass = (Class<T>)cpObj.getClass();
		CpIndex<T> cpItem = new CpIndex<T>(cpObj, cpClass, constant_pool_count);
		@SuppressWarnings("unchecked")
		CpIndex<CONSTANT_CP_Info> cpItemT = (CpIndex<CONSTANT_CP_Info>)cpItem;
		constant_pool.add(cpItemT);
		constant_pool_count++;
		return cpItem;
	}


	public void setConstantPool(int index, CONSTANT_CP_Info cpObj) {
		if(index < 1 || index >= constant_pool_count) { throw new IndexOutOfBoundsException("Illegal class file constant pool index: " + index); }
		constant_pool.get(index).setCpObject(cpObj);
	}


	public void swapConstantPoolIndices(int index1, int index2) {
		// Swap the constant_pool items
		CONSTANT_CP_Info item1 = constant_pool.get(index1).getCpObject();
		CONSTANT_CP_Info item2 = constant_pool.get(index2).getCpObject();
		setConstantPool(index1, item2);
		setConstantPool(index2, item1);
		// Swap the indices of all objects that refer to these two constant pool objects
		short tempIndex = (short)(constant_pool_count + 1);
		changeCpIndex((short)index1, tempIndex);
		changeCpIndex((short)index2, (short)index1);
		changeCpIndex(tempIndex, (short)index2);
	}


	/** Swap two constant pool indices and update the associated offsets and index pointers
	 * @param offset the offset into each method's instructions at which to start searching and replace {@code index1} with {@code index2}
	 * @param index1 the first index to swap
	 * @param index2 the second index to swap
	 */
	public void swapCodeCpIndices(int offset, int index1, int index2) {
		int tempIndex = (constant_pool_count + 1);
		for(Method_Info method : methods) {
			if(method.getCode() != null) {
				byte[] instructions = method.getCode().getCode();
				TypeUtility.changeCpIndices(instructions, offset, index1, tempIndex);
				TypeUtility.changeCpIndices(instructions, offset, index2, index1);
				TypeUtility.changeCpIndices(instructions, offset, tempIndex, index2);
			}
		}
	}


	public CONSTANT_Class getInterface(int index) {
		CONSTANT_CP_Info interfaceClass = interfaces[index].getCpObject();
		if(!(interfaceClass instanceof CONSTANT_Class)) {
			throw new IllegalStateException("Constant pool interface index: " + interfaces[index] + " not of type CONSTANT_Class");
		}
		return (CONSTANT_Class)interfaceClass;
	}


	public void addInterface(CpIndex<CONSTANT_Class> interfaceClassIndex) {
		CONSTANT_CP_Info interfaceClass = interfaceClassIndex.getCpObject();
		if(!(interfaceClass instanceof CONSTANT_Class)) {
			throw new IllegalStateException("Constant pool interface index: " + interfaceClassIndex + " is not of type CONSTANT_Class");
		}
		CpIndex<CONSTANT_Class>[] interfacesOld = interfaces;
		interfaces = new CpIndex[interfacesOld.length + 1];
		System.arraycopy(interfacesOld, 0, interfaces, 0, interfacesOld.length);
		interfaces[interfacesOld.length] = interfaceClassIndex;
		interfaces_count++;
	}


	public int getInterfaceCount() {
		return interfaces_count;
	}


	public Field_Info getField(int index) {
		return fields[index];
	}


	public int getFieldCount() {
		return fields_count;
	}


	public Method_Info getMethod(int index) {
		return methods[index];
	}


	public int getMethodCount() {
		return methods_count;
	}


	public Attribute_Type getAttribute(int index) {
		return attributes[index];
	}


	public int getAttributeCount() {
		return attributes_count;
	}


	public void rename(String className) {
		CONSTANT_Class thisClass = this_class.getCpObject();
		String oldName = thisClass.getName().getString();
		String str = null;
		thisClass.getName().setString(className);
		CONSTANT_Utf8 cpStr = null;
		for(int i = 1; i < constant_pool_count; i++) {
			if(constant_pool.get(i).getCpObject() instanceof CONSTANT_Utf8) {
				cpStr = (CONSTANT_Utf8)constant_pool.get(i).getCpObject();
				str = cpStr.getString();
				if(str.contains(oldName)) {
					cpStr.setString(str.replace(oldName, className));
				}
			}
		}
	}


	public BootstrapMethods getBootstrapMethods() {
		return bootstrapMethods;
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		out.writeInt(HEADER_BYTES);
		out.writeShort(minor_version);
		out.writeShort(major_version);
		out.writeShort(constant_pool_count);
		// Constant pool starts at 1
		for(int i = 1; i < constant_pool_count; i++) {
			constant_pool.get(i).getCpObject().writeData(out);
		}
		out.writeShort(access_flags);
		this_class.writeData(out);
		super_class.writeData(out);
		out.writeShort(interfaces_count);
		for(int i = 0; i < interfaces_count; i++) {
			interfaces[i].writeData(out);
		}
		out.writeShort(fields_count);
		for(int i = 0; i < fields_count; i++) {
			fields[i].writeData(out);
		}
		out.writeShort(methods_count);
		for(int i = 0; i < methods_count; i++) {
			methods[i].writeData(out);
		}
		out.writeShort(attributes_count);
		for(int i = 0; i < attributes_count; i++) {
			attributes[i].writeData(out);
		}
	}


	@Override
	public void readData(DataInput in) throws IOException {
		magic = in.readInt();
		if(magic != HEADER_BYTES && requireJavaHeaderBytes) {
			throw new IllegalStateException("Header bytes do not match " + HEADER_BYTES + ": " + magic);
		}
		minor_version = in.readShort();
		major_version = in.readShort();
		constant_pool_count = in.readShort();
		constant_pool = new ArrayList<CpIndex<CONSTANT_CP_Info>>();
		// The special index 0 of the constant pool
		constant_pool.add(new CpIndex<CONSTANT_CP_Info>(CONSTANT_CP_Info.class, 0));
		// initialize the values in the constant pool
		for(int i = 1; i < constant_pool_count; i++) {
			constant_pool.add(new CpIndex<CONSTANT_CP_Info>(i));
		}
		if(Settings.debug) { System.out.println("Class file version: " + major_version + "." + minor_version); }
		if(Settings.debug) { System.out.println("Constant pool size: " + constant_pool_count); }
		// Constant pool starts at 1
		for(int i = 1; i < constant_pool_count; i++) {
			constant_pool.get(i).setCpObject( Settings.loadConstantPoolObject(in, this) );
		}
		access_flags = in.readShort();
		this_class = this.getCheckCpIndex(in.readShort(), CONSTANT_Class.class);
		super_class = this.getCheckCpIndex(in.readShort(), CONSTANT_Class.class);
		interfaces_count = in.readShort();
		interfaces = new CpIndex[interfaces_count];
		for(int i = 0; i < interfaces_count; i++) {
			interfaces[i] = this.getCheckCpIndex(in.readShort(), CONSTANT_Class.class);
		}
		fields_count = in.readShort();
		fields = new Field_Info[fields_count];
		for(int i = 0; i < fields_count; i++) {
			fields[i] = new Field_Info(this);
			fields[i].readData(in);
		}
		methods_count = in.readShort();
		methods = new Method_Info[methods_count];
		for(int i = 0; i < methods_count; i++) {
			methods[i] = new Method_Info(this);
			methods[i].readData(in);
		}
		attributes_count = in.readShort();
		attributes = new Attribute_Type[attributes_count];
		for(int i = 0; i < attributes_count; i++) {
			attributes[i] = Settings.loadAttributeObject(in, this, null);
			// Loop bootstrap methods attribute
			if(attributes[i].getClass() == BootstrapMethods.class) {
				this.bootstrapMethods = (BootstrapMethods)attributes[i];
			}
		}
	}


	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		writeData(out);
	}


	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		readData(in);
	}


	public void print(PrintStream stream) {
		stream.println("Java ClassFile " + major_version + "." + minor_version);
		stream.println("Constants: " + constant_pool_count);
		for(int i = 1; i < constant_pool_count; i++) {
			stream.println("Constant " + i + ": " + constant_pool.get(i).getCpObject().toString());
		}
		stream.println();
		stream.println("This class: " + this_class.getCpObject() + ", access=" + Integer.toHexString(access_flags));
		stream.println("Super class: " + super_class.getCpObject());
		stream.println();
		stream.println("Interfaces: " + interfaces_count);
		for(int i = 0; i < interfaces_count; i++) {
			stream.println(interfaces[i].getCpObject());
		}
		stream.println();
		stream.println("Fields: " + fields_count);
		for(int i = 0; i < fields_count; i++) {
			stream.println(fields[i].toString());
		}
		stream.println();
		stream.println("Methods: " + methods_count);
		for(int i = 0; i < methods_count; i++) {
			stream.println(methods[i].toString());
		}
		stream.println();
		stream.println("Attributes: " + attributes_count);
		for(int i = 0; i < attributes_count; i++) {
			stream.println(attributes[i].toString());
		}
		stream.println();
	}


	/** Create a {@link ClassFile} from the specified file
	 * @param file the file to load
	 * @return the class file loaded from the specified file
	 * @throws IOException if the file cannot be opened or if it is not a recognized class file format
	 */
	public static final ClassFile load(File file) throws IOException {
		ClassFile classFile = new ClassFile();
		DataInputStream in = null;
		try {
			in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
			classFile.readData(in);
		} finally {
			if(in != null) {
				in.close();
			}
		}
		return classFile;
	}


	/** Create a {@link ClassFile} from the specified class. The class is loaded using this thread's context classloader.
	 * @param clazz the class to load
	 * @return the class file loaded from the specified class' class file
	 * @throws IOException if the class' class file could not be found or opened or if the class file found
	 * is not a recognized class file format
	 */
	public static final ClassFile load(Class<?> clazz) throws IOException {
		return load(clazz, true);
	}


	/** Create a {@link ClassFile} from the specified class. The class is loaded using this thread's context classloader.
	 * @param clazz the class to load
	 * @param requireJavaMagicBytes true to require java magic bytes 'CAFEBABE' at the beginning of the java file,
	 * false to read any 4 bytes at the beginning of the file
	 * @return the class file loaded from the specified class' class file
	 * @throws IOException if the class' class file could not be found or opened or if the class file found
	 * is not a recognized class file format
	 */
	public static final ClassFile load(Class<?> clazz, boolean requireJavaMagicBytes) throws IOException {
		ClassFile classFile = new ClassFile(requireJavaMagicBytes);
		String filesystemClassName = clazz.getCanonicalName().replace('.', '/') + ".class";
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filesystemClassName);
		DataInputStream in = null;
		try {
			in = new DataInputStream(new BufferedInputStream(inputStream));
			classFile.readData(in);
		} finally {
			if(in != null) {
				in.close();
			}
		}
		return classFile;
	}

}
