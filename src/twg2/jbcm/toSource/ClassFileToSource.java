package twg2.jbcm.toSource;

import twg2.jbcm.MethodStack;
import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.Field_Info;
import twg2.jbcm.classFormat.Method_Info;

/**
 * @author TeamworkGuy2
 * @since 2020-06-06
 */
public class ClassFileToSource {

	public static void toSource(ClassFile cls, SourceWriter dst, boolean decompile) {
		// TODO Annotations
		int attributeCount = cls.getAttributeCount();
		StringBuilder attrs = new StringBuilder("Attributes: " + attributeCount + "\n");
		for(int i = 0; i < attributeCount; i++) {
			attrs.append(cls.getAttribute(i).toString()).append('\n');
		}
		dst.comment(attrs.toString());
		dst.newln();

		// Class
		dst.accessModifierClass(cls.getAccessFlags()).space().keyword("class").space();
		String className = cls.getClassIndex().getCpObject().getName().getString();
		className = className.substring(className.lastIndexOf('/') + 1);
		dst.identifier(className);
		// extends
		String superClassName = cls.getSuperClassIndex().getCpObject().getName().getString().replace('/', '.');
		if(!superClassName.equals("java.lang.Object")) {
			dst.space().keyword("extends").space().identifier(superClassName);
		}
		// implements
		int interfaceCount = cls.getInterfaceCount();
		if(interfaceCount > 0) {
			dst.space().keyword("implements").space();
			for(int i = 0; i < interfaceCount; i++) {
				dst.identifier(cls.getInterface(i).getName().getString());
				if(i < interfaceCount - 1) {
					dst.separator(',').space();
				}
			}
		}
		dst.openBracket();

		//for(int i = 1, size = cls.getConstantPoolCount(); i < size; i++) {
		//	dst.append("Constant " + i + ": " + cls.getConstantPoolIndex(i).getCpObject().toString());
		//}

		// Fields
		int fieldCount = cls.getFieldCount();
		if(fieldCount > 0) {
			for(int i = 0; i < fieldCount; i++) {
				Field_Info field = cls.getField(i);
				dst.accessModifierField(field.getAccessFlags()).space().fieldDescriptor(field.getDescriptor()).space().identifier(field.getName().getString()).semicolon();
				dst.newln();
			}
		}
		dst.newln();

		// Methods
		ParameterNamer paramNamer = new DefaultParameterNamer();
		int methodCount = cls.getMethodCount();
		if(methodCount > 0) {
			for(int i = 0; i < methodCount; i++) {
				Method_Info method = cls.getMethod(i);

				MethodStack methodVars = new MethodStack(className, (method.getAccessFlags() & Method_Info.ACC_STATIC) == 0);

				dst.accessModifierMethod(method.getAccessFlags()).space();
				// parameters
				dst.methodDescriptor(method.getDescriptor(), className, method.getName().getString(), methodVars, paramNamer);
				dst.openBracket();
				if(decompile) {
					dst.writeSourceCode(cls, method, methodVars);
				}
				else {
					dst.writeClassCode(cls, method, methodVars);
				}
				dst.closeBracket();
				dst.newln();
			}
		}
		dst.closeBracket();
	}



	/**
	 * @author TeamworkGuy2
	 * @since 2020-06-06
	 */
	static class DefaultParameterNamer implements ParameterNamer {

		@Override
		public String getName(String methodName, String type, MethodStack methodStack) {
			return methodStack.addParameterUnnamed(type);
		}

	}

}
