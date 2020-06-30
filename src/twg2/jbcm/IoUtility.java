package twg2.jbcm;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

import twg2.jbcm.modify.ByteCodeConsumer;
import twg2.jbcm.modify.ChangeCpIndex;
import twg2.jbcm.modify.CpIndexChanger;
import twg2.jbcm.modify.CodeOffsetChanger;

/**
 * @author TeamworkGuy2
 * @since 2014-4-19
 */
public final class IoUtility {

	public static final CpIndexChanger EMPTY_OPCODE_CP_INDEX = new CpIndexChanger() {
		@Override public void shiftIndex(byte[] code, int location, int offset) {
		}

		@Override
		public void changeCpIndexIf(byte[] code, int location, int currentIndex, int newIndex) {
		}
	};
	public static final CodeOffsetChanger EMPTY_OPCODE_OFFSET = new CodeOffsetChanger() {
		@Override public void shiftIndex(byte[] code, int location, int offset) {
		}
	};


	private IoUtility() { throw new AssertionError("cannot instantiate IoUtility"); }


	/** Write the specified long to the specified byte array
	 * @param value the long to write
	 * @param b the byte array to write the long to
	 * @param offset the offset into the array at which to start writing bytes
	 */
	public static void writeLong(long value, byte[] b, int offset) {
		b[offset] = (byte)(value >>> 56);
		b[offset+1] = (byte)(value >>> 48);
		b[offset+2] = (byte)(value >>> 40);
		b[offset+3] = (byte)(value >>> 32);
		b[offset+4] = (byte)(value >>> 24);
		b[offset+5] = (byte)(value >>> 16);
		b[offset+6] = (byte)(value >>> 8);
		b[offset+7] = (byte)(value & 0xFF);
	}


	/** Write the specified int to the specified byte array
	 * @param value the integer to write
	 * @param b the byte array to write the integer to
	 * @param offset the offset into the array at which to start writing bytes
	 */
	public static void writeInt(int value, byte[] b, int offset) {
		b[offset] = (byte)((value >>> 24) & 0xFF);
		b[offset+1] = (byte)((value >>> 16) & 0xFF);
		b[offset+2] = (byte)((value >>> 8) & 0xFF);
		b[offset+3] = (byte)(value & 0xFF);
	}


	/** Write the specified short to the specified byte array
	 * @param value the short to write
	 * @param b the byte array to write the short to
	 * @param offset the offset into the array at which to start writing bytes
	 */
	public static void writeShort(short value, byte[] b, int offset) {
		b[offset] = (byte)((value >>> 8) & 0xFF);
		b[offset+1] = (byte)(value & 0xFF);
	}


	/** Read a long value from the specified location in the specified array
	 * @param b the array to read the short from
	 * @param offset the offset into the array at which to start reading bytes
	 * @return eight bytes read from the indices {@code [offset, offset+3]} and converted to
	 * a long by {@code (b[offset] << 24) | (b[offset+1] << 16) | (b[offset+2] << 8) | b[offset+3]}
	 */
	public static long readLong(byte[] b, int offset) {
		return ((long)b[offset] << 56) |
				((long)(b[offset+1] & 0xFF) << 48) |
				((long)(b[offset+2] & 0xFF) << 40) |
				((long)(b[offset+3] & 0xFF) << 32) |
				((long)(b[offset+4] & 0xFF) << 24) |
				((b[offset+5] & 0xFF) << 16) |
				((b[offset+6] & 0xFF) << 8) |
				(b[offset+7] & 0xFF);
	}


	/** Read an integer value from the specified location in the specified array
	 * @param b the array to read the short from
	 * @param offset the offset into the array at which to start reading bytes
	 * @return four bytes read from the indices {@code [offset, offset+3]} and converted to
	 * an integer by {@code (b[offset] << 24) | (b[offset+1] << 16) | (b[offset+2] << 8) | b[offset+3]}
	 */
	public static int readInt(byte[] b, int offset) {
		return (b[offset] << 24) | (b[offset+1] << 16) | (b[offset+2] << 8) | b[offset+3];
	}


