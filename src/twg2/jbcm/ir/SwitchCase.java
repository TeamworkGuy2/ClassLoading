package twg2.jbcm.ir;

import java.util.Comparator;

import twg2.collections.primitiveCollections.IntList;
import twg2.jbcm.CodeFlow;

/**
 * @author TeamworkGuy2
 * @since 2020-09-08
 */
public class SwitchCase {

	public static Comparator<SwitchCase> CASE_MATCH_COMPARATOR = new Comparator<SwitchCase>() {
		@Override public int compare(SwitchCase o1, SwitchCase o2) {
			return o1.caseMatch - o2.caseMatch;
		}
	};

	public static Comparator<SwitchCase> CASE_INDEX_COMPARATOR = new Comparator<SwitchCase>() {
		@Override public int compare(SwitchCase o1, SwitchCase o2) {
			return o1.caseTarget - o2.caseTarget;
		}
	};

	public final int caseMatch;
	public final int caseTarget;
	public final int caseEndIdx;
	public final int caseEndTarget;
	public final boolean hasEndTarget;
	private final IntList codeFlow;
	private boolean finished;

	public SwitchCase(int caseMatch, int caseTarget, int caseEndIdx, IntList codeFlow) {
		this.caseMatch = caseMatch;
		this.caseTarget = caseTarget;
		this.caseEndIdx = caseEndIdx;
		this.caseEndTarget = 0;
		this.hasEndTarget = false;
		this.codeFlow = codeFlow;
	}


	public SwitchCase(int caseMatch, int caseTarget, int caseEndIdx, int caseEndTarget, IntList codeFlow) {
		this.caseMatch = caseMatch;
		this.caseTarget = caseTarget;
		this.caseEndIdx = caseEndIdx;
		this.caseEndTarget = caseEndTarget;
		this.hasEndTarget = true;
		this.codeFlow = codeFlow;
	}


	public boolean contains(int idx) {
		return idx >= caseTarget && idx <= caseEndIdx;
	}


	/** The {@link CodeFlow} for this switch case statement, starting from the case target, tracing all non-circular paths within the method's code
	 * @return
	 */
	public IntList getCodeFlow() {
		return codeFlow;
	}


	public void finish() {
		this.finished = true;
	}


	public boolean isFinished() {
		return finished;
	}


	@Override
	public String toString() {
		return "case " + this.caseMatch + ": [" + this.caseTarget + ", " + this.caseEndIdx + (this.hasEndTarget ? "] -> " + this.caseEndTarget : "]");
	}
}
