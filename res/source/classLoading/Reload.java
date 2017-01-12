package classLoading;

import classLoading.SubReload;

/** Reload test that is being used to test whether two version of the same named class can be loaded during
 * a program's runtime.
 * @author TeamworkGuy2
 * @since 2013-7-20
 */
public class Reload implements Runnable {

	public void callTest() {
		SubReload r = new SubReload();
		System.out.println("Reload.callTest(" + Settings.version + ") call: " + r.toString());
	}


	@Override
	public void run() {
		SubReload r = new SubReload();
		System.out.println("Reload.run(" + Settings.version + ") call: " + r.toString());
	}


	public static final void main(String[] args) {
		Reload a = new Reload();
		a.callTest();
	}

}
