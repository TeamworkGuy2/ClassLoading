package twg2.jbcm.classFormat;

/** Settings for the Java class file format classes
 * @author TeamworkGuy2
 * @since 2013-7-7
 */
public class Settings {
	public static boolean debug = false;
	public static boolean cpTagRead = true;
	public static boolean checkCPIndex = true;
	/** Whether to validate the tag type of each constant pool entry read, without a valid tag constant pool entries
	 * cannot be loaded and two bytes of data assumed to contain the constant pool entry info are skipped
	 */
	public static boolean checkCPTag = true;
	public static boolean checkCPExpectedType = true;
	public static boolean checkAttributeName = true;
	public static boolean readAttributeName = false;


	private Settings() {
		throw new AssertionError("may not instantiate Settings");
	}

}
