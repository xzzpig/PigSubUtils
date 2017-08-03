package com.github.xzzpig.pigutils.thread;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadLock {

	private Vector<Thread> threads;

	private AtomicBoolean locked;

	public ThreadLock() {
		locked = new AtomicBoolean(false);
		threads = new Vector<>();
	}

	public boolean isLocked() {
		return locked.get();
	}

	public boolean isPrepared() {
		return threads.contains(Thread.currentThread());
	}

	public void lock() {
		threads.clear();
		locked.set(true);
	}

	public void prepare() {
		waitUnlock();
		waitLock();
		threads.add(Thread.currentThread());
		waitUnlock();
	}

	public void prepared() {
		threads.remove(Thread.currentThread());
	}

	public void unlock() {
		locked.set(false);
		while (threads.size() != 0)
			;
	}

	public void waitLock() {
		while (!locked.get())
			;
	}

	public void waitUnlock() {
		while (locked.get())
			;
	}
}
