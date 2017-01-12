package classLoading;

import classLoading.base.TestInterface;

public class TestThing implements TestInterface {

	@Override
	public void printContextInfo() {
		System.out.println("Test Thing: " + this + ", " + Thread.currentThread() + ", " + Thread.currentThread().getContextClassLoader());
	}
}
