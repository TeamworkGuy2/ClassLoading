package twg2.jbcm.toSource.structures;

import twg2.jbcm.Opcodes;
import twg2.jbcm.ir.OperandInfo;
import twg2.jbcm.ir.Switch;
import twg2.jbcm.toSource.StringBuilderIndent;
import twg2.jbcm.toSource.SwitchFlow;

/**
 * @author TeamworkGuy2
 * @since 2022-10-01
 */
public class CodeStructureSwitch implements CodeEmitter {
	Switch curSwitch = null;


	@Override
	public EmitterResponse emitOpcode(Opcodes opc, byte[] instr, int idx, int operand, StringBuilderIndent src) {
		// end switch
		if(idx == curSwitch.switchEndIdx) {
			src.dedent();
			src.appendIndent().append('}').append('\n');
			return EmitterResponse.DEREGISTER;
		}

		// switch cases
		if(curSwitch.switchDefault.caseTarget == idx) {
			curSwitch.finish(curSwitch.switchDefault);
			src.append("default: ").append("// offset ").append(curSwitch.switchDefault.caseTarget);
		}

		var switchCaseIdx = SwitchFlow.findSwitchCase(idx, curSwitch.switchCases, 0);
		while(switchCaseIdx > -1) {
			var caseObj = curSwitch.switchCases.get(switchCaseIdx);
			caseObj.finish();
			src.append(src.indent.toDedent()).append("case ").append(caseObj.caseMatch).append(": ").append("// offset ").append(caseObj.caseTarget).append('\n');
			switchCaseIdx = SwitchFlow.findSwitchCase(idx, curSwitch.switchCases, switchCaseIdx + 1);
		}

		return EmitterResponse.CONTINUE;
	}


	public static CodeStructureSwitch create(Opcodes opc, byte[] instr, int idx, OperandInfo switchKey, StringBuilderIndent src) {
		var inst = new CodeStructureSwitch();

		if(opc == Opcodes.TABLESWITCH) {
			inst.curSwitch = Switch.loadTableSwitch(idx, instr);
		}
		else if(opc == Opcodes.LOOKUPSWITCH) {
			inst.curSwitch = Switch.loadLookupSwitch(idx, instr);
		}
		//int origI = idx;
		idx += inst.curSwitch.switchInstSize;
		src.indent();
		src.append("switch(" + switchKey.getExpression() + ") {");

		//var instrFlow = last(instrFlows);
		//instrFlow.add(curSwitch.switchEndIdx > -1 ? ~curSwitch.switchEndIdx : ~i); // TODO technically we wouldn't want to restart at the current index
		// need some unit tests to check different types of switches
		// can a switch with fall through cases still have a common end index, 'break' instructions should lead to a common end index, what if the default throws, etc?
		//instrFlow.add(endIdx);

		//SwitchFlow.loadCasesToFlow(curSwitch.switchCases, curSwitch.switchDefault, instrFlows);

		// TODO debugging
		//str.append(" // common end: " + commonEndIdx);
		//for(var caseObj : switchCases) {
		//	str.append('\n').append(indentation).append(indentMark).append("// case " + caseObj.caseMatch + ": [" + caseObj.caseTarget + ", " + caseObj.caseEndIdx + "]" + (caseObj.caseEndTarget > 0 ? " jump to " + caseObj.caseEndTarget : ""));
		//	System.out.println("case " + caseObj.caseMatch + ": " + CodeFlow.flowPathToString(instr, caseObj.getCodeFlow()));
		//}
		//str.append('\n').append(indentation).append(indentMark).append("// default: [" + switchDefault.get().caseTarget + ", " + switchDefault.get().caseEndIdx + "]" + (switchDefault.get().caseEndTarget > 0 ? " jump to " + switchDefault.get().caseEndTarget : ""));

		//if(curSwitch.switchEndIdx == -1) {
		//	throw new IllegalStateException(opc + " switch statement common end index could not be found, cannot decompile");
		//}
		return inst;
	}
}
