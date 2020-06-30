package twg2.jbcm.classFormat;

import java.io.DataInput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
import twg2.jbcm.classFormat.attributes.ModuleMainClass;
import twg2.jbcm.classFormat.attributes.ModulePackages;
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
import twg2.jbcm.classFormat.attributes.UnknownAttributeType;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Utf8;

/**
 * @author TeamworkGuy2
 * @since 2014-1-31
 */
public enum ClassFileAttributes {
	// Java SE 1.0.2
	CONSTANT_VALUE("ConstantValue") {
		@Override public ConstantValue create(ClassFile clazz, int cpNameIndex, Code code) { return new ConstantValue(clazz, (short)cpNameIndex); }
	},
	CODE("Code") {
		@Override public Code create(ClassFile clazz, int cpNameIndex, Code code) { return new Code(clazz, (short)cpNameIndex); }
	},
	EXCEPTIONS("Exceptions") {
		@Override public Exceptions create(ClassFile clazz, int cpNameIndex, Code code) { return new Exceptions(clazz, (short)cpNameIndex); }
	},
	SOURCE_FILE("SourceFile") {
		@Override public SourceFile create(ClassFile clazz, int cpNameIndex, Code code) { return new SourceFile(clazz, (short)cpNameIndex); }
	},
	LINE_NUMBER_TABLE("LineNumberTable") {
		@Override public LineNumberTable create(ClassFile clazz, int cpNameIndex, Code code) { return new LineNumberTable(clazz, code, (short)cpNameIndex); }
	},
	LOCAL_VARIABLE_TABLE("LocalVariableTable") {
		@Override public LocalVariableTable create(ClassFile clazz, int cpNameIndex, Code code) { return new LocalVariableTable(clazz, code, (short)cpNameIndex); }
	},
	// Java SE 1.1
	INNER_CLASSES("InnerClasses") {
		@Override public InnerClasses create(ClassFile clazz, int cpNameIndex, Code code) { return new InnerClasses(clazz, (short)cpNameIndex); }
	},
	SYNTHETIC("Synthetic") {
		@Override public Synthetic create(ClassFile clazz, int cpNameIndex, Code code) { return new Synthetic(clazz, (short)cpNameIndex); }
	},
	DEPRECATED("Deprecated") {
		@Override public Deprecated create(ClassFile clazz, int cpNameIndex, Code code) { return new Deprecated(clazz, (short)cpNameIndex); }
	},
	// Java SE 5.0
	ENCLOSING_METHOD("EnclosingMethod") {
		@Override public EnclosingMethod create(ClassFile clazz, int cpNameIndex, Code code) { return new EnclosingMethod(clazz, (short)cpNameIndex); }
	},
	SIGNATURE("Signature") {
		@Override public Signature create(ClassFile clazz, int cpNameIndex, Code code) { return new Signature(clazz, (short)cpNameIndex); }
	},
	SOURCE_DEBUG_EXTENSION("SourceDebugExtension") {
		@Override public SourceDebugExtension create(ClassFile clazz, int cpNameIndex, Code code) { return new SourceDebugExtension(clazz, (short)cpNameIndex); }
	},
	LOCAL_VARIABLE_TYPE_TABLE("LocalVariableTypeTable") {
		@Override public LocalVariableTypeTable create(ClassFile clazz, int cpNameIndex, Code code) { return new LocalVariableTypeTable(clazz, code, (short)cpNameIndex); }
	},
	RUNTIME_VISIBLE_ANNOTATIONS("RuntimeVisibleAnnotations") {
		@Override public RuntimeVisibleAnnotations create(ClassFile clazz, int cpNameIndex, Code code) { return new RuntimeVisibleAnnotations(clazz, (short)cpNameIndex); }
	},
	RUNTIME_INVISIBLE_ANNOTATIONS("RuntimeInvisibleAnnotations") {
		@Override public RuntimeInvisibleAnnotations create(ClassFile clazz, int cpNameIndex, Code code) { return new RuntimeInvisibleAnnotations(clazz, (short)cpNameIndex); }
	},
	RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS("RuntimeVisibleParameterAnnotations") {
		@Override public RuntimeVisibleParameterAnnotations create(ClassFile clazz, int cpNameIndex, Code code) { return new RuntimeVisibleParameterAnnotations(clazz, (short)cpNameIndex); }
	},
	RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS("RuntimeInvisibleParametersAnnotations") {
		@Override public RuntimeInvisibleParameterAnnotations create(ClassFile clazz, int cpNameIndex, Code code) { return new RuntimeInvisibleParameterAnnotations(clazz, (short)cpNameIndex); }
	},
	ANNOTATION_DEFAULT("AnnotationDefault") {
		@Override public AnnotationDefault create(ClassFile clazz, int cpNameIndex, Code code) { return new AnnotationDefault(clazz, (short)cpNameIndex); }
	},
	// Java SE 6
	STACK_MAP_TABLE("StackMapTable") {
		@Override public StackMapTable create(ClassFile clazz, int cpNameIndex, Code code) { return new StackMapTable(clazz, (short)cpNameIndex); }
	},
	// Java SE 7
	BOOTSTRAP_METHODS("BootstrapMethods") {
		@Override public BootstrapMethods create(ClassFile clazz, int cpNameIndex, Code code) { return new BootstrapMethods(clazz, (short)cpNameIndex); }
	},
	// Java SE 8 (2013-3-20)
	RUNTIME_VISIBLE_TYPE_ANNOTATIONS("RuntimeVisibleTypeAnnotations") {
		@Override public RuntimeVisibleTypeAnnotations create(ClassFile clazz, int cpNameIndex, Code code) { return new RuntimeVisibleTypeAnnotations(clazz, (short)cpNameIndex); }
	},
	RUNTIME_INVISIBLE_TYPE_ANNOTATIONS("RuntimeInvisibleTypeAnnotations") {
		@Override public RuntimeInvisibleTypeAnnotations create(ClassFile clazz, int cpNameIndex, Code code) { return new RuntimeInvisibleTypeAnnotations(clazz, (short)cpNameIndex); }
	},
	METHOD_PARAMETERS("MethodParameters") {
		@Override public MethodParameters create(ClassFile clazz, int cpNameIndex, Code code) { return new MethodParameters(clazz, (short)cpNameIndex); }
	},
	// Java SE 9 (2017-12-22)
	MODULE("Module") {
		@Override public twg2.jbcm.classFormat.attributes.Module create(ClassFile clazz, int cpNameIndex, Code code) { return new twg2.jbcm.classFormat.attributes.Module(clazz, (short)cpNameIndex); }
	},
	MODULE_PACKAGES("ModulePackages") {
		@Override public ModulePackages create(ClassFile clazz, int cpNameIndex, Code code) { return new ModulePackages(clazz, (short)cpNameIndex); }
	},
	MODULE_MAIN_CLASS("ModuleMainClass") {
		@Override public ModuleMainClass create(ClassFile clazz, int cpNameIndex, Code code) { return new ModuleMainClass(clazz, (short)cpNameIndex); }
	};	


