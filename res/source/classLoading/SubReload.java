package classLoading;

/** SubReload test that is being used to test class calling from multiple dynamically loaded calling classes.
 * @author TeamworkGuy2
 * @since 2013-7-20
 */
public class SubReload {

	public void callTest() {
		System.out.println("SubReload.callTest(" + Settings.version + ")");
	}


	public String toString() {
		return "SubReload.toString(" + Settings.version + ")";
	}


	public static final void main(String[] args) {
		SubReload a = new SubReload();
		a.callTest();
	}

}