	/** Read a short value from the specified location in the specified array
	 * @param b the array to read the short from
	 * @param offset the offset into the array at which to start reading bytes
	 * @return two bytes read from indices {@code offset} and {@code offset+1} and converted to
	 * a short by {@code (b[offset] << 8) | b[offset+1]}
	 */
	public static short readShort(byte[] b, int offset) {
		return (short)((b[offset] << 8) | b[offset+1]);
	}


	/** Shift the offset values associated with a specific instruction in a chunk of code.
	 * For example, shifting a goto offsets at position 55 by 12 might look like:<br/>
	 * {@code shiftOffset(0xA7, 12, 1, 2, code, 55);}<br/>
	 * Or shifting a goto_w offsets at position 25 by 160:<br/>
	 * {@code shiftOffset(0xC8, 160, 1, 4, code, 25);}
	 * @param offset the instruction code offset to adjust
	 * @param offsetOffset the number of bytes ahead of the opcode at which the offset to adjust starts (1 for an offset that immediately follows an opcode)
	 * @param code the array of code to search through for the opcode
	 * @param codeOffset the offset into the code array at which to update the opcode's offset value
	 * @return the location after the opcode's offset value, calculated as {@code codeOffset + offsetOffset + offsetLen}
	 */
	public static int shift1Offset(int offset, final int offsetOffset, byte[] code, int codeOffset) {
		codeOffset += offsetOffset;
		byte curOffset = code[codeOffset];
		if(curOffset + offset < curOffset) {
			throw new ArithmeticException("byte overflow: " + curOffset + "+" + offset + "=" + (curOffset+offset));
		}
		curOffset += offset;
		code[codeOffset] = curOffset;
		return codeOffset + 1;
	}


	/** Shift the offset values associated with a specific instruction in a chunk of code.
	 * For example, shifting a goto offsets at position 55 by 12 might look like:<br/>
	 * {@code shiftOffset(12, 1, 2, code, 55);}<br/>
	 * Or shifting a goto_w offsets at position 25 by 160:<br/>
	 * {@code shiftOffset(160, 1, 4, code, 25);}
	 * @param offset the instruction code offset to adjust
	 * @param offsetOffset the number of bytes ahead of the opcode at which the offset to adjust starts (1 for an offset that immediately follows an opcode)
	 * @param code the array of code to search through for the opcode
	 * @param codeOffset the offset into the code array at which to update the opcode's offset value
	 * @return the location after the opcode's offset value, calculated as {@code codeOffset + offsetOffset + offsetLen}
	 */
	public static int shift2Offset(int offset, int offsetOffset, byte[] code, int codeOffset) {
		codeOffset += offsetOffset;
		short curOffset = IoUtility.readShort(code, codeOffset);
		if(curOffset + offset < curOffset) {
			throw new ArithmeticException("short overflow: " + curOffset + "+" + offset + "=" + (curOffset+offset));
		}
		curOffset += offset;
		IoUtility.writeShort(curOffset, code, codeOffset);
		return codeOffset + 2;
	}


	/** Shift the offset values associated with a specific instruction in a chunk of code.
	 * For example, shifting a goto offsets at position 55 by 12 might look like:<br/>
	 * {@code shiftOffset(12, 1, 2, code, 55);}<br/>
	 * Or shifting a goto_w offsets at position 25 by 160:<br/>
	 * {@code shiftOffset(160, 1, 4, code, 25);}
	 * @param offset the instruction code offset to adjust
	 * @param offsetOffset the number of bytes ahead of the opcode at which the offset to adjust starts (1 for an offset that immediately follows an opcode)
	 * @param code the array of code to search through for the opcode
	 * @param codeOffset the offset into the code array at which to update the opcode's offset value
	 * @return the location after the opcode's offset value, calculated as {@code codeOffset + offsetOffset + offsetLen}
	 */
	public static int shift4Offset(int offset, int offsetOffset, byte[] code, int codeOffset) {
		codeOffset += offsetOffset;
		int curOffset = IoUtility.readInt(code, codeOffset);
		if(curOffset + offset < curOffset) {
			throw new ArithmeticException("integer overflow: " + curOffset + "+" + offset + "=" + (curOffset+offset));
		}
		curOffset += offset;
		IoUtility.writeInt(curOffset, code, codeOffset);
		return codeOffset + 4;
	}


