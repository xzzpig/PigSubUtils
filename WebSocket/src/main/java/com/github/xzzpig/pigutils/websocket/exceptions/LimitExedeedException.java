package com.github.xzzpig.pigutils.websocket.exceptions;

import com.github.xzzpig.pigutils.websocket.framing.CloseFrame;

public class LimitExedeedException extends InvalidDataException {

	/**
	 * Serializable
	 */
	private static final long serialVersionUID = 6908339749836826785L;

	public LimitExedeedException() {
		super(CloseFrame.TOOBIG);
	}

	public LimitExedeedException(String s) {
		super(CloseFrame.TOOBIG, s);
	}

}
