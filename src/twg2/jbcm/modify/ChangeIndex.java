package twg2.jbcm.modify;

import twg2.jbcm.CodeUtility;
import twg2.jbcm.IoUtility;

/** Implementation of {@link CodeCpIndexChanger} and {@link CodeOffsetChanger}.<br>
 * {@link #shiftIndex(byte[], int, int)} method calls {@code IoUtility.shiftOffset(...)}
 * using the parameters from this object's constructor.<br>
 * {@link #changeCpIndexIf(byte[], int, int, int)} method calls {@code IoUtility.read*()} and then conditionally
 * calls {@code IoUtility.write*()} if the index matches the current target index.
 * @author TeamworkGuy2
 * @since 2014-4-20
 */
public class ChangeIndex implements CodeCpIndexChanger, CodeOffsetGetter, CodeOffsetChanger {
	/** The offset at which the offset is stored relative to the instructions (this is always 1 for all Java opcodes as of Java 16) */
	final int offsetFromInstruction;
	/** The offset value storage size in bytes, must be 1, 2, or 4. Could represent a constant pool index or a code[] offset. */
	final int offsetStorageSize;


	public ChangeIndex(int offsetFromInstruction, int storageSize) {
		if(storageSize != 1 && storageSize != 2 && storageSize != 4) {
			throw new IllegalArgumentException("cannot shift offsets values that are not 1, 2, or 4 bytes long: " + storageSize);
		}
		this.offsetFromInstruction = offsetFromInstruction;
		this.offsetStorageSize = storageSize;
	}


	@Override
	public int getOffset(byte[] code, int location) {
		location += offsetFromInstruction;
		if(offsetStorageSize == 2) {
			short offset = IoUtility.readShort(code, location);
			return offset;
		}
		else if(offsetStorageSize == 4) {
			int offset = IoUtility.readInt(code, location);
			return offset;
		}
		else if(offsetStorageSize == 1) {
			byte offset = code[location];
			return offset;
		}
		else {
			throw new IllegalStateException("offset length must be 1, 2, or 4");
		}
	}


	@Override
	public void shiftIndex(byte[] code, int location, int offset) {
		if(offsetStorageSize == 2) {
			CodeUtility.shift2Offset(offset, offsetFromInstruction, code, location);
		}
		else if(offsetStorageSize == 4) {
			CodeUtility.shift4Offset(offset, offsetFromInstruction, code, location);
		}
		else if(offsetStorageSize == 1) {
			CodeUtility.shift1Offset(offset, offsetFromInstruction, code, location);
		}
		else {
			throw new IllegalStateException("offset length must be 1, 2, or 4");
		}
	}


	@Override
	public void changeCpIndexIf(byte[] code, int location, int currentIndex, int newIndex) {
		location += offsetFromInstruction;
		if(offsetStorageSize == 2) {
			short index = IoUtility.readShort(code, location);
			if(index == currentIndex) {
				IoUtility.writeShort((short)newIndex, code, location);
			}
		}
		else if(offsetStorageSize == 4) {
			int index = IoUtility.readInt(code, location);
			if(index == currentIndex) {
				IoUtility.writeInt(newIndex, code, location);
			}
		}
		else if(offsetStorageSize == 1) {
			byte index = code[location];
			if(index == currentIndex) {
				code[location] = (byte)newIndex;
			}
		}
		else {
			throw new IllegalStateException("offset length must be 1, 2, or 4");
		}
	}
}