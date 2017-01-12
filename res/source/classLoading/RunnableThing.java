package classLoading;

import classLoading.base.TestInterface;

public class RunnableThing implements TestInterface, Runnable {
	TestThing thing = new TestThing();
	int variable;
	boolean printInfo;
	boolean doExit;

	@Override
	public void run() {
		variable++;
		thing = new TestThing();
		thing.printContextInfo();
		synchronized(this) {
			while(!doExit) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(printInfo) {
					printContextInfo();
				}
			}
		}
	}


	public void event(boolean printInfo, boolean exit) {
		synchronized(this) {
			this.printInfo = printInfo;
			this.doExit = exit;
		}
	}


	@Override
	public void printContextInfo() {
		System.out.println("Thread: " + Thread.currentThread() + ", classloader: " + Thread.currentThread().getContextClassLoader());
		synchronized(this) {
			this.printInfo = false;
		}
	}

}
