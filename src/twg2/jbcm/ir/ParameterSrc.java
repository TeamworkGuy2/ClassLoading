package twg2.jbcm.ir;

/**
 * @author TeamworkGuy2
 * @since 2020-07-04
 */
public class ParameterSrc {
	private final String name;
	private final String fullTypeName;
	private final String simpleTypeName;
	private final int numbering;


	public ParameterSrc(String name, String fullTypeName, int numbering) {
		this.name = name;
		this.fullTypeName = fullTypeName;
		this.simpleTypeName = getSimpleTypeName(fullTypeName);
		this.numbering = numbering;
	}


	public String getName() {
		return name;
	}


	public String getFullTypeName() {
		return fullTypeName;
	}


	public String getSimpleTypeName() {
		return simpleTypeName;
	}


	public int getNumbering() {
		return numbering;
	}


	private String getSimpleTypeName(String fullTypeName) {
		int idx = fullTypeName.lastIndexOf('.');
		return fullTypeName.substring(idx + 1);
	}

}
