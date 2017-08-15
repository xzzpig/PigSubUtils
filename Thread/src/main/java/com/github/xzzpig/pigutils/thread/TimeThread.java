package com.github.xzzpig.pigutils.thread;

import java.util.concurrent.atomic.AtomicLong;

public abstract class TimeThread extends Thread {

	AtomicLong time;

	public TimeThread(long time) {
		super();
		this.time = new AtomicLong(time);
	}

	public TimeThread resetTime(long time) {
		this.time.set(time);
		return this;
	}

	@Override
	public final void run() {
		while (!this.isInterrupted() && System.currentTimeMillis() < time.get()) {
			try {
				Thread.sleep(time.get() - System.currentTimeMillis());
			} catch (InterruptedException e) {
				return;
			}
		}
		if (this.isInterrupted())
			return;
		realRun();
	}

	public abstract void realRun();
}
