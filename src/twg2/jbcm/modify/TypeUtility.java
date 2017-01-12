package twg2.jbcm.modify;

import java.lang.reflect.Method;

import twg2.jbcm.IoUtility;
import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.ConstantPoolTag;
import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.attributes.Code;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Class;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Methodref;
import twg2.jbcm.classFormat.constantPool.CONSTANT_NameAndType;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Utf8;

/**
 * @author TeamworkGuy2
 * @since 2014-4-19
 */
public class TypeUtility {


	/** Read through a byte array of instructions and update class constant pool indices matching the oldIndex to use the newIndex
	 * @param instructions the raw method instructions to process
	 * @param offset the offset into {@code instructions} at which to start processing instructions
	 * @param oldIndex the old class constant pool index to replace
	 * @param newIndex the new class constant pool index
	 */
	public static final void changeCpIndices(byte[] instructions, final int offset, final int oldIndex, final int newIndex) {
		IoUtility.forEach(instructions, offset, instructions.length - offset, (opcode, code, location) -> {
			if(opcode.getOperations().getCpIndexModifier() != null) {
				opcode.getOperations().getCpIndexModifier().changeCpIndexIf(code, location, oldIndex, newIndex);
			}
		});
	}


	/**
	 * @param code
	 * @param methodCp
	 * @param method
	 * @return the number of bytes added to the code's instructions
	 */
	public static final int addMethodCall(Code code, final CpIndex<CONSTANT_Methodref> methodCp, Method method) {
		int methodIndex = methodCp.getIndex();
		byte[] instructions = new byte[] {
				(byte)184, // InvokeStatic
				(byte)((methodIndex >>> 8) & 0xFF),
				(byte)(methodIndex & 0xFF),
				//(byte)0, // NOP
		};
		code.prependCode(instructions);
		return instructions.length;
	}


	/** Add a Methodref of the specified method to the specified class file.
	 * This ensures that the appropriate class, method, and method definitions
	 * are present in the class and inserts a properly linked {@link CONSTANT_Methodref}
	 * into the class file's constant pool.
	 * @param classFile the class file object to add the method reference to
	 * @param method the method reference to add to the class file object
	 * @return the index of the inserted method reference in the class file's constant pool
	 */
	public static final CpIndex<CONSTANT_Methodref> addMethodToConstantPool(ClassFile classFile, Method method) {
		String className = TypeUtility.classNameInternal(method.getDeclaringClass());
		String methodName = method.getName();
		String methodDes = TypeUtility.methodDescriptor(method);
		CpIndex<CONSTANT_Utf8> classNameIndex = classFile.findConstantPoolString(className);
		CpIndex<CONSTANT_Utf8> methodNameIndex = classFile.findConstantPoolString(methodName);
		CpIndex<CONSTANT_Utf8> methodDesIndex = classFile.findConstantPoolString(methodDes);

		if(classNameIndex == null) {
			classNameIndex = addString(classFile, className);
		}
		if(methodNameIndex == null) {
			methodNameIndex = addString(classFile, methodName);
		}
		if(methodDesIndex == null) {
			methodDesIndex = addString(classFile, methodDes);
		}

		CpIndex<CONSTANT_Class> classIndex = classFile.findConstantPoolClass(method.getDeclaringClass());
		CpIndex<CONSTANT_NameAndType> nameTypeIndex = classFile.findConstantPoolNameAndType(method);
		CpIndex<CONSTANT_Methodref> methodIndex = classFile.findConstantPoolMethod(method);

		if(classIndex == null) {
			CONSTANT_Class clas = (CONSTANT_Class)ConstantPoolTag.CLASS.create(classFile);
			clas.setNameIndex(classNameIndex);
			classIndex = classFile.addToConstantPool(clas);
		}
		if(nameTypeIndex == null) {
			CONSTANT_NameAndType nameType = (CONSTANT_NameAndType)ConstantPoolTag.NAME_AND_TYPE.create(classFile);
			nameType.setNameIndex(methodNameIndex);
			nameType.setDescriptorIndex(methodDesIndex);
			nameTypeIndex = classFile.addToConstantPool(nameType);
		}
		if(methodIndex == null) {
			CONSTANT_Methodref methodref = (CONSTANT_Methodref)ConstantPoolTag.METHOD_REF.create(classFile);
			methodref.setClassIndex(classIndex);
			methodref.setNameAndTypeIndex(nameTypeIndex);
			methodIndex = classFile.addToConstantPool(methodref);
		}
		return methodIndex;
	}


	/** Add a string as a Utf8 constant pool object to the specified class file
	 * @param classFile the class file to add the string to
	 * @param str the string to add to the constant pool of the specified class file object
	 * @return the constant pool index of the added Utf8 object in the class file
	 */
	private static final CpIndex<CONSTANT_Utf8> addString(ClassFile classFile, String str) {
		CONSTANT_Utf8 name = (CONSTANT_Utf8)ConstantPoolTag.UTF8.create(classFile);
		name.setString(str);
		CpIndex<CONSTANT_Utf8> result = classFile.addToConstantPool(name);
		return result;
	}