	/** Call the specified {@code ByteCodeConsumer} for each instruction in the specified code array
	 * @param code the code array
	 * @param offset the offset into the code array at which to start finding instructions
	 * @param length the number of bytes of the code array to check through
	 * @param cbFunc the function to call for each instruction found in specified code array range
	 */
	public static void forEach(byte[] code, int offset, int length, ByteCodeConsumer cbFunc) {
		int numOperands = 0;
		@SuppressWarnings("unused")
		int operand = 0;

		for(int i = offset, size = offset + length; i < size; i++) {
			numOperands = Opcodes.get((code[i] & 0xFF)).getOperandCount();
			// Read following bytes of code and convert them to an operand depending on the number of operands specified for the current command
			operand = loadOperands(numOperands, code, i);
			// Special handling for instructions with unpredictable byte code lengths
			if(numOperands == Opcodes.Const.UNPREDICTABLE) {
				if(Opcodes.WIDE.is(code[i])) {
					cbFunc.accept(Opcodes.get((code[i] & 0xFF)), code, i);
					i++; // because wide operations are nested around other operations 
					numOperands = Opcodes.get((code[i] & 0xFF)).getOperandCount();
				}
				else if(Opcodes.TABLESWITCH.is(code[i])) {
					throw new IllegalStateException("tableswitch code handling not implemented");
				}
				else if(Opcodes.LOOKUPSWITCH.is(code[i])) {
					throw new IllegalStateException("lookupswitch code handling not implemented");
				}
			}
			cbFunc.accept(Opcodes.get((code[i] & 0xFF)), code, i);
			i+= (numOperands < 0) ? 0 : numOperands;
		}
	}


	private static int loadOperands(int numOperands, byte[] code, int index) {
		return (numOperands > 3 ? (((code[index+1] & 0xFF) << 24) | ((code[index+2] & 0xFF) << 16) | ((code[index+3] & 0xFF) << 8) | (code[index+4] & 0xFF)) :
			(numOperands > 2 ? (((code[index+1] & 0xFF) << 16) | ((code[index+2] & 0xFF) << 8) | (code[index+3] & 0xFF)) :
				(numOperands > 1 ? (((code[index+1] & 0xFF) << 8) | (code[index+2] & 0xFF)) :
					(numOperands > 0 ? ((code[index+1] & 0xFF)) : -1))));
	}


	/** A default implementation of {@link CodeOffsetChanger}.
	 * The {@link CodeOffsetChanger#shiftIndex(byte[], int, int)} in this implementation simply calls {@code IoUtility.shiftOffset(...)}
	 * using the parameters from the constructor and the {@link CodeOffsetChanger#shiftIndex(byte[], int, int)} method.
	 * @author TeamworkGuy2
	 * @since 2014-419
	 */
	/*
	public static class ChangeOffsetDefault implements CodeOffsetChanger {
		public static final CodeOffsetChanger EMPTY = new CodeOffsetChanger() {
			@Override public void accept(byte[] code, int location, int offset) {
			}
		};
		private final int opcode;
		private final int offsetOffset;
		private final int offsetLen;

		public ChangeOffsetDefault(int opcode, int offsetOffset, int offsetLen) {
			if(offsetLen != 1 && offsetLen != 2 && offsetLen != 4) {
				throw new IllegalArgumentException("cannot shift offsets values that are not 1, 2, or 4 bytes long: " + offsetLen);
			}
			this.opcode = opcode;
			this.offsetOffset = offsetOffset;
			this.offsetLen = offsetLen;
		}

		/** Accept the specified code location and shift any offset values
		 * @param code the code array containing instructions and offsets to modify
		 * @param location the index into the code array at which start a shift of offset values
		 * @param offset the offset to shift offset values by
		 *
		@Override
		public void accept(byte[] code, int location, int offset) {
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
	}
	*/


