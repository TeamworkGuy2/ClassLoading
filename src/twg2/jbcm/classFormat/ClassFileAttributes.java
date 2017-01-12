package twg2.jbcm.classFormat;

import twg2.jbcm.classFormat.attributes.AnnotationDefault;
import twg2.jbcm.classFormat.attributes.Attribute_Type;
import twg2.jbcm.classFormat.attributes.BootstrapMethods;
import twg2.jbcm.classFormat.attributes.Code;
import twg2.jbcm.classFormat.attributes.ConstantValue;
import twg2.jbcm.classFormat.attributes.Deprecated;
import twg2.jbcm.classFormat.attributes.EnclosingMethod;
import twg2.jbcm.classFormat.attributes.Exceptions;
import twg2.jbcm.classFormat.attributes.InnerClasses;
import twg2.jbcm.classFormat.attributes.LineNumberTable;
import twg2.jbcm.classFormat.attributes.LocalVariableTable;
import twg2.jbcm.classFormat.attributes.LocalVariableTypeTable;
import twg2.jbcm.classFormat.attributes.MethodParameters;
import twg2.jbcm.classFormat.attributes.RuntimeInvisibleAnnotations;
import twg2.jbcm.classFormat.attributes.RuntimeInvisibleParameterAnnotations;
import twg2.jbcm.classFormat.attributes.RuntimeInvisibleTypeAnnotations;
import twg2.jbcm.classFormat.attributes.RuntimeVisibleAnnotations;
import twg2.jbcm.classFormat.attributes.RuntimeVisibleParameterAnnotations;
import twg2.jbcm.classFormat.attributes.RuntimeVisibleTypeAnnotations;
import twg2.jbcm.classFormat.attributes.Signature;
import twg2.jbcm.classFormat.attributes.SourceDebugExtension;
import twg2.jbcm.classFormat.attributes.SourceFile;
import twg2.jbcm.classFormat.attributes.StackMapTable;
import twg2.jbcm.classFormat.attributes.Synthetic;

/**
 * @author TeamworkGuy2
 * @since 2014-1-31
 */
