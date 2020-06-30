package twg2.jbcm;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import twg2.jbcm.modify.TypeUtility;

/** A data structure for tracking the parameters and local variables of a method scope as it is parsed, decompiled, or run
 * @author TeamworkGuy2
 * @since 2020-06-28
 */
public class MethodStack {
	private String classType;
	private boolean instance;
	private Map<String, Integer> paramNamesMap;


	public MethodStack(String containingClassType, boolean instance) {
		this.classType = containingClassType;
		this.instance = instance;
		this.paramNamesMap = new LinkedHashMap<String, Integer>();
		if(instance) {
			this.paramNamesMap.put("this", 0);
		}
	}


	public String getContainingClassType() {
		return this.classType;
	}


	public boolean isInstanceMethod() {
		return this.instance;
	}


	public Set<String> getParameters() {
		return this.paramNamesMap.keySet();
	}


	/** Add a parameter. Only the type is known, generate a name based on the type and a counter (ex: 'i1', 'i2', etc for 'int' types)
	 * @param type
	 * @return
	 */
	public String addParameterUnnamed(String type) {
		Integer count = this.paramNamesMap.get(type);
		if(count == null) {
			count = 1;
		}

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

		this.paramNamesMap.put(type, count + 1);

		return paramName + count;
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
		if(Character.isUpperCase(prevCh)) sb.append(Character.toLowerCase(prevCh));

		for(int i = 2, size = str.length(); i < size; i++) {
			nextCh = str.charAt(i);
			if(prevCh == '_' || prevCh == '$' || (Character.isUpperCase(ch) && (!Character.isUpperCase(prevCh) || Character.isLowerCase(nextCh)))) sb.append(Character.toLowerCase(prevCh));
			prevCh = ch;
			ch = nextCh;
		}

		return sb.toString();
	}

}
