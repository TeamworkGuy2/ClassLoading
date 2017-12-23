package twg2.jbcm.classFormat;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.constantPool.CONSTANT_CP_Info;
import twg2.jbcm.modify.IndexUtility;

/** A constant pool index and type.<br/>
 * The data type of the index this object points to can be lazily initialized by using the
 * {@link #CpIndex(int)} constructor and later calling {@link #initialize(Class)}.
 * @author TeamworkGuy2
 * @since 2014-4-20
 */
public final class CpIndex<T extends CONSTANT_CP_Info> implements ReadWritable {
	private static final Class<?> VALID_TYPE = CONSTANT_CP_Info.class;
	private boolean initialized;
	private Class<T> clas;
	private T cpItem;
	private int index;


	// package-private
	CpIndex(int index) {
		this.index = index;
		this.initialized = false;
	}


	// package-private
	CpIndex(Class<T> clas, int index) {
		if(clas == null) { throw new NullPointerException("constant pool item class type cannot be null"); }
		this.index = index;
		initialize(clas);
	}


	// package-private
	CpIndex(T obj, Class<T> clas, int index) {
		if(!clas.isInstance(obj)) {
			throw new IllegalArgumentException(obj.getClass() + " constant pool item is not a valid subtype of the class " + clas);
		}
		this.index = index;
		initialize(clas);
		if(clas.isInstance(obj)) {
			this.cpItem = obj;
		}
		else {
			throw new ClassCastException("this constant pool item's type is " + clas + ", cannot hold object of type " + obj);
		}
	}


	boolean isInitialized() {
		return initialized;
	}


	final void initialize(Class<T> clas) {
		if(initialized == true) {
			throw new IllegalStateException("cannot reinitialize CpIndex");
		}
		if(clas == null) { throw new NullPointerException("constant pool item class type cannot be null"); }

		if(!VALID_TYPE.isAssignableFrom(clas)) {
			throw new ClassCastException(clas + " is not a valid constant pool item, must be a subtype of " + VALID_TYPE);
		}

		this.clas = clas;
		this.initialized = true;
	}


	public void setCpObject(T obj) {
		if(!isInitialized()) {
			if(!(obj instanceof CONSTANT_CP_Info)) {
				throw new IllegalArgumentException(obj.getClass() + " is not a valid constant pool item type, object must be instance of " + VALID_TYPE);
			}
			@SuppressWarnings("unchecked")
			Class<T> objClass = (Class<T>)obj.getClass();
			initialize(objClass);
		}
		if(clas.isInstance(obj)) {
			this.cpItem = obj;
		}
		else {
			throw new ClassCastException("this constant pool item's type is " + clas + ", cannot hold object of type " + obj);
		}
	}


	public int getIndex() {
		return index;
	}


	public T getCpObject() {
		return cpItem;
	}


	@Override
	public void changeCpIndex(short oldIndex, short newIndex) {
		this.index = IndexUtility.indexChange((short)this.index, oldIndex, newIndex);
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		out.writeShort(index);
	}


	@Override
	public void readData(DataInput in) throws IOException {
		this.index = in.readShort();
	}


	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + index;
		return result;
	}


	@Override
	public String toString() {
		return "CpIndex(index=" + index + ", value=" + getCpObject() + ")";
	}

}