	private static Map<String, ClassFileAttributes> attribMap;

	private String binaryName;


	static {
		ClassFileAttributes[] attribs = ClassFileAttributes.values();
		attribMap = new HashMap<String, ClassFileAttributes>(attribs.length);
		for(ClassFileAttributes attrib : attribs) {
			attribMap.put(attrib.getBinaryName(), attrib);
		}
	}


	ClassFileAttributes(String name) {
		this.binaryName = name;
	}


	public abstract Attribute_Type create(ClassFile clazz, int cpNameIndex, Code code);


	public String getBinaryName() {
		return binaryName;
	}


	/** Java class file format <code>Attribute</code> info type loader mothod
	 * @author TeamworkGuy2
	 * @since 2013-7-7
	 * 
	 * @param in the input data stream to read the attribute from
	 * @param resolver the class file to use for constant pool index resolution
	 * @param codeCaller the code related to the attribute being loaded
	 * @return the attribute object read from the input stream
	 * @throws IOException if there is an error reading from the input stream
	 * or if the data read does not match a known attribute format.
	 */
	public static final Attribute_Type loadAttributeObject(DataInput in, ClassFile resolver, Code codeCaller) throws IOException {
		short attributeNameIndex = in.readShort();
		String name = resolver.getCpString(attributeNameIndex);

		Attribute_Type attrib = attribMap.get(name).create(resolver, attributeNameIndex, codeCaller);

		if(attrib == null) {
			attrib = new UnknownAttributeType(resolver, attributeNameIndex);
			System.err.println("[Settings] Unknown Attribute_Info type, name: " + name);
		}

		attrib.readData(in);
		return attrib;
	}


	/** Read an attribute name index from an input stream and compare the constant pool string
	 * at that index to the expected name.
	 * This method checks if attribute names should be read first and does not modify the input stream
	 * if attribute names should not be read
	 * @author TeamworkGuy2
	 * @since 2014-3-19
	 * 
	 * @param in the input stream to read the name from
	 * @param resolver the constant pool resolver to check the name index against
	 * @param attributeName the expected name of the attribute
	 * @return -1 if the expected name does not match the constant pool string at the index read
	 * or the constant pool index of the correct string read
	 * @throws IOException if there is an error reading from the input stream
	 */
	public static final CpIndex<CONSTANT_Utf8> readAttributeNameIndex(DataInput in, ClassFile resolver, String attributeName) throws IOException {
		short nameIndex = in.readShort();
		String cpName = resolver.getCpString(nameIndex);
		if(Settings.checkAttributeName) {
			if(!attributeName.equals(cpName)) {
				throw new IllegalStateException(attributeName + " attribute name does not match: " + cpName);
			}
		}
		return resolver.getCheckCpIndex(nameIndex, CONSTANT_Utf8.class);
	}


	/** Read an attribute name index from an input stream.
	 * This method checks if attribute names should be read first and does not modify the input stream
	 * if attribute names should not be read
	 * @author TeamworkGuy2
	 * @since 2014-3-19
	 * 
	 * @param in the input stream to read the name from
	 * @param resolver the constant pool resolver to check the name index against
	 * @return -1 if the expected name does not match the constant pool string at the index read
	 * or the constant pool index of the correct string read
	 * @throws IOException if there is an error reading from the input stream
	 */
	public static final CpIndex<CONSTANT_Utf8> readAttributeNameIndex(DataInput in, ClassFile resolver) throws IOException {
		short nameIndex = in.readShort();
		return resolver.getCheckCpIndex(nameIndex, CONSTANT_Utf8.class);
	}

}
