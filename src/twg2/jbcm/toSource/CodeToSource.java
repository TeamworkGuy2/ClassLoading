package twg2.jbcm.toSource;

import twg2.jbcm.MethodStack;
import twg2.jbcm.Opcodes;
import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.Method_Info;
import twg2.jbcm.classFormat.attributes.Code;

/**
 * @author TeamworkGuy2
 * @since 2020-06-09
 */
public class CodeToSource {

	/**
	 * @param cls the {@link ClassFile} containing the method
	 * @param method the {@link Method_Info} to generate code from
	 * @param methodVars the method variables used map (contains parameter names at the point this method is called).
	 * The number associated with each name is the highest numbered use of that variable name (e.g. 'int1', 'int2' variables in a method would be recorded as 'int', '2')
	 * @param dst the {@link SourceWriter} to write the generated code to
	 */
	public static void toSource(ClassFile cls, Method_Info method, MethodStack methodStack, String tab, SourceWriter dst) {
		// TODO working on code generator

		//method.getCode().toClassString("  ", dst.src);

		Code code = method.getCode();
		byte[] instr = code.getCode();
		int ptr = 0;

		StringBuilder str = dst.src;
		str.append(" // stack: ").append(code.getMaxStack())
			.append(", locals: ").append(code.getMaxLocals()).append(",\n").append(tab)
			.append("code: ").append(instr.length).append(" [\n");

		for(int i = 0, instrCnt = instr.length; i < instrCnt; i++) {
			Opcodes opc = Opcodes.get(instr[i] & 0xFF);
			boolean isWide = false;
			int numOperands = opc.getOperandCount();
			// Read following bytes of code and convert them to an operand depending on the current instruction
			int operand = loadOperands(numOperands, instr, i);
			// Special handling for instructions with unpredictable byte code lengths
			if(numOperands == Opcodes.Const.UNPREDICTABLE) {
				if(Opcodes.WIDE.is(instr[i])) {
					// TODO doesn't properly read extra wide operand bytes
					isWide = true;
					i++; // because wide instructions are wrapped around other instructions
					opc = Opcodes.get(instr[i] & 0xFF);
					numOperands = opc.getOperandCount();
				}
				else if(Opcodes.TABLESWITCH.is(instr[i])) {
					throw new IllegalStateException("tableswitch code handling not implemented");
				}
				else if(Opcodes.LOOKUPSWITCH.is(instr[i])) {
					throw new IllegalStateException("lookupswitch code handling not implemented");
				}
			}

			str.append(tab).append(isWide ? "WIDE " : "").append(opc.displayName());

			if(operand > 0) {
				if(opc.hasBehavior(Opcodes.Type.CP_INDEX) && operand < cls.getConstantPoolCount()) {
					str.append(' ').append(cls.getCpIndex((short)operand).getCpObject().toShortString()).append(" [").append(operand).append(']');
				}
				else if(opc.hasBehavior(Opcodes.Type.JUMP)) {
					str.append(' ').append(i + operand);
				}
				//else if(opc.hasBehavior(Opcodes.Type.VAR_STORE)) { ... }
				//else if(opc.hasBehavior(Opcodes.Type.VAR_LOAD)) { ... }
				//else if(opc.hasBehavior(Opcodes.Type.ARRAY_STORE)) { ... }
				//else if(opc.hasBehavior(Opcodes.Type.ARRAY_LOAD)) { ... }
				else {
					str.append(' ').append(operand).append(" 0x").append(Integer.toHexString(operand));
				}
				str.append("\n");
			}
			else {
				str.append("\n");
			}

			i += (numOperands < 0 ? 0 : numOperands);
		}
		str.append(tab).append("],\n").append(tab);

		var exception_table = code.getExceptionTable();
		str.append("exceptions: ").append(exception_table.length).append(" [");
		if(exception_table.length > 0) { str.append("\n").append(tab); }
		for(int i = 0; i < exception_table.length - 1; i++) {
			str.append(exception_table[i]).append(",\n").append(tab);
		}
		if(exception_table.length > 0) { str.append(exception_table[exception_table.length - 1]); }
		str.append("],\n").append(tab);

		var attributes = code.getAttributes();
		str.append("attributes: ").append(attributes.length).append(" [");
		if(attributes.length > 0) { str.append("\n").append(tab); }
		for(int i = 0; i < attributes.length - 1; i++) {
			str.append(attributes[i]).append(",\n").append(tab);
		}
		if(attributes.length > 0) { str.append(attributes[attributes.length - 1]); }
		str.append("])");

		while(ptr < instr.length) {
			int next = validateInstr(instr, ptr);
			ptr += next;
		}
	}


	private static int loadOperands(int numOperands, byte[] code, int index) {
		return (numOperands > 3 ? (((code[index+1] & 0xFF) << 24) | ((code[index+2] & 0xFF) << 16) | ((code[index+3] & 0xFF) << 8) | (code[index+4] & 0xFF)) :
			(numOperands > 2 ? (((code[index+1] & 0xFF) << 16) | ((code[index+2] & 0xFF) << 8) | (code[index+3] & 0xFF)) :
				(numOperands > 1 ? (((code[index+1] & 0xFF) << 8) | (code[index+2] & 0xFF)) :
					(numOperands > 0 ? ((code[index+1] & 0xFF)) : -1))));
	}


	private static int validateInstr(byte[] instructions, int ptr) {
		return 1;
	}

}
