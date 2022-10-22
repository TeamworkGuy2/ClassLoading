package twg2.jbcm.toSource.structures;

import twg2.jbcm.Opcodes;
import twg2.jbcm.ir.JumpConditionInfo;
import twg2.jbcm.ir.JumpConditionInfo.UsageHint;
import twg2.jbcm.toSource.StringBuilderIndent;
import twg2.jbcm.ir.OperandInfo;

/**
 * @author TeamworkGuy2
 * @since 2022-10-01
 */
public class CodeStructureIf implements CodeEmitter {
	final JumpConditionInfo condition;
	final int endTargetIndex;
	final OperandInfo lhs;
	final String rhs;


	public CodeStructureIf(JumpConditionInfo condition, int endTargetIndex, OperandInfo lhs, String rhs) {
		this.condition = condition;
		this.endTargetIndex = endTargetIndex;
		this.lhs = lhs;
		this.rhs = rhs;
	}


	@Override
	public EmitterResponse emitOpcode(Opcodes opc, byte[] instr, int idx, int operand, StringBuilderIndent src) {
		// end if-else blocks based on ending instruction index tracked when jump instructions (IF_* and GOTO) are first encountered
		if(idx >= endTargetIndex) {
			// if-statement (loops start with GOTO, handled elsewhere)
			if(condition.getOpcode() != Opcodes.GOTO) {
				src.dedent();
				try {
					src.appendIndent().append('}').append(" // end IF-" + condition).append('\n');
				} catch(Exception ex) {
					throw ex;
				}
			}
			return EmitterResponse.DEREGISTER;
		}

		if(idx == condition.opcIdx) {
			String comparisonSymbol = opc.getComparisonSymbol();
			if(condition.usageHint == UsageHint.IF_WITHIN_LOOP_AND_WITH_LOOP_ENDS && condition.targetOffset < 0) {
				comparisonSymbol = opc.getComparisonSymbolInverse();
			}
			src.appendIndent().append("if(")
				.append(lhs.getExpression()).append(' ').append(comparisonSymbol).append(' ').append(rhs)
				.append(") {")
				.append('\n');
			src.indent();
		}
		return EmitterResponse.CONTINUE;
	}


	public static CodeStructureIf create(JumpConditionInfo condition, OperandInfo lhs, String rhs) {
		int endTargetIndex = condition.getTargetIndex();
		if(condition.usageHint == UsageHint.IF_WITHIN_LOOP_AND_WITH_LOOP_ENDS && condition.targetOffset < 0) {
			endTargetIndex = condition.loopEndIndexForIf;
		}

		return new CodeStructureIf(
				condition,
				endTargetIndex,
				lhs,
				rhs
		);
	}
}