	public static CpIndexChanger cpIndex(int offset, int len) {
		return new ChangeCpIndex(offset, len);
	}


	public static CodeOffsetChanger offsetModifier(int offset, int len) {
		return new ChangeCpIndex(offset, len);
	}


	public static final CodeOffsetChanger TableswitchOffsetModifier = new CodeOffsetChanger() {
		/** Add an offset to all of the tableswitch instructions in the
		 * specified chunk of code
		 * @param offset the offset to adjust all tableswitch offsets by.
		 * TODO this must be divisible by 4 at the moment.
		 * @param code the array of instructions to manipulate
		 * TODO return the next opcode location following this instruction
		 */
		@Override public void shiftIndex(byte[] code, int location, int offset) {
			byte op = code[location];
			if(op == 170) {
				location++;
				// Skip padding
				int padding = (location % 4);
				location+=padding;
				// Default 32bit offset
				int defaultOffset = IoUtility.readInt(code, location);
				defaultOffset += offset;
				IoUtility.writeInt(defaultOffset, code, location);
				location += 4;
				// low
				int low = IoUtility.readInt(code, location);
				location += 4;
				// high
				int high = IoUtility.readInt(code, location);
				location += 4;
				// For each jump-offset 32bit value
				for(int ii = 0; ii < (high-low+1); ii++, location += 4) {
					int matchOffset = IoUtility.readInt(code, location);
					matchOffset += offset;
					IoUtility.writeInt(matchOffset, code, location);
				}
			}
			else {
				throw new IllegalStateException("Expected opcode 170 at location " + location + ", found opcode " + code[location] + " instead");
			}
		}
	};


	public static final CodeOffsetChanger LookupswitchOffsetModifier = new CodeOffsetChanger() {
		/** Add an offset to all of the lookupswitch instructions in the
		 * specified chunk of code
		 * @param offset the offset to adjust all lookupswitch offsets by.
		 * TODO this must be divisible by 4 at the moment.
		 * @param code the array of instructions to manipulate
		 * TODO return the next opcode location following this instruction
		 */
		@Override public void shiftIndex(byte[] code, int location, int offset) {
			byte op = code[location];
			if(op == 171) {
				location++;
				// Skip padding
				int padding = (location % 4);
				location+=padding;
				// Default 32bit offset
				int defaultOffset = IoUtility.readInt(code, location);
				defaultOffset += offset;
				IoUtility.writeInt(defaultOffset, code, location);
				location += 4;
				// Number of pairs
				int npairs = IoUtility.readInt(code, location);
				location += 4;
				// For each pair of match-offset 32bit values
				for(int ii = 0; ii < npairs; ii++, location += 8) {
					int matchOffset = IoUtility.readInt(code, location + 4);
					matchOffset += offset;
					IoUtility.writeInt(matchOffset, code, location + 4);
				}
			}
			else {
				throw new IllegalStateException("Expected opcode 171 at location " + location + ", found opcode " + code[location] + " instead");
			}
		}
	};


	public static byte[] loadBytes(File file) throws IOException {
		return loadBytes(file.toURI().toURL());
	}


	public static byte[] loadBytes(URL url) throws IOException {
		byte[] buf = new byte[8192];
		InputStream is = null;
		// Open the URL and load its contents into a byte buffer
		is = url.openStream();
		int read = 0;
		int totalRead = 0;
		// Read each input stream into our byte array buffer
		try {
			while((read = is.read(buf, totalRead, buf.length-totalRead)) > -1) {
				if(totalRead >= buf.length) {
					buf = Arrays.copyOf(buf, ((buf.length+2) << 1));
				}
				totalRead += read;
			}
		} finally {
			if(is != null) { is.close(); }
		}
		// Create a copy of the buffers exact length and add it to the classloader
		byte[] bufN = new byte[totalRead];
		System.arraycopy(buf, 0, bufN, 0, totalRead);
		return bufN;
	}

}
