package twg2.jbcm.modify;

import java.lang.reflect.Method;

import twg2.jbcm.IoUtility;
import twg2.jbcm.MethodStack;
import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.ConstantPoolTag;
import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.attributes.Code;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Class;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Methodref;
import twg2.jbcm.classFormat.constantPool.CONSTANT_NameAndType;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Utf8;
import twg2.jbcm.toSource.ParameterNamer;

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
	public static void changeCpIndices(byte[] instructions, final int offset, final int oldIndex, final int newIndex) {
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
	public static int addMethodCall(Code code, final CpIndex<CONSTANT_Methodref> methodCp, Method method) {
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
	public static CpIndex<CONSTANT_Methodref> addMethodToConstantPool(ClassFile classFile, Method method) {
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
	private static CpIndex<CONSTANT_Utf8> addString(ClassFile classFile, String str) {
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
	public static String methodDescriptor(Method method) {
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
	public static String classNameInternal(Class<?> clas) {
		return classNameFieldDescriptor(clas.getName());
	}


	public static String methodDescriptor(CONSTANT_NameAndType method) {
		StringBuilder dst = new StringBuilder();
		methodDescriptor(method, dst);
		return dst.toString();
	}


	public static void methodDescriptor(CONSTANT_NameAndType method, StringBuilder dst) {
		String md = method.getDescriptor().getString();
		if(md.charAt(0) != '(') {
			throw new IllegalArgumentException("method descriptor expected to start with '('");
		}

		int lastIdx = md.lastIndexOf(')');
		String paramsDescriptor = md.substring(1, lastIdx);
		String remainingDescriptors = paramsDescriptor;

		dst.append(method.getName().getString()).append('(');
		while(remainingDescriptors.length() > 0) {
			int read = typeDescriptorToSource(remainingDescriptors, dst);
			remainingDescriptors = remainingDescriptors.substring(read);
			if(remainingDescriptors.length() > 0) {
				dst.append(", ");
			}
		}
		dst.append(')').append(" : ");

		String returnDescriptor = md.substring(lastIdx + 1);
		typeDescriptorToSource(returnDescriptor, dst);
	}


	/** Convert a class to a field descriptor. For example a byte is 'B',
	 * an int is 'I', a String is 'Ljava/lang/String;' (§4.3).
	 * @param clas the class name to convert to a field descriptor
	 * @return the field descriptor name of the specified class
	 * @see #classNameInternal(Class)
	 */
	public static String classToFieldDescriptor(Class<?> clas) {
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
	private static String classNameFieldDescriptor(String className) {
		return className.replace('.', '/');
	}


	/** Exactly the same as {@link #classToFieldDescriptor(Class)}
	 * except that the resulting string is appended to the specified string builder
	 * @param clas the class to convert to a field descriptor
	 * @param dst the string builder to append the resulting field descriptor to
	 * @see #classToFieldDescriptor(Class)
	 */
	public static void classToFieldDescriptor(Class<?> clas, StringBuilder dst) {
		if(clas == Byte.TYPE) { dst.append('B'); }
		else if(clas == Character.TYPE) { dst.append('C'); }
		else if(clas == Double.TYPE) { dst.append('D'); }
		else if(clas == Float.TYPE) { dst.append('F'); }
		else if(clas == Integer.TYPE) { dst.append('I'); }
		else if(clas == Long.TYPE) { dst.append('J'); }
		else if(clas == Short.TYPE) { dst.append('S'); }
		else if(clas == Boolean.TYPE) { dst.append('Z'); }
		else if(clas == Void.TYPE) { dst.append('V'); }
		else if(clas.isArray()) { classNameFieldDescriptor(clas.getName(), dst); }
		else {
			dst.append('L');
			classNameFieldDescriptor(clas.getName(), dst);
			dst.append(';');
		}
	}


	/** Exactly the same as {@link #classNameToFieldDescriptor(String)}
	 * except that the resulting string is appended to the specified string builder
	 * @param className the standard class name to convert
	 * @param dst the string builder to append the resulting internal binary class name to
	 * @see #classNameToFieldDescriptor(String)
	 */
	private static void classNameFieldDescriptor(String className, StringBuilder dst) {
		int pos = dst.length();
		dst.append(className);
		int index = dst.indexOf(".", pos);
		while(index > -1) {
			dst.setCharAt(index, '/');
			index = dst.indexOf(".", index+1);
		}
	}


	/** Convert a field descriptor string to source
	 * same as {@link #fieldDescriptorToSource(String, StringBuilder)
	 * @param fd the field descriptor
	 * @param dst the string builder to append the resulting field type to
	 */
	public static void fieldDescriptorToSource(String fd, StringBuilder dst) {
		typeDescriptorToSource(fd, dst);
	}


	/** Extract method parameters descriptor string to source
	 * @param md the method descriptor
	 * @param dst the string builder to append the resulting field type to
	 * @param methodVars map of parameters/variables used by the method, generally starts out empty when this method is called
	 * @param paramNamer generates names for parameters
	 */
	public static void methodParametersDescriptorToSource(String md, String methodName, StringBuilder dst, MethodStack methodVars, ParameterNamer paramNamer) {
		if(md.charAt(0) != '(') {
			throw new IllegalArgumentException("method descriptor expected to start with '('");
		}

		String paramsDescriptor = md.substring(1, md.lastIndexOf(')'));
		String remainingDescriptors = paramsDescriptor;

		while(remainingDescriptors.length() > 0) {
			int off = dst.length();
			int read = typeDescriptorToSource(remainingDescriptors, dst);
			String type = dst.substring(off, dst.length());
			String paramName = paramNamer.getName(methodName, type, methodVars);
			dst.append(' ').append(paramName);
			remainingDescriptors = remainingDescriptors.substring(read);
			if(remainingDescriptors.length() > 0) {
				dst.append(", ");
			}
		}
	}


	/** Extract method return type descriptor string to source
	 * @param fd the field descriptor
	 * @param dst the string builder to append the resulting field type to
	 */
	public static void methodReturnDescriptorToSource(String md, StringBuilder dst) {
		String returnDescriptor = md.substring(md.lastIndexOf(')') + 1);
		typeDescriptorToSource(returnDescriptor, dst);
	}


	/** Convert a type descriptor string to source
	 * @param fd the field descriptor
	 * @param dst the string builder to append the resulting field type to
	 * @return the number of characters read
	 */
	public static int typeDescriptorToSource(String td, StringBuilder dst) {
		char tc = td.charAt(0);
		int idx = 0;
		if(tc == 'B') { dst.append("byte"); return 1; }
		else if(tc == 'C') { dst.append("char"); return 1; }
		else if(tc == 'D') { dst.append("double"); return 1; }
		else if(tc == 'F') { dst.append("float"); return 1; }
		else if(tc == 'I') { dst.append("int"); return 1; }
		else if(tc == 'J') { dst.append("long"); return 1; }
		else if(tc == 'S') { dst.append("short"); return 1; }
		else if(tc == 'Z') { dst.append("boolean"); return 1; }
		else if(tc == 'V') { dst.append("void"); return 1; }
		else if(tc == '[') {
			int read = typeDescriptorToSource(td.substring(1), dst); dst.append("[]");
			return read + 1;
		}
		else if(tc == 'L' && (idx = td.indexOf(";")) > -1) {
			String typeName = td.substring(1, idx).replace('/', '.'); // full type name
			if(typeName.startsWith("java.lang.")) typeName = typeName.substring(10);
			dst.append(typeName);
			return idx + 1;
		}
		else {
			throw new IllegalArgumentException("unknown type descriptor '" + td + "'");
		}
	}


	public static boolean isPrimitive(String type) {
		switch(type) {
		case "boolean":
			return true;
		case "byte":
			return true;
		case "char":
			return true;
		case "short":
			return true;
		case "int":
			return true;
		case "long":
			return true;
		case "float":
			return true;
		case "double":
			return true;
		default:
			return false;
		}
	}

}
