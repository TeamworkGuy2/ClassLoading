package twg2.jbcm.classFormat;

import java.lang.reflect.Method;

import twg2.jbcm.classFormat.constantPool.CONSTANT_CP_Info;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Class;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Methodref;
import twg2.jbcm.classFormat.constantPool.CONSTANT_NameAndType;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Utf8;
import twg2.jbcm.modify.TypeUtility;

/** Constant pool extension methods for {@link ClassFile}
 * @author TeamworkGuy2
 * @since 2020-06-06
 */
public abstract class ConstantPoolExtensions {


	/** The number of constant pool entries
	 * @return the constant pool size
	 */
	public abstract int getConstantPoolCount();


	/** Get the constant pool value at the specified index
	 * @param index the constant pool index
	 * @return the constant pool value at the specified index
	 */
	public abstract CpIndex<CONSTANT_CP_Info> getConstantPoolIndex(int index);


	/** Optional, called by {@link #getExpectCpIndex(int, Class)}. Track constant pool index expected types for validation once constant pool is fully loaded/resolved
	 * @param index the constant pool index
	 * @param clazz the expected {@link CONSTANT_CP_Info} type at the specified constant pool index
	 */
	public abstract void addConstantPoolExpectation(int index, Class<? extends CONSTANT_CP_Info> clazz);


	public String getCpString(int attributeNameIndex) {
		CpIndex<CONSTANT_CP_Info> cpItem = getConstantPoolIndex(attributeNameIndex);
		CONSTANT_CP_Info cpInfoObj = cpItem.getCpObject();
		if(!(cpInfoObj instanceof CONSTANT_Utf8)) {
			throw new IllegalStateException("constant pool string index not of type CONSTANT_Utf8");
		}
		String cpName = ((CONSTANT_Utf8)cpInfoObj).getString();
		return cpName;
	}


	// TODO debugging
	public CpIndex<CONSTANT_CP_Info> getCpIndex(int index, boolean allowZero) {
		if(index == 0) {
			return null;
		}
		return getCpIndex(index);
	}


	public CpIndex<CONSTANT_CP_Info> getCpIndex(int index) {
		if(index > 0 && index < getConstantPoolCount()) {
			CpIndex<CONSTANT_CP_Info> result = getConstantPoolIndex(index);
			return result;
		}
		if(Settings.checkCPIndex) {
			throw new IllegalStateException("constant pool index " + index + " out of constant pool size bounds");
		}
		return null;
	}


	public <T extends CONSTANT_CP_Info> CpIndex<T> getExpectCpIndex(int index, Class<T> clazz) {
		if(index > 0 && index < getConstantPoolCount()) {
			@SuppressWarnings("unchecked")
			CpIndex<T> result = (CpIndex<T>)getConstantPoolIndex(index);
			if(Settings.checkCPExpectedType) {
				addConstantPoolExpectation(index, clazz);
			}
			return result;
		}
		if(Settings.checkCPIndex) {
			throw new IllegalStateException("constant pool index " + index + " out of constant pool size bounds");
		}
		return null;
	}


	public <T extends CONSTANT_CP_Info> CpIndex<T> getCheckCpIndex(int index, Class<T> clazz) {
		return getCheckCpIndex(index, clazz, false);
	}


	public <T extends CONSTANT_CP_Info> CpIndex<T> getCheckCpIndex(int index, Class<T> clazz, boolean allowZero) {
		if(index > 0 && index < getConstantPoolCount()) {
			@SuppressWarnings("unchecked")
			CpIndex<T> result = (CpIndex<T>)getConstantPoolIndex(index);
			if(!result.isInitialized()) {
				result.initialize(clazz);
			}
			else {
				checkCpIndex(result, clazz);
			}
			return result;
		}
		if(!allowZero && Settings.checkCPIndex) {
			throw new IllegalStateException("constant pool index " + index + " out of constant pool size bounds");
		}
		return null;
	}


	public <T extends CONSTANT_CP_Info> CpIndex<T> checkCpIndex(CpIndex<T> item, Class<? extends CONSTANT_CP_Info> clazz) {
		if(!clazz.isInstance(item.getCpObject())) {
			throw new ClassCastException("constant pool item " + item + " does not match check type " + clazz);
		}
		return item;
	}


	public CpIndex<CONSTANT_Utf8> getAttributeNameIndex(short index) {
		return this.getCheckCpIndex(index, CONSTANT_Utf8.class);
	}


	/** Find the constant pool index of a specific string
	 * @param str the string to search for in the constant pool
	 * @return the index of the matching string in the constant pool, or null if no match was found
	 */
	public CpIndex<CONSTANT_Utf8> findConstantPoolString(String str) {
		for(int i = 1; i < getConstantPoolCount(); i++) {
			CpIndex<CONSTANT_CP_Info> cpItem = getConstantPoolIndex(i);
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


	/** Find the constant pool index of a {@link CONSTANT_Methodref} matching the name and descriptor
	 * of the {@link Method} given.
	 * @param method the {@link Method} to find
	 * @return the index of the matching method in the constant pool, or null if no match was found
	 */
	public CpIndex<CONSTANT_Methodref> findConstantPoolMethod(Method method) {
		String className = TypeUtility.classNameInternal(method.getDeclaringClass());
		String methodName = method.getName();
		String methodDes = TypeUtility.methodDescriptor(method);

		for(int i = 1, size = getConstantPoolCount(); i < size; i++) {
			CpIndex<CONSTANT_CP_Info> cpItem = getConstantPoolIndex(i);
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


	/** Find the constant pool index of a {@link CONSTANT_NameAndType} matching the name and descriptor
	 * of the {@link Method} given.
	 * @param method the method to search for in this class' constant pool
	 * @return the {@link CONSTANT_NameAndType} matching the method specified, otherwise null
	 */
	public CpIndex<CONSTANT_NameAndType> findConstantPoolNameAndType(Method method) {
		String methodName = method.getName();
		String methodDes = TypeUtility.methodDescriptor(method);
		return findConstantPoolNameAndType(methodName, methodDes);
	}


	/** Find the constant pool index of a {@link CONSTANT_NameAndType} matching the formatted method name
	 * and method descriptor specified.
	 * @param methodName the name of the method, for example {@code "main"}
	 * @param methodDescriptor the method's descriptor, for example {@code "()V"} for a parameterless, void return method.
	 * @return the {@link CONSTANT_NameAndType} matching the name and descriptor specified, otherwise null
	 * @see TypeUtility#methodDescriptor(Method)
	 * @see Method#getName()
	 */
	public CpIndex<CONSTANT_NameAndType> findConstantPoolNameAndType(String methodName, String methodDescriptor) {
		for(int i = 1, size = getConstantPoolCount(); i < size; i++) {
			CpIndex<CONSTANT_CP_Info> cpItem = getConstantPoolIndex(i);
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
		for(int i = 1, size = getConstantPoolCount(); i < size; i++) {
			CpIndex<CONSTANT_CP_Info> cpItem = getConstantPoolIndex(i);
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

}
