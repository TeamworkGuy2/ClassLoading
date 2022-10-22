package twg2.jbcm.toSource.structures;

import static twg2.jbcm.CodeUtility.loadOperands;

import twg2.jbcm.CodeFlow;
import twg2.jbcm.Opcodes;
import twg2.jbcm.classFormat.attributes.Code.ExceptionPoint;
import twg2.jbcm.ir.MethodStack;
import twg2.jbcm.ir.OperandInfo;
import twg2.jbcm.modify.TypeUtility;
import twg2.jbcm.toSource.StringBuilderIndent;

/**
 * @author TeamworkGuy2
 * @since 2022-10-09
 */
public class CodeStructureTryCatchFinally implements CodeEmitter {
	final ExceptionPoint exceptionAttr;
	final String exceptionVariableName;
	final MethodStack stack;
	/** 'catch' blocks are generally preceded by an unconditional 'GOTO' jump to skip the catch code in the case of no exception */
	final int jumpIdxAroundHandler;
	/** whether the exception point 'handler' is an ALOAD* instruction */
	final boolean startsWithAload;
	/** the instruction immediately before 'start' is MONITORENTER */
	final boolean monitorEnterBeforeStart;
	/** the last instruction before 'end' is MONITOREXIT */
	final boolean monitorExitAtEnd;
	/** whether the exception point has a null 'catch_type' (i.e. is a 'finally { }' block) */
	final boolean isFinally;


	public CodeStructureTryCatchFinally(
			ExceptionPoint exceptionAttr,
			String exceptionVariableName,
			int jumpIdxAroundHandler,
			boolean startsWithAload,
			boolean monitorEnterBeforeStart,
			boolean monitorExitAtEnd,
			boolean isFinally,
			MethodStack stack) {
		this.exceptionAttr = exceptionAttr;
		this.exceptionVariableName = exceptionVariableName;
		this.stack = stack;
		this.jumpIdxAroundHandler = jumpIdxAroundHandler;
		this.startsWithAload = startsWithAload;
		this.monitorEnterBeforeStart = monitorEnterBeforeStart;
		this.monitorExitAtEnd = monitorExitAtEnd;
		this.isFinally = isFinally;
	}


	@Override
	public EmitterResponse emitOpcode(Opcodes opc, byte[] instr, int idx, int operand, StringBuilderIndent src) {
		// end loops based on ending instruction index tracked when jump instructions (IF_* and GOTO) are first encountered
		if(idx == exceptionAttr.getEndPc()) {
			// TODO need to push exception onto the stack for handler
			var catchType = exceptionAttr.getCatchType();
			String exceptionType = (catchType != null && catchType.getIndex() > 0)
					? TypeUtility.decodeBinaryClassName(catchType.getCpObject())
					: "Throwable";

			src.append(src.indent.toDedent()).append('}').append(" catch(")
				.append(exceptionType).append(' ').append(exceptionVariableName)
				.append(") {").append('\n');

			// push the exception onto the stack like the JVM will during runtime (I think... https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.3)
			stack.addOperand(new OperandInfo(exceptionVariableName, exceptionType, opc));

			if(jumpIdxAroundHandler < 0) {
				// done - deregister this emitter
				return EmitterResponse.DEREGISTER;
			}
		}

		if(idx == jumpIdxAroundHandler) {
			src.dedent();
			src.appendIndent().append('}').append(" // end try-catch of (" + exceptionVariableName + ")").append('\n');
			// done - deregister this emitter
			return EmitterResponse.DEREGISTER;
		}

		if(idx == exceptionAttr.getStartPc()) {
			// if-statement (loops start with GOTO, handled elsewhere)
			try {
				src.appendIndent().append("try {").append('\n');
				src.indent();
			} catch(Exception ex) {
				throw ex;
			}
		}
		return EmitterResponse.CONTINUE;
	}


	public static CodeStructureTryCatchFinally create(byte[] instr, ExceptionPoint exceptionAttr, String exceptionVariableName, MethodStack stack) {
		int firstOpcodeIdxBeforeExBlock = CodeFlow.findLastOpcodeIndex(instr, 0, exceptionAttr.getStartPc());
		boolean monitorEnterBeforeStart = firstOpcodeIdxBeforeExBlock >= 0 && Opcodes.get(instr[firstOpcodeIdxBeforeExBlock]) == Opcodes.MONITORENTER;

		int firstOpcodeIdxBeforeHandler = CodeFlow.findLastOpcodeIndex(instr, 0, exceptionAttr.getHandlerPc());
		int gotoIdxBeforeStart = -1;
		if(firstOpcodeIdxBeforeHandler >= 0 && Opcodes.get(instr[firstOpcodeIdxBeforeHandler]) == Opcodes.GOTO) {
			int numOperands = Opcodes.GOTO.getOperandCount();
			gotoIdxBeforeStart = firstOpcodeIdxBeforeHandler + loadOperands(numOperands, instr, firstOpcodeIdxBeforeHandler);
		}
		boolean startsWithAload = Opcodes.get(instr[exceptionAttr.getHandlerPc()]).hasBehavior(Opcodes.Type.VAR_LOAD);
		int lastExBlockOpcodeIdx = CodeFlow.findLastOpcodeIndex(instr, exceptionAttr.getStartPc(), exceptionAttr.getEndPc());
		boolean monitorExitAtEnd = lastExBlockOpcodeIdx >= 0 && Opcodes.get(instr[lastExBlockOpcodeIdx]) == Opcodes.MONITOREXIT;
		boolean isFinally = exceptionAttr.getCatchType() == null || exceptionAttr.getCatchType().getIndex() == 0;

		// skip generating a try-catch for a 'synchronized (...) { }' block since this is synthetic, automatically generated when the source code is compiled
		if(monitorEnterBeforeStart && monitorExitAtEnd) {
			return null;
		}
		else {
			return new CodeStructureTryCatchFinally(exceptionAttr, exceptionVariableName, gotoIdxBeforeStart, startsWithAload, monitorEnterBeforeStart, monitorExitAtEnd, isFinally, stack);
		}
	}

}
