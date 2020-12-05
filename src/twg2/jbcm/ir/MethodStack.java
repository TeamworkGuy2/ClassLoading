package twg2.jbcm.ir;

import java.util.ArrayList;
import java.util.List;

import twg2.jbcm.classFormat.Method_Info;
import twg2.jbcm.classFormat.attributes.LocalVariableTable;
import twg2.jbcm.modify.TypeUtility;

/** Mutable data structure for tracking the parameters and local variables of a method scope as it is parsed, decompiled, or run.
 * @author TeamworkGuy2
 * @since 2020-06-28
 */
public class MethodStack {
	private String classType;
	private String methodName;
	private List<String> parameterTypes;
	private boolean instance;
	/** The method's parameters. This forms the basis of the method's local variable array. */
	private List<ParameterSrc> parameters;
	/** The method's local variables at the current point in parsing/decompiling/running. */
	private List<VariableInfo> vars;
	/** The operand stack - scratch pad area for local operands */
	private List<OperandInfo> operandStack;


	public MethodStack(String containingClassType, String methodName, String methodDescriptor, boolean instance) {
		this.classType = containingClassType;
		this.methodName = methodName;
		this.parameterTypes = new ArrayList<String>();
		TypeUtility.methodParameters(methodDescriptor, this.parameterTypes);
		this.instance = instance;
		this.parameters = new ArrayList<ParameterSrc>();
		this.vars = new ArrayList<VariableInfo>();
		this.operandStack = new ArrayList<OperandInfo>();
		if(instance) {
			this.parameters.add(new ParameterSrc("this", containingClassType, 0));
			this.vars.add(new VariableInfo("this", containingClassType, 0));
		}
	}


	public String getContainingClassType() {
		return this.classType;
	}


	public String getMethodName() {
		return this.methodName;
	}


	public boolean isInstanceMethod() {
		return this.instance;
	}


	/**
	 * @param idx positive index (from the beginning of the parameter list) or negative index (from the end of the parameter list) to retrieve from the parameter list
	 * @return the parameter
	 */
	public ParameterSrc getParameter(int idx) {
		return idx > -1 ? this.parameters.get(idx) : this.parameters.get(this.parameters.size() - idx);
	}


	/**
	 * @return the number of variables currently initialized
	 */
	public int getVariableCount() {
		return this.vars.size();
	}


	/**
	 * @param idx positive index (from the beginning of the variable stack) or negative index (from the end of the variable stack) to retrieve from the variable stack
	 * @return the variable
	 */
	public VariableInfo getVariable(int idx) {
		return idx > -1 ? this.vars.get(idx) : this.vars.get(this.vars.size() - idx);
	}


	/**
	 * @param idx positive index (from the beginning of the operand stack) or negative index (from the end of the operand stack) to retrieve from the operand stack
	 * @return the operand
	 */
	public OperandInfo getOperand(int idx) {
		return idx > -1 ? this.operandStack.get(idx) : this.operandStack.get(this.operandStack.size() - idx);
	}


	/**
	 * @param idx positive index (from the beginning of the operand stack) or negative index (from the end of the operand stack) to retrieve from the operand stack
	 * @return the operand
	 */
	public OperandInfo popOperand() {
		return this.operandStack.remove(this.operandStack.size() - 1);
	}


	/** Add a parameter. Only the type is known, generate a name based on the type and a counter (ex: 'i1', 'i2', etc for 'int' types)
	 * @param type the parameter type
	 * @return
	 */
	public String addParameterUnnamed(String type) {
		ParameterSrc highestNumberedMatch = null;
		for(var paramType : this.parameters) {
			if(paramType.getFullTypeName().equals(type) && (highestNumberedMatch == null || highestNumberedMatch.getNumbering() < paramType.getNumbering())) {
				highestNumberedMatch = paramType;
			}
		}
		int lastCount = highestNumberedMatch != null ? highestNumberedMatch.getNumbering() : 0;

		String paramName = null;
		if(TypeUtility.isPrimitive(type)) {
			paramName = "boolean".equals(type) ? "bo" : "" + type.charAt(0);
		}
		else {
			String simpleName = type.substring(type.lastIndexOf('.') + 1);
			if(simpleName.length() <= 4) {
				paramName = simpleName.toLowerCase();
			}
			else {
				paramName = toInitials(simpleName);
			}
		}

		this.parameters.add(new ParameterSrc(paramName + (lastCount + 1), type, lastCount + 1));
		this.vars.add(new VariableInfo(paramName + (lastCount + 1), type, lastCount + 1));

		return paramName + (lastCount + 1);
	}


