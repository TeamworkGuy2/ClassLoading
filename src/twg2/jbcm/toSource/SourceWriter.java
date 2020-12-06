package twg2.jbcm.toSource;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.Method_Info;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Utf8;
import twg2.jbcm.ir.MethodStack;
import twg2.jbcm.modify.TypeUtility;

/**
 * @author TeamworkGuy2
 * @since 2020-06-06
 */
public class SourceWriter {
	StringBuilder src;
	Indent indent;
	String newln;


	public SourceWriter(String indentMark, String newln) {
		this.indent = new Indent(indentMark);
		this.newln = newln;
		this.src = new StringBuilder();
	}


	public SourceWriter accessModifierClass(int accessFlags) {
		src.append(accessModifiersClass(accessFlags));
		return this;
	}


	public SourceWriter accessModifierField(int accessFlags) {
		src.append(accessModifiersField(accessFlags));
		return this;
	}


	public SourceWriter accessModifierMethod(int accessFlags) {
		src.append(accessModifiersMethod(accessFlags));
		return this;
	}


	public SourceWriter identifier(String identifier) {
		src.append(identifier);
		return this;
	}


	public SourceWriter keyword(String keyword) {
		src.append(keyword);
		return this;
	}


	public SourceWriter operator(String operator) {
		src.append(operator);
		return this;
	}


	public SourceWriter separator(char separator) {
		src.append(separator);
		return this;
	}


	public SourceWriter separator(String separator) {
		src.append(separator);
		return this;
	}


	public SourceWriter semicolon() {
		src.append(';');
		return this;
	}


	public SourceWriter fieldDescriptor(CONSTANT_Utf8 type) {
		TypeUtility.fieldDescriptorToSource(type.getString(), src);
		return this;
	}


	public SourceWriter methodDescriptor(CONSTANT_Utf8 type, String constructorName, String methodName, MethodStack methodVars, ParameterNamer paramNamer) {
		TypeUtility.methodReturnType(type.getString(), src);
		space();
		
		if("<clinit>".equals(methodName)) {
			src.append("static");
		}
		else {
			String name = "<init>".equals(methodName) ? constructorName : methodName;
			src.append(name);
			src.append('(');
			TypeUtility.methodParametersDescriptorToSource(type.getString(), name, src, methodVars, paramNamer);
			src.append(')');
		}
		return this;
	}


	public SourceWriter type(String type) {
		src.append(type);
		return this;
	}


	public SourceWriter openBracket() {
		indent.indent();
		src.append(" {").append(newln);
		writeIndent();
		return this;
	}


	public SourceWriter closeBracket() {
		indent.dedent();
		src.append(newln);
		writeIndent();
		src.append('}').append(newln);
		return this;
	}


	public SourceWriter openParenthesis() {
		src.append('(');
		return this;
	}


	public SourceWriter closeParenthesis() {
		src.append(')');
		return this;
	}


	public SourceWriter writeClassCode(ClassFile cls, Method_Info method, MethodStack methodStack) {
		method.getCode().toClassString(this.getIndent().getIndent(), this.src);
		return this;
	}


	public SourceWriter writeSourceCode(ClassFile cls, Method_Info method, MethodStack methodStack) {
		CodeToSource.toSource(cls, method, methodStack, this);
		return this;
	}


	public SourceWriter space() {
		src.append(' ');
		return this;
	}


	public SourceWriter newln() {
		src.append(newln);
		writeIndent();
		return this;
	}


	public SourceWriter comment(String comment) {
		if(comment.indexOf('\n') > -1) {
			src.append("/* ").append(comment).append(" */");
		}
		else {
			src.append("// ").append(comment);
		}
		return this;
	}


	@Override
	public String toString() {
		return src.toString();
	}



	protected Indent getIndent() {
		return indent;
	}


	private void writeIndent() {
		indent.writeTo(src);
	}


	/* The value of the access_flags item is a mask of flags used to denote access permission to and properties of
	 * this field. The interpretation of each flag, when set, is as shown in Table 4.4.
	 * Fields of classes may set any of the flags in Table 4.4. However, a specific field of a class may have at most
	 * one of its ACC_PRIVATE, ACC_PROTECTED, and ACC_PUBLIC flags set (§2.7.4) and may not have both its ACC_FINAL
	 * and ACC_VOLATILE flags set (§2.9.1).
	 * Flag Name 	Value 	Interpretation
	 * ACC_PUBLIC 	0x0001 	Declared public; may be accessed from outside its package.
	 * ACC_PRIVATE 	0x0002 	Declared private; usable only within the defining class.
	 * ACC_PROTECTED 	0x0004 	Declared protected; may be accessed within subclasses.
	 * ACC_STATIC 	0x0008 	Declared static.
	 * ACC_FINAL 	0x0010 	Declared final; no further assignment after initialization.
	 * ACC_VOLATILE 	0x0040 	Declared volatile; cannot be cached.
	 * ACC_TRANSIENT 	0x0080 	Declared transient; not written or read by a persistent object manager.
	 */
	public String accessModifiersField(int access) {
		StringBuilder sb = new StringBuilder();
		if((access & 0x0001) == 0x0001) { // ACC_PUBLIC
			sb.append("public ");
		}
		if((access & 0x0004) == 0x0004) { // ACC_PROTECTED
			sb.append("protected ");
		}
		if((access & 0x0002) == 0x0002) { // ACC_PRIVATE
			sb.append("private ");
		}
		if((access & 0x0008) == 0x0008) { // ACC_STATIC
			sb.append("static ");
		}
		if((access & 0x0010) == 0x0010) { // ACC_FINAL
			sb.append("final ");
		}
		if((access & 0x0040) == 0x0040) { // ACC_VOLATILE
			sb.append("volatile ");
		}
		if((access & 0x0080) == 0x0080) { // ACC_TRANSIENT
			sb.append("transient ");
 		}

		return sb.substring(0, sb.length() - 1);
	}


