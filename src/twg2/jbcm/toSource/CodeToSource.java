package twg2.jbcm.toSource;

import java.util.ArrayList;
import java.util.List;

import twg2.collections.primitiveCollections.IntArrayList;
import twg2.jbcm.CodeFlow;
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
import twg2.jbcm.ir.MethodStack;
import twg2.jbcm.ir.OperandInfo;
import twg2.jbcm.ir.VariableInfo;
import twg2.jbcm.modify.TypeUtility;
import twg2.jbcm.toSource.structures.CodeStructureIf;
import twg2.jbcm.toSource.structures.CodeStructureLoop;
import twg2.jbcm.toSource.structures.CodeEmitter;
import twg2.jbcm.toSource.structures.CodeStructureSwitch;
import twg2.jbcm.toSource.structures.CodeStructureSynchronized;
import twg2.jbcm.toSource.structures.CodeStructureTryCatchFinally;

import static twg2.jbcm.CodeUtility.loadOperands;
import static twg2.jbcm.modify.TypeUtility.decodeBinaryClassName;
import static twg2.jbcm.toSource.structures.CodeEmitter.runEmits;

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
		int instrCount = instr.length;
		int i = 0;
		int endIdx = instrCount - 1;

		var possibleLoops = CodeFlow.findFlowConditions(instr, i, endIdx); // track GOTO/IF_* loops detected in the code
		var structuresInProgress = new ArrayList<CodeEmitter>();

		var exception_table = code.getExceptionTable();
		for(int j = 0; j < exception_table.length; j++) {
			var tryCatchStructure = CodeStructureTryCatchFinally.create(instr, exception_table[j], "ex" + (j + 1), methodStack);
			if(tryCatchStructure != null) {
				structuresInProgress.add(tryCatchStructure);
			}
		}

		StringBuilder tmp = new StringBuilder();
		var tmpList = new ArrayList<String>();
		var tmpList2 = new ArrayList<String>();
		var src = new StringBuilderIndent(dst.src, dst.getIndent());

		src.append("// stack: ").append(code.getMaxStack())
			.append(", locals: ").append(code.getMaxLocals()).append('\n');

		var instrFlows = new ArrayList<IntArrayList>(); // pairs of start (inclusive) and end (inclusive) indexes to control which portions of the code are processed by the main loop
		instrFlows.add(new IntArrayList());

		for( ; i <= endIdx; i++) {
			Opcodes opc = Opcodes.get(instr[i] & 0xFF);
			int numOperands = opc.getOperandCount();
			// read following bytes of code and convert them to an operand
			int operand = loadOperands(numOperands, instr, i);

			// unpredictable operand length instructions
			if(opc == Opcodes.WIDE) {
				// TODO need to read extra wide operand bytes in all possible cases
				i++; // because wide instructions are wrapped around other instructions
				opc = Opcodes.get(instr[i] & 0xFF);
				numOperands = opc.getOperandCount();
			}
			// switch
			else if(numOperands == Opcodes.Const.UNPREDICTABLE) {
				structuresInProgress.add(CodeStructureSwitch.create(opc, instr, i, methodStack.popOperand(), src));
			}

			if(opc.hasBehavior(Type.CONST_LOAD)) {
				Object constValue = opc.getConstantValue();
				Class<?> constType = constValue != null ? tryGetPrimitiveType(constValue.getClass()) : Object.class;
				try {
					methodStack.addOperand(new OperandInfo(constValue.toString(), constType.getName(), opc));
				} catch(Exception ex) {
					System.err.println(ex);
				}
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
			// includes: ILOAD*, LLOAD*, FLOAD*, DLOAD*, ALOAD*
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
			// includes: ISTORE*, LSTORE*, FSTORE*, DSTORE*, ASTORE*
			else if(opc.hasBehavior(Type.VAR_STORE)) {
				int localVarIdx = numOperands > 0 ? loadOperands(numOperands, instr, i) : ((Number)opc.getConstantValue()).intValue();
				try {
					setVariable(methodStack, localVarIdx, methodStack.popOperand(), method, src);
				} catch(Exception ex) {
					throw ex;
				}
			}
			else if(opc.hasBehavior(Type.ARRAY_STORE)) {
				var value = methodStack.popOperand();
				var index = methodStack.popOperand();
				var arrayref = methodStack.popOperand();
				src.appendIndent().append(arrayref.getExpression()).append('[').append(index.getExpression()).append(']').append(" = ").append(value.getExpression()).append(';').append('\n');
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
				src.appendIndent().append(varref.getName()).append(increment == 1 ? "++" : " += " + increment).append('\n');
			}
			else if(opc.hasBehavior(Type.TYPE_CONVERT)) {
				// TODO
			}
			else if(opc.hasBehavior(Type.COMPARE_NUMERIC)) {
				// TODO
			}
			else if(opc.hasBehavior(Type.CONDITION)) {
				OperandInfo lhs = null;
				String rhs = null;

				// extract the operands for the 'if' condition
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
				// all condition opcodes involve either one or two operands (as of Java 8)
				else {
					var val2 = methodStack.popOperand();
					var val1 = methodStack.popOperand();

					lhs = val1;
					rhs = val2.getExpression();

					if(opc == Opcodes.IF_ICMPEQ || opc == Opcodes.IF_ICMPNE || opc == Opcodes.IF_ICMPLT || opc == Opcodes.IF_ICMPGT || opc == Opcodes.IF_ICMPLE || opc == Opcodes.IF_ICMPGE ||
							opc == Opcodes.IF_ACMPEQ || opc == Opcodes.IF_ACMPNE) {
					}
				}

				int loopIdx = CodeFlow.findContainsIfIndex(possibleLoops, i);

				// loop found
				if(loopIdx >= 0) {
					structuresInProgress.add(CodeStructureLoop.create(possibleLoops.get(loopIdx), lhs, rhs));
				}
				// if-statement
				else {
					int condIdx = CodeFlow.findOpcIndex(possibleLoops, i);
					if(condIdx >= 0) {
						structuresInProgress.add(CodeStructureIf.create(possibleLoops.get(condIdx), lhs, rhs));
					}
					else {
						throw new IllegalArgumentException("could not find condition at index " + i);
					}
				}
			}
			else if(opc == Opcodes.GOTO) {
				// handled by Type.CONDITION loop and if structures above 
				// TODO what other actions is GOTO used for:
				// labeled block breaks?
			}
			else if(opc == Opcodes.JSR || opc == Opcodes.GOTO_W || opc == Opcodes.JSR_W) {
				// TODO: JSR, GOTO_W, & JSR_W
			}
			else if(opc == Opcodes.RET) {
				// TODO
			}
			else if(opc.hasBehavior(Type.RETURN)) {
				src.appendIndent().append("return");
				if(opc != Opcodes.RETURN) {
					var retVal = methodStack.popOperand();
					src.append(' ').append(retVal.getExpression());
				}
				src.append(';').append('\n');
			}
			else if(opc == Opcodes.GETSTATIC) {
				var fieldRef = (CONSTANT_Fieldref)cls.getConstantPoolIndex(operand).getCpObject();
				tmp.setLength(0);
				TypeUtility.typeDescriptorToSource(fieldRef.getNameAndType().getDescriptor().getString(), tmp);
				var fieldType = tmp.toString();

				var fieldClass = fieldRef.getClassType();
				var fieldNameAndType = fieldRef.getNameAndType();
				var fieldStr = decodeBinaryClassName(fieldClass) + "." + fieldNameAndType.getName().getString();

				methodStack.addOperand(new OperandInfo(fieldStr, fieldType, opc));
			}
			else if(opc == Opcodes.PUTSTATIC) {
				var val = methodStack.popOperand();
				var fieldRef = (CONSTANT_Fieldref)cls.getConstantPoolIndex(operand).getCpObject();
				var fieldClass = fieldRef.getClassType();
				var fieldNt = fieldRef.getNameAndType();

				src.appendIndent().append(decodeBinaryClassName(fieldClass)).append('.').append(fieldNt.getName().getString()).append(" = ").append(val.getExpression()).append('\n');
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

				src.appendIndent().append(objRef.getExpression()).append('.').append(fieldNt.getName().getString()).append(" = ").append(val.getExpression()).append('\n');
			}
			else if(opc.hasBehavior(Type.CP_INDEX) && opc.hasBehavior(Type.POP_UNPREDICTABLE)) {
				StringBuilder callExpr = null;
				if(opc == Opcodes.INVOKEVIRTUAL) {
					var methodRef = (CONSTANT_Methodref)cls.getConstantPoolIndex(operand).getCpObject();
					extractMethodTypesAndExpressions(methodRef, tmpList, tmpList2, tmp, methodStack);
					String returnType = tmp.toString();
					var objRef = methodStack.popOperand();
					callExpr = methodCallExpression(methodRef, objRef.getExpression(), tmpList, tmpList2, returnType, methodStack, opc, tmp);
				}
				else if(opc == Opcodes.INVOKESTATIC) {
					var methodRef = (CONSTANT_Methodref)cls.getConstantPoolIndex(operand).getCpObject();
					var staticClass = methodRef.getClassType();
					extractMethodTypesAndExpressions(methodRef, tmpList, tmpList2, tmp, methodStack);
					String returnType = tmp.toString();
					callExpr = methodCallExpression(methodRef, decodeBinaryClassName(staticClass), tmpList, tmpList2, returnType, methodStack, opc, tmp);
				}
				else if(opc == Opcodes.INVOKEDYNAMIC) {
					Opcodes nextOpc = i + (numOperands < 0 ? 0 : numOperands) + 1 < instrCount ? Opcodes.get(instr[i + (numOperands < 0 ? 0 : numOperands) + 1] & 0xFF) : null;
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
					callExpr = methodCallExpression(dynamicRef, dynamicMethodName, tmpList, tmpList2, returnType, methodStack, opc, tmp);
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
					callExpr = methodCallExpression(methodRef, objRef.getExpression(), tmpList, tmpList2, returnType, methodStack, opc, tmp);
				}
				else if(opc == Opcodes.INVOKEINTERFACE) {
					// TODO
				}
				else {
					throw new IllegalArgumentException("unknown Type.POP_UNPREDICTABLE instruction " + opc);
				}
				if(callExpr != null) {
					src.appendIndent().append(callExpr).append('\n');
				}
			}
			else if(opc == Opcodes.NEW) {
				var cpNewType = (CONSTANT_Class)cls.getConstantPoolIndex(operand).getCpObject();
				methodStack.addOperand(new OperandInfo("new " + decodeBinaryClassName(cpNewType), decodeBinaryClassName(cpNewType), opc));
			}
			else if(opc == Opcodes.NEWARRAY) {
				var countRef = methodStack.popOperand();
				var typeName = TypeUtility.getArrayType(operand);
				methodStack.addOperand(new OperandInfo("new " + typeName + '[' + countRef.getExpression() + ']', typeName + "[]", opc));
			}
			else if(opc == Opcodes.ANEWARRAY) {
				var countRef = methodStack.popOperand();
				var arrayType = (CONSTANT_Class)cls.getConstantPoolIndex(operand).getCpObject();
				var arrayTypeName = decodeBinaryClassName(arrayType);
				methodStack.addOperand(new OperandInfo("new " + arrayTypeName + '[' + countRef.getExpression() + ']', arrayTypeName + "[]", opc));
			}
			else if(opc == Opcodes.ARRAYLENGTH) {
				var arrayref = methodStack.popOperand();
				methodStack.addOperand(new OperandInfo(arrayref.getExpression() + ".length", "int", opc));
			}
			else if(opc == Opcodes.ALOAD) {
				
			}
			else if(opc == Opcodes.ATHROW) {
				var objectref = methodStack.popOperand();
				src.appendIndent().append("throw").append(' ').append(objectref.getExpression()).append(';').append('\n');
			}
			else if(opc == Opcodes.MONITORENTER) {
				var objectref = methodStack.popOperand();
				structuresInProgress.add(CodeStructureSynchronized.create(i, objectref));
			}
			else if(opc == Opcodes.MONITOREXIT) {
				// MONITOREXIT is handled by the above if-statement
			}
			/*
			TODO:
			CHECKCAST
			INSTANCEOF
			MULTIANEWARRAY
			*/
			else if(opc.hasBehavior(Opcodes.Type.CP_INDEX) && operand < cls.getConstantPoolCount()) {
				src.appendIndent().append(" // ").append(opc.displayName()).append(' ').append(cls.getCpIndex((short)operand).getCpObject().toShortString()).append(" [").append(operand).append(']').append('\n');
			}
			else {
				src.appendIndent().append(" // ").append(opc.displayName()).append(' ');
				if(numOperands > 0) {
					src.append(operand).append(" 0x").append(Integer.toHexString(operand));
				}
				src.append('\n');
			}

			runEmits(structuresInProgress, opc, instr, i, operand, src);

			i += (numOperands < 0 ? 0 : numOperands);

			// next code flow if current one is finished
			if(i >= endIdx) {
				var instrFlow = last(instrFlows);
				if(instrFlow != null) {
					if(instrFlow.size() == 0) {
						// don't remove the root flow
						if(instrFlows.size() > 1) {
							instrFlows.remove(instrFlows.size() - 1);
						}
					}
					else {
						endIdx = instrFlow.pop();
						endIdx = endIdx < 1 ? ~endIdx : endIdx;
						i = instrFlow.pop();
						i = i < 0 ? ~i : i;
					}
				}
			}
		}

		src.append("\n");

		// TODO should finish processing any unfinished structures

		src.appendIndent().append("// exceptions: ").append(exception_table.length).append(" [");
		if(exception_table.length > 0) { src.append("\n").appendIndent(); }
		for(int j = 0; j < exception_table.length - 1; j++) {
			src.append("// ").append(exception_table[j]).append(",\n").appendIndent();
		}
		if(exception_table.length > 0) { src.append("// ").append(exception_table[exception_table.length - 1]); src.append('\n'); }

		var attributes = code.getAttributes();
		src.appendIndent().append("// attributes: ").append(attributes.length).append(" [");
		if(attributes.length > 0) { src.append("\n").appendIndent(); }
		for(int j = 0; j < attributes.length - 1; j++) {
			src.append("// ").append(attributes[j]).append(",\n").appendIndent();
		}
		if(attributes.length > 0) { src.append("// ").append(attributes[attributes.length - 1]); }
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


	private static StringBuilder methodCallExpression(
		CONSTANT_Methodref methodRef,
		String callObjName,
		List<String> parameterTypes,
		List<String> parameterExpressions,
		String returnType,
		MethodStack methodStack,
		Opcodes opc,
		StringBuilder tmp
	) {
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
			return null;
		}
		else {
			return tmp;
		}
	}


	private static void setVariable(MethodStack methodStack, int variableIdx, OperandInfo operand, Method_Info method, StringBuilderIndent dst) {
		VariableInfo varInfo = methodStack.getVariableCount() > variableIdx ? methodStack.getVariable(variableIdx) : methodStack.setVariable(variableIdx, operand.getExpressionFullType(), method);
		dst.appendIndent().append(varInfo.getName()).append(" = ").append(operand.getExpression()).append(';').append('\n');
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


	private static <T> T last(List<T> lists) {
		int listSize = lists.size();
		return listSize > 0 ? lists.get(listSize - 1) : null;
	}

}
