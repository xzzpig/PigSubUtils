package com.github.xzzpig.pigutils.thread;

import com.github.xzzpig.pigutils.event.Event;

public abstract class PigThreadEvent extends Event {
	private PigThread t;

	public PigThreadEvent(PigThread t) {
		this.t = t;
	}

	public PigThread getPigThread() {
		return t;
	}
}