	/* The value of the access_flags item is a mask of flags used to denote access permission to and properties of
	 * this method. The interpretation of each flag, when set, is as shown in Table 4.5.
	 * Flag Name 	Value 	Interpretation
	 * ACC_PUBLIC 	0x0001 	Declared public; may be accessed from outside its package.
	 * ACC_PRIVATE 	0x0002 	Declared private; accessible only within the defining class.
	 * ACC_PROTECTED 	0x0004 	Declared protected; may be accessed within subclasses.
	 * ACC_STATIC 	0x0008 	Declared static.
	 * ACC_FINAL 	0x0010 	Declared final; may not be overridden.
	 * ACC_SYNCHRONIZED 	0x0020 	Declared synchronized; invocation is wrapped in a monitor lock.
	 * ACC_NATIVE 	0x0100 	Declared native; implemented in a language other than Java.
	 * ACC_ABSTRACT 	0x0400 	Declared abstract; no implementation is provided.
	 * ACC_STRICT 	0x0800 	Declared strictfp; floating-point mode is FP-strict 
	 */
	public String accessModifiersMethod(int access) {
		StringBuilder sb = new StringBuilder();
		if((access & 0x0001) == 0x0001) { // ACC_PUBLIC
			sb.append("public ");
		}
		if((access & 0x0002) == 0x0002) { // ACC_PRIVATE
			sb.append("private ");
		}
		if((access & 0x0004) == 0x0004) { // ACC_PROTECTED
			sb.append("protected ");
		}
		if((access & 0x0008) == 0x0008) { // ACC_STATIC
			sb.append("static ");
		}
		if((access & 0x0010) == 0x0010) { // ACC_FINAL
			sb.append("final ");
		}
		if((access & 0x0020) == 0x0020) { // ACC_SYNCHRONIZED
			sb.append("synchronized ");
		}
		if((access & 0x0100) == 0x0100) { // ACC_NATIVE
			sb.append("native ");
		}
		if((access & 0x0400) == 0x0400) { // ACC_ABSTRACT
			sb.append("abstract ");
		}
		if((access & 0x0800) == 0x0800) { // ACC_STRICT
			sb.append("strictfp ");
		}

		return sb.substring(0, sb.length() - 1);
	}


	/** The value of the access_flags item is a mask of flags used to denote access permissions to and properties of
	 * this class or interface. The interpretation of each flag, when set, is as shown in Table 4.1.
	 * Flag Name 	Value 	Interpretation
	 * ACC_PUBLIC 	0x0001 	Declared public; may be accessed from outside its package.
	 * ACC_FINAL 	0x0010 	Declared final; no subclasses allowed.
	 * ACC_SUPER 	0x0020 	Treat superclass methods specially when invoked by the invokespecial instruction.
	 * ACC_INTERFACE 	0x0200 	Is an interface, not a class.
	 * ACC_ABSTRACT 	0x0400 	Declared abstract; may not be instantiated.
	 * ACC_SYNTHETIC 	0x1000 	Declared synthetic; not present in the source code.
	 * ACC_ANNOTATION 	0x2000 	Declared as an annotation type.
	 * ACC_ENUM 	0x4000 	Declared as an enum type.
	 * ACC_MODULE 	0x8000 	Is a module, not a class or interface. 
	 */
	public String accessModifiersClass(int access) {
		StringBuilder sb = new StringBuilder();
		boolean isClass = true;

		if((access & 0x0001) == 0x0001) { // ACC_PUBLIC
			sb.append("public ");
		}
		if((access & 0x0008) == 0x0008) { // ACC_STATIC
			sb.append("static ");
		}
		if((access & 0x0010) == 0x0010) { // ACC_FINAL
			sb.append("final ");
		}
		if((access & 0x0020) == 0x0020) { // ACC_SUPER
			// nothing
		}
		if((access & 0x0200) == 0x0200) { // ACC_INTERFACE
			sb.append("interface ");
			isClass = false;
		}
		if((access & 0x0400) == 0x0400) { // ACC_ABSTRACT
			sb.append("abstract ");
		}
		if((access & 0x1000) == 0x0001) { // ACC_SYNTHETIC
			// nothing
		}	
		if((access & 0x2000) == 0x2000) { // ACC_ANNOTATION
			sb.append("@interface ");
			isClass = false;
		}
		if((access & 0x4000) == 0x4000) { // ACC_ENUM
			sb.append("enum ");
			isClass = false;
		}
		if((access & 0x8000) == 0x8000) { // ACC_MODULE
			sb.append("module ");
			isClass = false;
		}

		if(!isClass) {
			sb.append("class ");
		}

		return sb.substring(0, sb.length() - 1);
	}

}
