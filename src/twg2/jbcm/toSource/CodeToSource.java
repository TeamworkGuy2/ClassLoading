package twg2.jbcm.toSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import twg2.jbcm.Opcodes;
import twg2.jbcm.Opcodes.Type;
import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.Method_Info;
import twg2.jbcm.classFormat.attributes.Code;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Class;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Double;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Fieldref;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Float;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Integer;
import twg2.jbcm.classFormat.constantPool.CONSTANT_InvokeDynamic;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Long;
import twg2.jbcm.classFormat.constantPool.CONSTANT_MethodHandle;
import twg2.jbcm.classFormat.constantPool.CONSTANT_MethodType;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Methodref;
import twg2.jbcm.classFormat.constantPool.CONSTANT_String;
import twg2.jbcm.ir.JumpConditionInfo;
import twg2.jbcm.ir.MethodStack;
import twg2.jbcm.ir.OperandInfo;
import twg2.jbcm.ir.SwitchCase;
import twg2.jbcm.ir.VariableInfo;
import twg2.jbcm.modify.TypeUtility;

import static twg2.jbcm.CodeUtility.loadOperands;

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
	public static void toSource(ClassFile cls, Method_Info method, MethodStack methodStack, SourceWriter dst) {
		Code code = method.getCode();
		byte[] instr = code.getCode();
		//BitSet instrIndices = IterateCode.markInstructions(instr);

		var loops = new ArrayList<JumpConditionInfo>(); // track GOTO/IF_* loops detected in the code
		// pairs of values, the first is the case match value the second is the target index
		var switchCases = new ArrayList<SwitchCase>();
		AtomicReference<SwitchCase> switchDefault = new AtomicReference<>();
		int switchEndIdx = -1;
		int conditionUnfinishedIdx = -1;
		String indentMark = dst.indentMark;
		String indentation = dst.getIndent();
		StringBuilder tmp = new StringBuilder();
		var tmpList = new ArrayList<String>();
		var tmpList2 = new ArrayList<String>();
		StringBuilder str = dst.src;

		str.setLength(0);

		str.append(" // stack: ").append(code.getMaxStack())
			.append(", locals: ").append(code.getMaxLocals()).append(",\n").append(indentation)
			.append("code: ").append(instr.length).append(" [\n");

		for(int i = 0, instrCnt = instr.length; i < instrCnt; i++) {
			Opcodes opc = Opcodes.get(instr[i] & 0xFF);
			boolean isWide = false;
			int numOperands = opc.getOperandCount();
			// read following bytes of code and convert them to an operand
			int operand = loadOperands(numOperands, instr, i);

			// end if-else blocks and loops based on ending instruction index tracked when if_* and goto instructions are first encountered
			var curCond = conditionUnfinishedIdx > -1 ? loops.get(conditionUnfinishedIdx) : null;
			while(curCond != null && curCond.getTargetIndex() <= i) {
				// if-statement (loops start with GOTO, handled elsewhere)
				if(curCond.getOpcode() != Opcodes.GOTO && !curCond.isFinished()) {
					curCond.finish();
					indentation = indentation.substring(0, indentation.length() - indentMark.length());
					str.append(indentation).append('}').append('\n');
				}
				conditionUnfinishedIdx--;
				curCond = conditionUnfinishedIdx > -1 ? loops.get(conditionUnfinishedIdx) : null;
			}

			// switch cases
			if(switchDefault.get() != null && switchDefault.get().caseTarget == i) {
				switchDefault.get().finish();
				str.append("default: ").append("// offset ").append(switchDefault.get().caseTarget);
			}
			var switchCaseIdx = SwitchFlow.findSwitchCase(i, switchCases, 0);
			while(switchCaseIdx > -1) {
				var caseObj = switchCases.get(switchCaseIdx);
				caseObj.finish();
				var deIndent = indentation.substring(0, indentation.length() - indentMark.length());
				str.append(deIndent).append("case ").append(caseObj.caseMatch).append(": ").append("// offset ").append(caseObj.caseTarget).append('\n');
				switchCaseIdx = SwitchFlow.findSwitchCase(i, switchCases, switchCaseIdx + 1);
			}
			if(i == switchEndIdx) {
				indentation = indentation.substring(0, indentation.length() - indentMark.length());
				str.append(indentation).append('}').append('\n');
			}

			str.append(indentation).append(isWide ? "WIDE " : "");

			// unpredictable operand length instructions
			if(opc == Opcodes.WIDE) {
				// TODO doesn't properly read extra wide operand bytes
				isWide = true;
				i++; // because wide instructions are wrapped around other instructions
				opc = Opcodes.get(instr[i] & 0xFF);
				numOperands = opc.getOperandCount();
			}
			else if(numOperands == Opcodes.Const.UNPREDICTABLE) {
				if(opc == Opcodes.TABLESWITCH) {
					i += SwitchFlow.loadTableSwitch(i, instr, switchCases, switchDefault);
					var switchIndex = methodStack.popOperand();
					indentation += indentMark;
					str.append("switch(" + switchIndex.getExpression() + ") {");
				}
				else if(opc == Opcodes.LOOKUPSWITCH) {
					i += SwitchFlow.loadLookupSwitch(i, instr, switchCases, switchDefault);
					var switchKey = methodStack.popOperand();
					indentation += indentMark;
					str.append("switch(" + switchKey.getExpression() + ") {");
				}

				// TODO debugging
				var commonEndIdx = SwitchFlow.commonSwitchEndIndex(switchCases, switchDefault.get(), instr);
				switchEndIdx = commonEndIdx;
				//str.append(" // common end: " + commonEndIdx);
				//for(var caseObj : switchCases) {
				//	str.append('\n').append(indentation).append(indentMark).append("// case " + caseObj.caseMatch + ": [" + caseObj.caseTarget + ", " + caseObj.caseEndIdx + "]" + (caseObj.caseEndTarget > 0 ? " jump to " + caseObj.caseEndTarget : ""));
				//	System.out.println("case " + caseObj.caseMatch + ": " + CodeFlow.flowPathToString(instr, caseObj.getCodeFlow()));
				//}
				//str.append('\n').append(indentation).append(indentMark).append("// default: [" + switchDefault.get().caseTarget + ", " + switchDefault.get().caseEndIdx + "]" + (switchDefault.get().caseEndTarget > 0 ? " jump to " + switchDefault.get().caseEndTarget : ""));

				//if(!packed) {
				//	throw new IllegalStateException(opc + " case code ranges are not tightly packed or contain conditional jumps, decompilation not yet supported");
				//}
			}

			if(opc.hasBehavior(Type.CONST_LOAD)) {
				Object constValue = opc.getConstantValue();
				Class<?> constType = constValue != null ? tryGetPrimitiveType(constValue.getClass()) : Object.class;
				methodStack.addOperand(new OperandInfo(constValue.toString(), constType.getName(), opc));
			}
			else if(opc == Opcodes.BIPUSH || opc == Opcodes.SIPUSH) {
				methodStack.addOperand(new OperandInfo(Integer.toString(operand), (opc == Opcodes.BIPUSH ? Byte.TYPE : Short.TYPE).getName(), opc));
			}
			else if(opc == Opcodes.LDC || opc == Opcodes.LDC_W || opc == Opcodes.LDC2_W) {
				var cpVal = cls.getConstantPoolIndex(operand).getCpObject();
				String expr = cpVal.toShortString();
				String exprType = null;
				// TODO not fully implemented or tested
				switch(cpVal.getTag()) {
					case CONSTANT_Float.TAG: exprType = "float"; break;
					case CONSTANT_Integer.TAG: exprType = "int"; break;
					case CONSTANT_Double.TAG: exprType = "double"; break;
					case CONSTANT_Long.TAG: exprType = "long"; break;
					case CONSTANT_String.TAG: exprType = "String"; expr = '"' + expr + '"'; break;
					case CONSTANT_MethodType.TAG: {
						var cpm = (CONSTANT_MethodType)cpVal;
						exprType = cpm.getDescriptor().getString();
						break;
					}
					case CONSTANT_MethodHandle.TAG: {
						var cpm = (CONSTANT_MethodHandle)cpVal;
						exprType = cpm.getReference().toShortString();
					}
					default:
						throw new IllegalStateException("unknown ldc CP entry type " + cpVal);
				}
				methodStack.addOperand(new OperandInfo(expr, exprType, opc));
			}
			else if(opc.hasBehavior(Type.VAR_LOAD)) {
				int localVarIdx = numOperands > 0 ? loadOperands(numOperands, instr, i) : ((Number)opc.getConstantValue()).intValue();
				var value = methodStack.getVariable(localVarIdx);
				methodStack.addOperand(new OperandInfo(value, opc));
			}
			else if(opc.hasBehavior(Type.ARRAY_LOAD)) {
				var index = methodStack.popOperand();
				var arrayref = methodStack.popOperand();
				String componentType = TypeUtility.arrayComponentType(arrayref.getExpressionFullType());
				methodStack.addOperand(new OperandInfo(arrayref.getExpression() + '[' + index.getExpression() + ']', componentType, opc));
			}
			else if(opc.hasBehavior(Type.VAR_STORE)) {
				int localVarIdx = numOperands > 0 ? loadOperands(numOperands, instr, i) : ((Number)opc.getConstantValue()).intValue();
				setVariable(methodStack, localVarIdx, methodStack.popOperand(), method, str);
			}
			else if(opc.hasBehavior(Type.ARRAY_STORE)) {
				var value = methodStack.popOperand();
				var index = methodStack.popOperand();
				var arrayref = methodStack.popOperand();
				str.append(arrayref.getExpression()).append('[').append(index.getExpression()).append(']').append(" = ").append(value.getExpression()).append(';');
			}
			else if(opc == Opcodes.POP) {
				methodStack.popOperand();
			}
			else if(opc == Opcodes.POP2) {
				methodStack.popOperand();
				methodStack.popOperand();
			}
			else if(opc.hasBehavior(Type.STACK_MANIPULATE)) {
				// TODO DUP instructions are referenced multiple times, so if the current expression is compound it needs to become a new temp variable
				if(opc == Opcodes.DUP) {
					// TODO Correct ??? DUP     (89, 0, enums(Type.STACK_MANIPULATE), null), // Duplicate the top operand stack value
					var op = methodStack.popOperand();
					var opCopy = op.copy();
					methodStack.addOperand(op);
					methodStack.addOperand(opCopy);
				}
				// TODO
				//DUP_X1  (90, 0, enums(Type.STACK_MANIPULATE), null), // Duplicate the top operand stack value and insert two values down
				//DUP_X2  (91, 0, enums(Type.STACK_MANIPULATE), null), // Duplicate the top operand stack value and insert two or three values down
				//DUP2    (92, 0, enums(Type.STACK_MANIPULATE), null), // Duplicate the top one or two operand stack values
				//DUP2_X1 (93, 0, enums(Type.STACK_MANIPULATE), null), // Duplicate the top one or two operand stack values and insert two or three values down
				//DUP2_X2 (94, 0, enums(Type.STACK_MANIPULATE), null), // Duplicate the top one or two operand stack values and insert two, three, or four values down
				//SWAP    (95, 0, enums(Type.STACK_MANIPULATE), null), // Swap the top two operand stack values
			}
			else if(opc.hasBehavior(Type.MATH_OP)) {
				if(opc.hasBehavior(Type.POP1)) {
					var opVar = methodStack.popOperand();
					methodStack.addOperand(new OperandInfo(opc.getMathSymbol() + opVar.getExpression(), opVar.getExpressionFullType(), opc));
				}
				else if(opc.hasBehavior(Type.POP2)) {
					var rightVar = methodStack.popOperand();
					var leftVar = methodStack.popOperand();
					methodStack.addOperand(new OperandInfo(leftVar.getExpression() + ' ' + opc.getMathSymbol() + ' ' + rightVar.getExpression(), leftVar.getExpressionFullType(), opc));
				}
			}
			else if(opc == Opcodes.IINC) {
				int index = (operand >> 8) & 0xFF;
				var varref = methodStack.getVariable(index);
				int increment = operand & 0xFF;
				str.append(varref.getName()).append(increment == 1 ? "++" : " += " + increment);
			}
			else if(opc.hasBehavior(Type.TYPE_CONVERT)) {
				// TODO
			}
			else if(opc.hasBehavior(Type.COMPARE_NUMERIC)) {
				// TODO
			}
			if(opc.hasBehavior(Type.CONDITION)) {
				OperandInfo lhs = null;
				String rhs = null;

				if(opc.hasBehavior(Type.POP1)) {
					lhs = methodStack.popOperand();

					if(opc == Opcodes.IFNULL || opc == Opcodes.IFNONNULL) {
						rhs = "null";
					}
					else {
						if(opc == Opcodes.IFEQ || opc == Opcodes.IFNE || opc == Opcodes.IFLT || opc == Opcodes.IFGE || opc == Opcodes.IFGT || opc == Opcodes.IFLE) {
						}
						rhs = "0";
					}
				}
				else {
					var val2 = methodStack.popOperand();
					var val1 = methodStack.popOperand();

					lhs = val1;
					rhs = val2.getExpression();

					if(opc == Opcodes.IF_ICMPEQ || opc == Opcodes.IF_ICMPNE || opc == Opcodes.IF_ICMPLT || opc == Opcodes.IF_ICMPGT || opc == Opcodes.IF_ICMPLE || opc == Opcodes.IF_ICMPGE ||
							opc == Opcodes.IF_ACMPEQ || opc == Opcodes.IF_ACMPNE) {
					}
				}

				// standard if-statement
				if((short)operand > 0) {
					loops.add(new JumpConditionInfo(opc, i, (short)operand));
					conditionUnfinishedIdx = loops.size() - 1;
					indentation += indentMark;
					tmp.setLength(0);
					tmp.append(lhs.getExpression()).append(' ').append(opc.getComparisonSymbolInverse()).append(' ').append(rhs);
					str.append("if(").append(tmp).append(") {");
				}
				// loop with if_* at the end and jump backward
				else {
					int loopIdx = findLoopStart(i, (short)operand, loops);

					if(loopIdx == -1) {
						throw new IllegalStateException("if_* jumps backward but no loop start point found at " + i);
					}

					if(conditionUnfinishedIdx == loopIdx) {
						conditionUnfinishedIdx--;
					}
					loops.get(loopIdx).finish();
					indentation = indentation.substring(0, indentation.length() - indentMark.length());
					tmp.setLength(0);
					tmp.append(lhs.getExpression()).append(' ').append(opc.getComparisonSymbol()).append(' ').append(rhs);
					str.append("} while(").append(tmp).append(");");
				}
			}
			else if(opc == Opcodes.GOTO) {
				int loopIdx = findLoopStart(i, (short)operand, loops);

				// standard if-else statement
				if(loopIdx > -1) {
					if(conditionUnfinishedIdx == loopIdx) {
						conditionUnfinishedIdx--;
					}
					loops.get(loopIdx).finish();
					indentation = indentation.substring(0, indentation.length() - indentMark.length());
					str.append('}');
				}
				else {
					loops.add(new JumpConditionInfo(opc, i, (short)operand));
					conditionUnfinishedIdx = loops.size() - 1;
					indentation += indentMark;
					str.append("do {");
				}
			}
			else if(opc == Opcodes.JSR || opc == Opcodes.GOTO_W || opc == Opcodes.JSR_W) {
				// TODO: JSR, GOTO_W, & JSR_W
			}
			else if(opc == Opcodes.RET) {
				// TODO
			}
			else if(opc.hasBehavior(Type.RETURN)) {
				str.append("return");
				if(opc != Opcodes.RETURN) {
					var retVal = methodStack.popOperand();
					str.append(' ').append(retVal.getExpression());
				}
				str.append(';');
			}
			else if(opc == Opcodes.GETSTATIC) {
				var fieldRef = (CONSTANT_Fieldref)cls.getConstantPoolIndex(operand).getCpObject();
				tmp.setLength(0);
				TypeUtility.typeDescriptorToSource(fieldRef.getNameAndType().getDescriptor().getString(), tmp);
				var fieldType = tmp.toString();

				var fieldClass = fieldRef.getClassType();
				var fieldNameAndType = fieldRef.getNameAndType();
				var fieldStr = fieldClass.getName().getString() + "." + fieldNameAndType.getName().getString();

				methodStack.addOperand(new OperandInfo(fieldStr, fieldType, opc));
			}
			else if(opc == Opcodes.PUTSTATIC) {
				var val = methodStack.popOperand();
				var fieldRef = (CONSTANT_Fieldref)cls.getConstantPoolIndex(operand).getCpObject();
				var fieldClass = fieldRef.getClassType();
				var fieldNt = fieldRef.getNameAndType();

				str.append(fieldClass.getName().getString()).append('.').append(fieldNt.getName().getString()).append(" = ").append(val.getExpression());
			}
			else if(opc == Opcodes.GETFIELD) {
				var objRef = methodStack.popOperand();
				var fieldRef = (CONSTANT_Fieldref)cls.getConstantPoolIndex(operand).getCpObject();
				tmp.setLength(0);
				TypeUtility.typeDescriptorToSource(fieldRef.getNameAndType().getDescriptor().getString(), tmp);
				var fieldType = tmp.toString();

				var fieldNt = fieldRef.getNameAndType();

				methodStack.addOperand(new OperandInfo(objRef.getExpression() + "." + fieldNt.getName().getString(), fieldType, opc));
			}
			else if(opc == Opcodes.PUTFIELD) {
				var val = methodStack.popOperand();
				var objRef = methodStack.popOperand();
				var fieldRef = (CONSTANT_Fieldref)cls.getConstantPoolIndex(operand).getCpObject();
				var fieldNt = fieldRef.getNameAndType();

				str.append(objRef.getExpression()).append('.').append(fieldNt.getName().getString()).append(" = ").append(val.getExpression());
			}
			else if(opc.hasBehavior(Type.CP_INDEX) && opc.hasBehavior(Type.POP_UNPREDICTABLE)) {
				if(opc == Opcodes.INVOKEVIRTUAL) {
					var methodRef = (CONSTANT_Methodref)cls.getConstantPoolIndex(operand).getCpObject();
					extractMethodTypesAndExpressions(methodRef, tmpList, tmpList2, tmp, methodStack);
					String returnType = tmp.toString();
					var objRef = methodStack.popOperand();
					methodCallExpression(methodRef, objRef.getExpression(), tmpList, tmpList2, returnType, methodStack, opc, tmp, str);
				}
				else if(opc == Opcodes.INVOKESTATIC) {
					var methodRef = (CONSTANT_Methodref)cls.getConstantPoolIndex(operand).getCpObject();
					var staticClass = methodRef.getClassType().getName().getString();
					extractMethodTypesAndExpressions(methodRef, tmpList, tmpList2, tmp, methodStack);
					String returnType = tmp.toString();
					methodCallExpression(methodRef, staticClass, tmpList, tmpList2, returnType, methodStack, opc, tmp, str);
				}
				else if(opc == Opcodes.INVOKEDYNAMIC) {
					Opcodes nextOpc = i + (numOperands < 0 ? 0 : numOperands) + 1 < instrCnt ? Opcodes.get(instr[i + (numOperands < 0 ? 0 : numOperands) + 1] & 0xFF) : null;
					if(nextOpc == null || nextOpc != Opcodes.INVOKESTATIC) {
						throw new RuntimeException("unsupported invokedynamic call, next instruction: " + nextOpc);
					}
					var cpIndex = operand >> 16;
					var callSite = (CONSTANT_InvokeDynamic)cls.getConstantPoolIndex(cpIndex).getCpObject();
					var dynamicHandle = (CONSTANT_MethodHandle)callSite.getBootstrapMethod().getBootstrapArgument(1);
					var dynamicRef = (CONSTANT_Methodref)dynamicHandle.getReference();
					var dynamicMethodName = dynamicRef.getNameAndType().getName().getString();
					extractMethodTypesAndExpressions(dynamicRef, tmpList, tmpList2, tmp, methodStack);
					String returnType = tmp.toString();
					methodCallExpression(dynamicRef, dynamicMethodName, tmpList, tmpList2, returnType, methodStack, opc, tmp, str);
					// TODO handle lambda decompiling or at least print out what the call looks like
					i += (numOperands < 0 ? 0 : numOperands); // skip next instruction which invokes the lambda
					numOperands = opc.getOperandCount();
					// java.lang.invoke.CallSite res = java.lang.invoke.LambdaMetafactory.metafactory(MethodHandles.lookup(), "String", java.lang.invoke.MethodType, java.lang.invoke.MethodType, java.lang.invoke.MethodHandle, java.lang.invoke.MethodType);
				}
				else if(opc == Opcodes.INVOKESPECIAL) {
					var methodRef = (CONSTANT_Methodref)cls.getConstantPoolIndex(operand).getCpObject();
					extractMethodTypesAndExpressions(methodRef, tmpList, tmpList2, tmp, methodStack);
					String returnType = tmp.toString();
					var objRef = methodStack.popOperand();
					methodCallExpression(methodRef, objRef.getExpression(), tmpList, tmpList2, returnType, methodStack, opc, tmp, str);
				}
				else if(opc == Opcodes.INVOKEINTERFACE) {
					// TODO
				}
				else {
					throw new IllegalArgumentException("unknown Type.POP_UNPREDICTABLE instruction " + opc);
				}
			}
			else if(opc == Opcodes.NEW) {
				var cpNewType = (CONSTANT_Class)cls.getConstantPoolIndex(operand).getCpObject();
				methodStack.addOperand(new OperandInfo("new " + cpNewType.getName().getString().replace('/', '.'), cpNewType.getName().getString().replace('/', '.'), opc));
			}
			else if(opc == Opcodes.NEWARRAY) {
				var countRef = methodStack.popOperand();
				var typeName = TypeUtility.getArrayType(operand);
				methodStack.addOperand(new OperandInfo("new " + typeName + '[' + countRef.getExpression() + ']', typeName + "[]", opc));
			}
			else if(opc == Opcodes.ANEWARRAY) {
				var countRef = methodStack.popOperand();
				var arrayType = (CONSTANT_Class)cls.getConstantPoolIndex(operand).getCpObject();
				var arrayTypeName = arrayType.getName().getString().replace('/', '.');
				methodStack.addOperand(new OperandInfo("new " + arrayTypeName + '[' + countRef.getExpression() + ']', arrayTypeName + "[]", opc));
			}
			else if(opc == Opcodes.ARRAYLENGTH) {
				var arrayref = methodStack.popOperand();
				methodStack.addOperand(new OperandInfo(arrayref.getExpression() + ".length", "int", opc));
			}
			/*
			TODO:
			ATHROW
			CHECKCAST
			INSTANCEOF
			MONITORENTER
			MONITOREXIT
			WIDE
			MULTIANEWARRAY
			*/
			else if(opc.hasBehavior(Opcodes.Type.CP_INDEX) && operand < cls.getConstantPoolCount()) {
				str.append(" // ").append(opc.displayName()).append(' ').append(cls.getCpIndex((short)operand).getCpObject().toShortString()).append(" [").append(operand).append(']');
			}
			else {
				str.append(" // ").append(opc.displayName()).append(' ').append(operand).append(" 0x").append(Integer.toHexString(operand));
			}
			str.append('\n');

			i += (numOperands < 0 ? 0 : numOperands);
		}

		var tab = indentation;
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
	}


	private static int extractMethodTypesAndExpressions(CONSTANT_Methodref methodRef, List<String> parameterTypes, List<String> parameterExpressions, StringBuilder returnType, MethodStack methodStack) {
		parameterTypes.clear();
		var methodDescriptor = methodRef.getNameAndType().getDescriptor().getString();
		var parameterCount = TypeUtility.methodParameters(methodDescriptor, parameterTypes);

		parameterExpressions.clear();
		for(int k = 0; k < parameterCount; k++) {
			var argRef = methodStack.popOperand();
			parameterExpressions.add(argRef.getExpression());
		}

		returnType.setLength(0);
		TypeUtility.methodReturnType(methodDescriptor, returnType);
		return parameterCount;
	}


	private static void methodCallExpression(CONSTANT_Methodref methodRef, String callObjName, List<String> parameterTypes, List<String> parameterExpressions, String returnType, MethodStack methodStack, Opcodes opc, StringBuilder tmp, StringBuilder dst) {
		int parameterCount = parameterExpressions.size();
		tmp.setLength(0);
		var methodName = methodRef.getNameAndType().getName().getString();
		tmp.append(callObjName);
		if(!"<init>".equals(methodName) && !"<clinit>".equals(methodName)) {
			tmp.append('.').append(methodName);
		}
		tmp.append('(');

		for(int k = 0; k < parameterCount; k++) {
			if(k > 0) { tmp.append(", "); }
			var argExpr = parameterExpressions.get(k);
			tmp.append(argExpr);
		}

		tmp.append(')');

		if(!"void".equals(returnType)) {
			methodStack.addOperand(new OperandInfo(tmp.toString(), returnType, opc));
		}
		else {
			dst.append(tmp);
		}
	}


	private static void setVariable(MethodStack methodStack, int variableIdx, OperandInfo operand, Method_Info method, StringBuilder dst) {
		VariableInfo varInfo = methodStack.getVariableCount() > variableIdx ? methodStack.getVariable(variableIdx) : methodStack.setVariable(variableIdx, operand.getExpressionFullType(), method);
		dst.append(varInfo.getName()).append(" = ").append(operand.getExpression()).append(';');
	}


	private static int findLoopStart(int curIdx, int jumpRelative, List<JumpConditionInfo> loops) {
		// Loops are generally compiled using a GOTO and an IF_* instruction
		// form 1: [..., GOTO <setup_if[0]>, instructions[], setup_if[], IF_* <instructions[0]>, ...]
		if(jumpRelative < 0) {
			var jumpToIdx = curIdx + jumpRelative - 3; // GOTO has a 2 byte operand so -3 is the GOTO instruction index right before the jump destination (which is the first instruction in a loop)
			for(int i = loops.size() - 1; i >= 0; i--) {
				var jc = loops.get(i);
				if(jc.getOpcodeIndex() == jumpToIdx) {
					return i;
				}
			}
		}
		return -1;
	}


	public static Class<?> tryGetPrimitiveType(Class<?> type) {
		if(type == Boolean.class) {
			return Boolean.TYPE;
		}
		else if(type == Byte.class) {
			return Byte.TYPE;
		}
		else if(type == Character.class) {
			return Character.TYPE;
		}
		else if(type == Short.class) {
			return Short.TYPE;
		}
		else if(type == Integer.class) {
			return Integer.TYPE;
		}
		else if(type == Float.class) {
			return Float.TYPE;
		}
		else if(type == Long.class) {
			return Long.TYPE;
		}
		else if(type == Double.class) {
			return Double.TYPE;
		}
		else {
			return null;
		}
	}

}