public enum ClassFileAttributes {
	// Java SE 1.0.2
	CONSTANT_VALUE("ConstantValue") {
		@Override public Attribute_Type create(ClassFile clazz, int cpNameIndex, Code code) { return new ConstantValue(clazz, (short)cpNameIndex); } },
	CODE("Code") {
		@Override public Attribute_Type create(ClassFile clazz, int cpNameIndex, Code code) { return new Code(clazz, (short)cpNameIndex); } },
	EXCEPTIONS("Exceptions") {
		@Override public Attribute_Type create(ClassFile clazz, int cpNameIndex, Code code) { return new Exceptions(clazz, (short)cpNameIndex); } },
	SOURCE_FILE("SourceFile") {
		@Override public Attribute_Type create(ClassFile clazz, int cpNameIndex, Code code) { return new SourceFile(clazz, (short)cpNameIndex); } },
	LINE_NUMBER_TABLE("LineNumberTable") {
		@Override public Attribute_Type create(ClassFile clazz, int cpNameIndex, Code code) { return new LineNumberTable(clazz, code, (short)cpNameIndex); } },
	LOCAL_VARIABLE_TABLE("LocalVariableTable") {
		@Override public Attribute_Type create(ClassFile clazz, int cpNameIndex, Code code) { return new LocalVariableTable(clazz, code, (short)cpNameIndex); } },
	// Java SE 1.1
	INNER_CLASSES("InnerClasses") {
		@Override public Attribute_Type create(ClassFile clazz, int cpNameIndex, Code code) { return new InnerClasses(clazz, (short)cpNameIndex); } },
	SYNTHETIC("Synthetic") {
		@Override public Attribute_Type create(ClassFile clazz, int cpNameIndex, Code code) { return new Synthetic(clazz, (short)cpNameIndex); } },
	DEPRECATED("Deprecated") {
		@Override public Attribute_Type create(ClassFile clazz, int cpNameIndex, Code code) { return new Deprecated(clazz, (short)cpNameIndex); } },
	// Java SE 5.0
	ENCLOSING_METHOD("EnclosingMethod") {
		@Override public Attribute_Type create(ClassFile clazz, int cpNameIndex, Code code) { return new EnclosingMethod(clazz, (short)cpNameIndex); } },
	SIGNATURE("Signature") {
		@Override public Attribute_Type create(ClassFile clazz, int cpNameIndex, Code code) { return new Signature(clazz, (short)cpNameIndex); } },
	SOURCE_DEBUG_EXTENSION("SourceDebugExtension") {
		@Override public Attribute_Type create(ClassFile clazz, int cpNameIndex, Code code) { return new SourceDebugExtension(clazz, (short)cpNameIndex); } },
	LOCAL_VARIABLE_TYPE_TABLE("LocalVariableTypeTable") {
		@Override public Attribute_Type create(ClassFile clazz, int cpNameIndex, Code code) { return new LocalVariableTypeTable(clazz, code, (short)cpNameIndex); } },
	RUNTIME_VISIBLE_ANNOTATIONS("RuntimeVisibleAnnotations") {
		@Override public Attribute_Type create(ClassFile clazz, int cpNameIndex, Code code) { return new RuntimeVisibleAnnotations(clazz, (short)cpNameIndex); } },
	RUNTIME_INVISIBLE_ANNOTATIONS("RuntimeInvisibleAnnotations") {
		@Override public Attribute_Type create(ClassFile clazz, int cpNameIndex, Code code) { return new RuntimeInvisibleAnnotations(clazz, (short)cpNameIndex); } },
	RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS("RuntimeVisibleParameterAnnotations") {
		@Override public Attribute_Type create(ClassFile clazz, int cpNameIndex, Code code) { return new RuntimeVisibleParameterAnnotations(clazz, (short)cpNameIndex); } },
	RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS("RuntimeInvisibleParametersAnnotations") {
		@Override public Attribute_Type create(ClassFile clazz, int cpNameIndex, Code code) { return new RuntimeInvisibleParameterAnnotations(clazz, (short)cpNameIndex); } },
	ANNOTATION_DEFAULT("AnnotationDefault") {
		@Override public Attribute_Type create(ClassFile clazz, int cpNameIndex, Code code) { return new AnnotationDefault(clazz, (short)cpNameIndex); } },
	// Java SE 6
	STACK_MAP_TABLE("StackMapTable") {
		@Override public Attribute_Type create(ClassFile clazz, int cpNameIndex, Code code) { return new StackMapTable(clazz, (short)cpNameIndex); } },
	// Java SE 7
	BOOTSTRAP_METHODS("BootstrapMethods") {
		@Override public Attribute_Type create(ClassFile clazz, int cpNameIndex, Code code) { return new BootstrapMethods(clazz, (short)cpNameIndex); } },
	// Java SE 8 (2013-3-20)
	RUNTIME_VISIBLE_TYPE_ANNOTATIONS("RuntimeVisibleTypeAnnotations") {
		@Override public Attribute_Type create(ClassFile clazz, int cpNameIndex, Code code) { return new RuntimeVisibleTypeAnnotations(clazz, (short)cpNameIndex); } },
	RUNTIME_INVISIBLE_TYPE_ANNOTATIONS("RuntimeInvisibleTypeAnnotations") {
		@Override public Attribute_Type create(ClassFile clazz, int cpNameIndex, Code code) { return new RuntimeInvisibleTypeAnnotations(clazz, (short)cpNameIndex); } },
	METHOD_PARAMETERS("MethodParameters") {
		@Override public Attribute_Type create(ClassFile clazz, int cpNameIndex, Code code) { return new MethodParameters(clazz, (short)cpNameIndex); } };


	private String binaryName;

	ClassFileAttributes(String name) {
		this.binaryName = name;
	}


	public abstract Attribute_Type create(ClassFile clazz, int cpNameIndex, Code code);


	public String getBinaryName() {
		return binaryName;
	}

}
