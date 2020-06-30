package twg2.jbcm.classFormat.constantPool;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.Settings;

/** Java class file format constant pool <code>UTF-8 string</code> info type.<br>
 * Constant value = 1, class version = 45.3, Java SE = 1.0.2
 * @author TeamworkGuy2
 * @since 2013-7-7
 */
public class CONSTANT_Utf8 implements CONSTANT_CP_Info {
	public static final byte TAG = 1;
	ClassFile resolver;

	/** The value of the length item gives the number of bytes in the bytes array (not the length of the resulting string).
	 * The strings in the CONSTANT_Utf8_info structure are not null-terminated.
	 */
	short length; // in bytes
	/** The bytes array contains the bytes of the string. No byte may have the value (byte)0. No byte may lie in the
	 * range {@code (byte)0xf0} to {@code (byte)0xff}.<br>
	 * 1 byte characters: 0xxxxxxx ('\u0001' to '\u007F')<br>
	 * 2 byte characters: (x)110xxxxx (y)10xxxxxx ('\u0000' and '\u0080' to '\u07FF') ((x&0x1f)<<6)+(y&03f)<br>
	 * 3 byte characters: (x)1110xxxx (y)10xxxxxx (z)10xxxxxx ((x & 0xf) << 12) + ((y & 0x3f) << 6) + (z & 0x3f)<br>
	 * stored in big-endian order in class file
	 */
	byte[] bytes;
	/** BaseType Character 	Type 	Interpretation
	 * B 	byte 	signed byte
	 * C 	char 	Unicode character
	 * D 	double 	double-precision floating-point value
	 * F 	float 	single-precision floating-point value
	 * I 	int 	integer
	 * J 	long 	long integer
	 * L<classname>; 	reference 	an instance of class <classname>
	 * S 	short 	signed short
	 * Z 	boolean 	true or false
	 * [ 	reference 	one array dimension 
	 */
	String str;


	public CONSTANT_Utf8(ClassFile resolver) {
		this.resolver = resolver;
	}


	@Override
	public void changeCpIndex(short oldIndex, short newIndex) {
	}


	/**
	 * @return this utf-8 string literal
	 */
	public String getString() {
		return str;
	}


	public void setString(String str) {
		char[] chars = str.toCharArray();
		int size = chars.length;
		int byteCount = 0;
		// Count bytes in string
		for(int i = 0; i < size; i++) {
			if(chars[i] < 0x0080 && chars[i] > 0x0000) {
				byteCount++;
			}
			else if(chars[i] < 0x0800 && chars[i] > 0x007F || chars[i] == 0x0000) {
				byteCount+=2;
			}
			else if(chars[i] > 0x07FF) {
				byteCount+=3;
			}
			else {
				throw new IllegalStateException("Unrecognized char while traversing string: " + chars[i]);
			}
		}
		byte[] bytes = new byte[byteCount];
		// Convert string to bytes
		for(int i = 0, a = 0; a < size; a++) {
			if(chars[i] < 0x0080 && chars[i] > 0x0000) {
				bytes[i] = (byte)(chars[a] & 0x7F);
				i++;
			}
			else if(chars[i] < 0x0800 && chars[i] > 0x007F || chars[i] == 0x0000) {
				bytes[i+0] = (byte)(0xC0 | (chars[a] >>> 6) & 0x1F);
				bytes[i+1] = (byte)(0x80 | chars[a] & 0x3F);
				i+=2;
			}
			else if(chars[i] > 0x07FF) {
				bytes[i+0] = (byte)(0xE0 | (chars[a] >>> 12) & 0x0F);
				bytes[i+1] = (byte)(0x80 | (chars[a] >>> 6) & 0x3F);
				bytes[i+2] = (byte)(0x80 | chars[a] & 0x3F);
				i+=3;
			}
			else {
				throw new IllegalStateException("Unrecognized char while converting string: " + chars[a]);
			}
		}
		this.bytes = bytes;
		this.str = str;
		this.length = (short)bytes.length;
	}


	@Override
	public int getTag() {
		return TAG;
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		out.writeByte(TAG);
		out.writeShort(length);
		out.write(bytes, 0, length);
	}


	@Override
	public void readData(DataInput in) throws IOException {
		if(!Settings.cpTagRead) {
			int tagV = in.readByte();
			if(tagV != TAG) { throw new IllegalStateException("Illegal CONSTANT_Utf8 tag: " + tagV); }
		}
		length = in.readShort();
		bytes = new byte[length];
		in.readFully(bytes, 0, length);
		// Parse the byte array into a string (assume average character length of 2 bytes)
		StringBuilder strBuilder = new StringBuilder(length >>> 1);
		for(int i = 0; i < length; ) {
			if((bytes[i] & 0x80) == 0x00) {
				strBuilder.append((char)bytes[i]);
				i++;
			}
			else if((bytes[i] & 0xE0) == 0xC0) {
				strBuilder.append((char)((bytes[i] & 0x1f) << 6) + (bytes[i+1] & 0x3f));
				i+=2;
			}
			else if((bytes[i] & 0xF0) == 0xE0) {
				strBuilder.append((char)((bytes[i] & 0xf) << 12) + ((bytes[i+1] & 0x3f) << 6) + (bytes[i+2] & 0x3f));
				i+=3;
			}
			else {
				throw new IllegalStateException("Unrecognized CONSTANT_Utf8 byte while parsing string: " + bytes[i]);
			}
		}
		str = strBuilder.toString();
	}


	/**
	 * @return the string literal
	 */
	@Override
	public String toShortString() {
		return str;
	}


	@Override
	public String toString() {
		//return "CONSTANT_Utf8(1, " + length + ", " + str + ")";
		return "\"" + str + "\"";
	}

}
