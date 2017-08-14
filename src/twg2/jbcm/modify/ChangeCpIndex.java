package twg2.jbcm.modify;

import twg2.jbcm.IoUtility;

/** A default implementation of {@link CpIndexChanger}.
 * The {@link #shiftIndex(byte[], int, int)} method calls {@code IoUtility.shiftOffset(...)}
 * using the parameters from this object's constructor and the {@code shiftCpIndex} method call.
 * @author TeamworkGuy2
 * @since 2014-4-20
 */
public class ChangeCpIndex implements CpIndexChanger, CodeOffsetChanger {
	private final int opcode;
	private final int offsetOffset;
	private final int offsetLen;

	public ChangeCpIndex(int opcode, int offsetOffset, int offsetLen) {
		if(offsetLen != 1 && offsetLen != 2 && offsetLen != 4) {
			throw new IllegalArgumentException("cannot shift offsets values that are not 1, 2, or 4 bytes long: " + offsetLen);
		}
		this.opcode = opcode;
		this.offsetOffset = offsetOffset;
		this.offsetLen = offsetLen;
	}

	@Override
	public void shiftIndex(byte[] code, int location, int offset) {
		if(offsetLen == 2) {
			IoUtility.shift2Offset(opcode, offset, offsetOffset, code, location);
		}
		else if(offsetLen == 4) {
			IoUtility.shift4Offset(opcode, offset, offsetOffset, code, location);
		}
		else if(offsetLen == 1) {
			IoUtility.shift1Offset(opcode, offset, offsetOffset, code, location);
		}
		else {
			throw new IllegalStateException("offset length must be 1, 2, or 4");
		}
	}

	@Override
	public void changeCpIndexIf(byte[] code, int location, int currentIndex, int newIndex) {
		location+=offsetOffset;
		if(offsetLen == 2) {
			short index = IoUtility.readShort(code, location);
			if(index == currentIndex) {
				IoUtility.writeShort((short)newIndex, code, location);
			}
		}
		else if(offsetLen == 4) {
			int index = IoUtility.readInt(code, location);
			if(index == currentIndex) {
				IoUtility.writeInt(newIndex, code, location);
			}
		}
		else if(offsetLen == 1) {
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