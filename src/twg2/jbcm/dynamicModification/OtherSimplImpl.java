package twg2.jbcm.dynamicModification;


public class OtherSimplImpl implements SimpleInterface {
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
}