	/** Create a Java class file method descriptor for the specified method
	 * @param method the method to create a method descriptor of
	 * @return a string in the format {@code ( ParameterDescriptor ) ReturnDescriptor}.
	 * See Java 8 class file format specification (§4.3)
	 */
	public static final String methodDescriptor(Method method) {
		StringBuilder strB = new StringBuilder();
		strB.append('(');
		for(Class<?> param : method.getParameterTypes()) {
			classToFieldDescriptor(param, strB);
		}
		strB.append(')');
		classToFieldDescriptor(method.getReturnType(), strB);
		return strB.toString();
	}


	/** Return the internal binary class name of the specified class. 
	 * For example, the normal binary name of class Thread is {@code java.lang.Thread}.
	 * In the internal form used in descriptors in the class file format, a
	 * reference to the name of class Thread is implemented using a CONSTANT_Utf8_info
	 * structure representing the string {@code java/lang/Thread} (§4.2.1).
	 * @param clas the class to get the internal formatted name of
	 * @return the internal binary name of the specified class
	 * @see #classToFieldDescriptor(Class)
	 */
	public static final String classNameInternal(Class<?> clas) {
		return classNameFieldDescriptor(clas.getName());
	}


	/** Convert a class to a field descriptor. For example a byte is 'B',
	 * an int is 'I', a String is 'Ljava/lang/String;' (§4.3).
	 * @param clas the class name to convert to a field descriptor
	 * @return the field descriptor name of the specified class
	 * @see #classNameInternal(Class)
	 */
	public static final String classToFieldDescriptor(Class<?> clas) {
		if(clas == Byte.TYPE) { return "B"; }
		else if(clas == Character.TYPE) { return "C"; }
		else if(clas == Double.TYPE) { return "D"; }
		else if(clas == Float.TYPE) { return "F"; }
		else if(clas == Integer.TYPE) { return "I"; }
		else if(clas == Long.TYPE) { return "J"; }
		else if(clas == Short.TYPE) { return "S"; }
		else if(clas == Boolean.TYPE) { return "Z"; }
		else if(clas == Void.TYPE) { return "V"; }
		else if(clas.isArray()) { return classNameFieldDescriptor(clas.getName()); }
		else {
			return 'L'+classNameFieldDescriptor(clas.getName())+';';
		}
	}


	/** Converts a class name retrieved by calling {@code class.getName()}
	 * to an internal binary class name, for example the internal binary name
	 * of {@code java.lang.String} is {@code java/lang/String}.
	 * @param className the standard class name to convert
	 * @return the internal binary class name of the specified class name
	 */
	private static final String classNameFieldDescriptor(String className) {
		return className.replace('.', '/');
	}


	/** Exactly the same as {@link #classToFieldDescriptor(Class)}
	 * except that the resulting string is appended to the specified string builder
	 * @param clas the class to convert to a field descriptor
	 * @param dst the string builder to append the resulting field descriptor to
	 * @see #classToFieldDescriptor(Class)
	 */
	public static final void classToFieldDescriptor(Class<?> clas, StringBuilder dst) {
		if(clas == Byte.TYPE) { dst.append('B'); return; }
		else if(clas == Character.TYPE) { dst.append('C'); return; }
		else if(clas == Double.TYPE) { dst.append('D'); return; }
		else if(clas == Float.TYPE) { dst.append('F'); return; }
		else if(clas == Integer.TYPE) { dst.append('I'); return; }
		else if(clas == Long.TYPE) { dst.append('J'); return; }
		else if(clas == Short.TYPE) { dst.append('S'); return; }
		else if(clas == Boolean.TYPE) { dst.append('Z'); return; }
		else if(clas == Void.TYPE) { dst.append('V'); return; }
		else if(clas.isArray()) { classNameFieldDescriptor(clas.getName(), dst); return; }
		else {
			dst.append('L');
			classNameFieldDescriptor(clas.getName(), dst);
			dst.append(';');
			return;
		}
	}


	/** Exactly the same as {@link #classNameToFieldDescriptor(String)}
	 * except that the resulting string is appended to the specified string builder
	 * @param className the standard class name to convert
	 * @param dst the string builder to append the resulting internal binary class name to
	 * @see #classNameToFieldDescriptor(String)
	 */
	private static final void classNameFieldDescriptor(String className, StringBuilder dst) {
		int pos = dst.length();
		dst.append(className);
		int index = dst.indexOf(".", pos);
		while(index > -1) {
			dst.setCharAt(index, '/');
			index = dst.indexOf(".", index+1);
		}
	}

}
