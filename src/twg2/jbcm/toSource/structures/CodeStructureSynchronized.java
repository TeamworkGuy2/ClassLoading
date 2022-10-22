package twg2.jbcm.toSource.structures;

import twg2.jbcm.Opcodes;
import twg2.jbcm.ir.OperandInfo;
import twg2.jbcm.toSource.StringBuilderIndent;

/** A <code>synchronized(...) { ... }</code> block source code emitter.
 * @author TeamworkGuy2
 * @since 2022-10-09
 */
public class CodeStructureSynchronized implements CodeEmitter {
	final int monitorEnterIndex;
	final OperandInfo synchronizeObj;


	public CodeStructureSynchronized(int monitorEnterIndex, OperandInfo operand) {
		this.monitorEnterIndex = monitorEnterIndex;
		this.synchronizeObj = operand;
	}


	@Override
	public EmitterResponse emitOpcode(Opcodes opc, byte[] instr, int idx, int operand, StringBuilderIndent src) {
		// end synchronized block
		if(opc == Opcodes.MONITOREXIT) {
			src.dedent();
			src.appendIndent().append('}').append(" // end synchronized of (" + synchronizeObj.getExpression() + ")").append('\n');
			return EmitterResponse.DEREGISTER;
		}

		if(idx == monitorEnterIndex) {
			src.appendIndent().append("synchronized(")
				.append(synchronizeObj.getExpression())
				.append(") {")
				.append('\n');
			src.indent();
		}
		return EmitterResponse.CONTINUE;
	}


	public static CodeStructureSynchronized create(int monitorEnterIndex, OperandInfo operand) {
		return new CodeStructureSynchronized(monitorEnterIndex, operand);
	}
}
