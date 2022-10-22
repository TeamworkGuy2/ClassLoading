package twg2.jbcm.toSource.structures;

import java.util.ArrayList;

import twg2.jbcm.Opcodes;
import twg2.jbcm.toSource.StringBuilderIndent;

/**
 * @author TeamworkGuy2
 * @since 2022-10-01
 */
public interface CodeEmitter {

	/** Accepts an instruction, processes it, potentially outputs source code,
	 * and returns whether this emitter will accept more code.
	 * @param opc the op-code of the current instruction
	 * @param instr code method/block that the {@code opc} comes from
	 * @param index index into {@code instr} where {@code opc} is located
	 * @param operand the operand data of the current {@code opc} instruction
	 * @param toSourceDst destination to write generated source code to
	 * @return the emitter response type
	 */
	public EmitterResponse emitOpcode(Opcodes opc, byte[] instr, int index, int operand, StringBuilderIndent toSourceDst);


	public static void runEmits(ArrayList<CodeEmitter> codeStructures, Opcodes opc, byte[] instr, int idx, int operand, StringBuilderIndent sourceCodeDst) {
		// loop backward so that nested code structures can process first
		for(int i = codeStructures.size() - 1; i >= 0; i--) {
			var res = codeStructures.get(i).emitOpcode(opc, instr, idx, operand, sourceCodeDst);
			if(res == EmitterResponse.DEREGISTER) {
				codeStructures.remove(i);
			}
			else if(res == EmitterResponse.CONSUMED) {
				break;
			}
		}
	}

}
