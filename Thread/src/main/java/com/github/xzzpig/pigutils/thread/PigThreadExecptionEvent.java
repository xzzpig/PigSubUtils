package com.github.xzzpig.pigutils.thread;

public class PigThreadExecptionEvent extends PigThreadEvent {

	private Exception e;

	public PigThreadExecptionEvent(PigThread t, Exception e) {
		super(t);
		this.e = e;
	}

	public Exception getException() {
		return e;
	}
}
