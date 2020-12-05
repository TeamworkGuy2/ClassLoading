package twg2.jbcm.ir;

import twg2.jbcm.Opcodes;

/**
 * @author TeamworkGuy2
 * @since 2020-07-11
 */
public class OperandInfo {
	// these 2 values are exclusive, only one will be non-null
	private final VariableInfo varSrc; // a variable serving as the source for this operand
	private final String expression; // the expression for this operand

	private final String fullTypeName; // the expression's full type name
	private final Opcodes opcodeSrc;


	public OperandInfo(VariableInfo varSrc, Opcodes opcodeSrc) {
		this.varSrc = varSrc;
		this.expression = null;
		this.fullTypeName = varSrc.getFullTypeName();
		this.opcodeSrc = opcodeSrc;
	}


	public OperandInfo(String expression, String expressionFullTypeName, Opcodes opcodeSrc) {
		this.varSrc = null;
		this.expression = expression;
		this.fullTypeName = expressionFullTypeName;
		this.opcodeSrc = opcodeSrc;
	}


	public OperandInfo copy() {
		if(this.expression != null) {
			return new OperandInfo(this.expression, this.fullTypeName, this.opcodeSrc);
		}
		else {
			return new OperandInfo(this.varSrc, this.opcodeSrc);
		}
	}


	public String getExpression() {
		if(expression != null) {
			return expression;
		}
		else if(varSrc != null) {
			return varSrc.getName();
		}
		return null;
	}


	public String getExpressionFullType() {
		return fullTypeName;
	}


	public VariableInfo getVariableSrc() {
		return this.varSrc;
	}


	public Opcodes getOpcodeSrc() {
		return this.opcodeSrc;
	}

}
