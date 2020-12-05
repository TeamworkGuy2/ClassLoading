package twg2.jbcm.ir;

/**
 * @author TeamworkGuy2
 * @since 2020-07-11
 */
public class VariableInfo {
	private final String name;
	private final String fullTypeName;
	private final int numbering;


	public VariableInfo(String name, String fullTypeName, int numbering) {
		this.name = name;
		this.fullTypeName = fullTypeName;
		this.numbering = numbering;
	}


	public String getName() {
		return name;
	}


	public String getFullTypeName() {
		return fullTypeName;
	}


	public int getNumbering() {
		return numbering;
	}

}
