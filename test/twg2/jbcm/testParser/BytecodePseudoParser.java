package twg2.jbcm.testParser;

import java.util.Arrays;

import twg2.jbcm.Opcodes;

/**
 * @author TeamworkGuy2
 * @since 2020-07-13
 */
public class BytecodePseudoParser {

	/** Parse a string in the format: <pre>{@code
	 * OPCODE [OPERAND_INT/HEX] [OPCODE [OPERAND_INT/HEX]...]
	 * }</pre>
	 * Example:<br>
	 * {@code ICONST_2 ISTORE_1 BIPUSH 6 ISTORE_2 ILOAD_1 ILOAD_2 IMUL ISTORE_3 IINC 3 1 RETURN}<br>
	 * Roughly equivalent to:<br>
	 * {@code int x = 2; int y = 6; int r = x * y; r++;}
	 * @param src
	 * @return
	 */
	public static byte[] parse(String src) {
		int codeLen = 1000;
		byte[] code = new byte[codeLen];
		int ci = 0;
		int len = src.length();
		int i = 0;
		while(i < len) {
			// extract next token from string
			int nextSpace = src.indexOf(' ', i);
			nextSpace = nextSpace >= 0 ? nextSpace : len;
			String token = src.substring(i, nextSpace);
			// if it is a number - parse it
			if(isDigit(token.charAt(0))) {
				int op = token.length() > 2 && Character.toUpperCase(token.charAt(2)) == 'X' ? Integer.parseInt(token, 16) : Integer.parseInt(token);
				if(op > 255 || op < 0) throw new IllegalArgumentException("instruction operand must be a byte value [0, 255]");
				code[ci++] = (byte)(op & 0xFF);
			}
			// else it better be an instruction - parse it
			else {
				Opcodes resOpcode = Enum.valueOf(Opcodes.class, token.toUpperCase());
				byte bc = (byte)resOpcode.opcode();
				code[ci++] = bc;
			}

			// expand byte code array if necessary
			if(ci >= codeLen) {
				codeLen += 1000;
				code = Arrays.copyOf(code, codeLen);
			}

			i += token.length() + 1;
		}

		return Arrays.copyOf(code, ci);
	}


	private static boolean isDigit(char ch) {
		return ch >= '0' && ch <= '9';
	}

}
