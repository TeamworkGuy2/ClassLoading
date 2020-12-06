package twg2.jbcm.ir;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import twg2.jbcm.toSource.SwitchFlow;

/**
 * @author TeamworkGuy2
 * @since 2020-12-05
 */
public class Switch {
	public List<SwitchCase> switchCases;
	protected List<SwitchCase> finishedCases;
	public SwitchCase switchDefault;
	public int switchInstSize;
	public int switchEndIdx;
	/** whether all the cases in this switch return/throw before the next case starts (see {@link SwitchFlow#isSwitchSimplePacked(List, SwitchCase, byte[])}) */
	public boolean isReturnPacked;


	public Switch(List<SwitchCase> switchCases, SwitchCase switchDefault) {
		this.switchCases = switchCases;
		this.switchDefault = switchDefault;
		this.finishedCases = new ArrayList<SwitchCase>();
	}


	public void finish(SwitchCase switchCase) {
		switchCase.finish();
		finishedCases.add(switchCase);
	}


	public boolean isFinished() {
		return finishedCases.size() == switchCases.size() + 1; // + 1 for default case
	}


	public static Switch loadTableSwitch(int i, byte[] instr) {
		var dstCases = new ArrayList<SwitchCase>();
		var dstSwitchDefault = new AtomicReference<SwitchCase>();
		int newI = SwitchFlow.loadTableSwitch(i, instr, dstCases, dstSwitchDefault);
		var inst = new Switch(dstCases, dstSwitchDefault.get());
		int endIdx = SwitchFlow.commonSwitchEndIndex(inst.switchCases, inst.switchDefault, instr);
		if(endIdx == -1) {
			endIdx = SwitchFlow.maxSwitchCodeFlowIndex(inst.switchCases, inst.switchDefault, instr);
			if(endIdx > -1) {
				inst.isReturnPacked = true;
			}
		}
		inst.switchEndIdx = endIdx;
		inst.switchInstSize = newI;
		return inst;
	}


	public static Switch loadLookupSwitch(int i, byte[] instr) {
		var dstCases = new ArrayList<SwitchCase>();
		var dstSwitchDefault = new AtomicReference<SwitchCase>();
		int newI = SwitchFlow.loadLookupSwitch(i, instr, dstCases, dstSwitchDefault);
		var inst = new Switch(dstCases, dstSwitchDefault.get());
		int endIdx = SwitchFlow.commonSwitchEndIndex(inst.switchCases, inst.switchDefault, instr);
		if(endIdx == -1) {
			endIdx = SwitchFlow.maxSwitchCodeFlowIndex(inst.switchCases, inst.switchDefault, instr);
			if(endIdx > -1) {
				inst.isReturnPacked = true;
			}
		}
		inst.switchEndIdx = endIdx;
		inst.switchInstSize = newI;
		return inst;
	}
}
