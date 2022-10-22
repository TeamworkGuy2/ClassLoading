package twg2.jbcm.toSource.structures;

import twg2.jbcm.Opcodes;
import twg2.jbcm.ir.JumpConditionInfo;
import twg2.jbcm.ir.OperandInfo;
import twg2.jbcm.toSource.StringBuilderIndent;

/**
 * @author TeamworkGuy2
 * @since 2022-10-01
 */
public class CodeStructureLoop implements CodeEmitter {
	final JumpConditionInfo condition;
	final int startIndex;
	final OperandInfo lhs;
	final String rhs;


	public CodeStructureLoop(JumpConditionInfo condition, int startIndex, OperandInfo lhs, String rhs) {
		this.condition = condition;
		this.startIndex = startIndex;
		this.lhs = lhs;
		this.rhs = rhs;
	}


	@Override
	public EmitterResponse emitOpcode(Opcodes opc, byte[] instr, int idx, int operand, StringBuilderIndent src) {
		// end loops based on ending instruction index tracked when jump instructions (IF_* and GOTO) are first encountered
		if(idx >= condition.getUpperIndex()) {
			src.dedent();
			try {
				src.appendIndent().append('}').append(" // end loop-" + condition).append('\n');
			} catch(Exception ex) {
				throw ex;
			}
			// done - deregister this emitter
			return EmitterResponse.DEREGISTER;
		}

		if(idx == startIndex) {
			// if-statement (loops start with GOTO, handled elsewhere)
			try {
				src.appendIndent().append("while(")
					.append(lhs.getExpression()).append(' ').append(opc.getComparisonSymbolInverse()).append(' ').append(rhs)
					.append(") {")
					.append('\n');
				src.indent();
			} catch(Exception ex) {
				throw ex;
			}
		}
		return EmitterResponse.CONTINUE;
	}


	public static CodeStructureLoop create(JumpConditionInfo condition, OperandInfo lhs, String rhs) {
		int startIndex = condition.getTargetIndex();
		if(condition.potentialIfIndex >= 0) {
			startIndex = condition.potentialIfIndex;
		}
		return new CodeStructureLoop(condition, startIndex, lhs, rhs);
	}
}
