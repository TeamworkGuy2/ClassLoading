package twg2.jbcm.toSource;

import twg2.jbcm.ir.MethodStack;

/** Generate the name for a method parameter
 * @author TeamworkGuy2
 * @since 2020-06-06
 */
public interface ParameterNamer {

	/** Generate the name of a method parameter
	 * @param methodName the method name
	 * @param type the parameter type
	 * @param methodVars names of the parameters and variables used currently in the method
	 * @return the parameter name
	 */
	public String getName(String methodName, String type, MethodStack methodVars);

}
