package classLoading;

import java.util.ArrayList;

import classLoading.base.WorkerInterface;

public class ClassLoaded implements WorkerInterface {
	private int id;
	private ArrayList<Thread> threads = new ArrayList<Thread>();
	private ArrayList<RunnableThing> objs = new ArrayList<RunnableThing>();

	@Override
	public void startThreads(int threadCount) {
		Thread newT = null;
		RunnableThing thing = null;
		int sleep = 10;
		for(int i = 0; i < threadCount; i++) {
			thing = new RunnableThing();
			newT = new Thread(thing);
			newT.setName("runnable test " + id);
			threads.add(newT);
			objs.add(thing);
			newT.start();
			id++;
		}
		try {
			Thread.sleep(sleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for(int i = 0; i < threadCount; i++) {
			thing = objs.get(i);
			thing.event(true, false);
			synchronized(thing) {
				thing.notify();
			}
		}
		try {
			Thread.sleep(sleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for(int i = 0; i < threadCount; i++) {
			thing = objs.get(i);
			thing.event(true, true);
			synchronized(thing) {
				thing.notify();
			}
		}
		try {
			Thread.sleep(sleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for(int i = 0; i < threadCount; i++) {
			if(threads.get(i).isAlive()) {
				System.out.println("Thread " + threads.get(i) + " (" + i + ") still alive");
			}
		}
		threads.clear();
		objs.clear();
		id = 0;
	}

}
