package com.github.xzzpig.pigutils.thread;

import com.github.xzzpig.pigutils.event.EventAdapter;
import com.github.xzzpig.pigutils.event.EventBus;
import com.github.xzzpig.pigutils.event.EventRunner;

public class PigThread extends Thread implements EventAdapter {
	public EventBus e = new EventBus();

	public PigThread(Runnable r) {
		super(r);
	}

	public PigThread(Thread t) {
		super(t::run);
	}

	@Override
	public EventBus getEventBus() {
		return e;
	}

	public PigThread regExceptionEvent(EventRunner<PigThreadExecptionEvent> runner) {
		this.regRunner(runner);
		return this;
	}

	public PigThread regFinishEvent(EventRunner<PigThreadFinishEvent> runner) {
		this.regRunner(runner);
		return this;
	}

	public PigThread regStartEvent(EventRunner<PigThreadStartEvent> runner) {
		this.regRunner(runner);
		return this;
	}

	@Override
	public void run() {
		this.callEvent(new PigThreadStartEvent(this));
		try {
			super.run();
		} catch (Exception e) {
			this.callEvent(new PigThreadExecptionEvent(this, e));
		}
		this.callEvent(new PigThreadFinishEvent(this));
	}
}
