package twg2.jbcm.dynamicModification;

public class SimpleInterfaceImpl implements SimpleInterface {
	int various = 0;

	@Override
	public void callTest() {
		callA();
	}

	@Override
	public int getCount() {
		return various;
	}

	public void callA() {
		various += 1;
	}

	public void callB() {
		various -= 1;
	}

	public void callC() {
		various += 2;
	}


	public void callD() {
		various -= 2;
	}

	public void callE() {
		various = 0;
	}

	public void callF() {
		various = Integer.MAX_VALUE;
	}

	public void callG() {
		various = Integer.MIN_VALUE;
	}

}
