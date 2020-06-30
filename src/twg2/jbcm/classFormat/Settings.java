package twg2.jbcm.classFormat;

/** Settings for the Java class file format classes
 * @author TeamworkGuy2
 * @since 2013-7-7
 */
public class Settings {
	public static boolean debug = true;
	public static boolean cpTagRead = true;
	public static boolean checkCPIndex = true;
	public static boolean checkCPExpectedType = true;
	public static boolean checkAttributeName = true;
	public static boolean readAttributeName = false;


	private Settings() {
		throw new AssertionError("may not instantiate Settings");
	}

	public static boolean debug() {
		return debug;
	}

	public static boolean cpTagRead() {
		return cpTagRead;
	}

	public static boolean checkCPIndex() {
		return checkCPIndex;
	}

	public static boolean checkCPExpectedType() {
		return checkCPExpectedType;
	}

	public static boolean checkAttributeName() {
		return checkAttributeName;
	}

	public static boolean readAttributeName() {
		return readAttributeName;
	}

}