	/** Add a variable. Only the type is known, generate a name based on the type and a counter (ex: 'i1', 'i2', etc for 'int' types)
	 * @param type
	 * @return
	 */
	public VariableInfo setVariable(int index, String type, Method_Info method) {
		// pick variable name based on type and previously used names
		VariableInfo highestNumberedMatch = null;
		for(var varType : this.vars) {
			if(varType.getFullTypeName().equals(type) && (highestNumberedMatch == null || highestNumberedMatch.getNumbering() < varType.getNumbering())) {
				highestNumberedMatch = varType;
			}
		}
		int lastCount = highestNumberedMatch != null ? highestNumberedMatch.getNumbering() : 0;

		String varName = null;
		if(TypeUtility.isPrimitive(type)) {
			varName = "boolean".equals(type) ? "bo" : "" + type.charAt(0);
		}
		else {
			String simpleName = type.substring(type.lastIndexOf('.') + 1);
			if(simpleName.length() <= 4) {
				varName = simpleName.toLowerCase();
			}
			else {
				varName = toInitials(simpleName);
			}
		}

		// add variables to fill the local variable array up to the 'index' to set
		var localVariableTable = method != null ? findLocalVariableTable(method) : null;
		if(localVariableTable != null) {
			var localVariableCount = localVariableTable.getLocalVariableTableLength();
			for(int i = this.vars.size(); i < localVariableCount; i++) {
				var localVar = localVariableTable.getLocalVariable(i);
				this.vars.add(new VariableInfo(localVar.getName(), null, 0));
			}
			if(localVariableCount <= index) {
				// TODO warning
				System.err.println("local variable table size (" + localVariableCount + ") was smaller than var accessed (" + index + ") for method " + method);
			}
		}

		for(int i = this.vars.size(); i <= index; i++) {
			this.vars.add(new VariableInfo("varPlaceholder" + (i + 1), null, 0));
		}

		// create the variable requested
		var newVar = new VariableInfo(varName + (lastCount + 1), type, lastCount + 1);
		this.vars.set(index, newVar);

		return newVar;
	}


	/** Add an operand stack value.
	 * @param src the operand info object to add
	 * @return the {@code src} operand info object
	 */
	public OperandInfo addOperand(OperandInfo operandInfo) {
		this.operandStack.add(operandInfo);
		return operandInfo;
	}


	/** Convert a string to {@code 'dash-css-case'}
	 * @param str the string to convert
	 * @return the dash-css-case version of the string
	 */
	public static String toInitials(String str) {
		StringBuilder sb = new StringBuilder();

		char prevCh = str.charAt(0);
		char ch = str.charAt(1);
		char nextCh = str.charAt(2);
		if(Character.isUpperCase(prevCh) || Character.isAlphabetic(prevCh)) sb.append(Character.toLowerCase(prevCh));

		for(int i = 2, size = str.length(); i < size; i++) {
			nextCh = str.charAt(i);
			if(prevCh == '_' || prevCh == '$' || (Character.isUpperCase(ch) && (!Character.isUpperCase(prevCh) || Character.isLowerCase(nextCh)))) sb.append(Character.toLowerCase(prevCh));
			prevCh = ch;
			ch = nextCh;
		}

		return sb.toString();
	}


	public static LocalVariableTable findLocalVariableTable(Method_Info method) {
		for(int i = 0, size = method.getAttributeCount(); i < size; i++) {
			var attr = method.getAttribute(i);
			if(LocalVariableTable.ATTRIBUTE_NAME.equals(attr.getAttributeName())) {
				return (LocalVariableTable)attr;
			}
		}
		return null;
	}

}
