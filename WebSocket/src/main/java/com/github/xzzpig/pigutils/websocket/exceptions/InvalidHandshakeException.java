package com.github.xzzpig.pigutils.websocket.exceptions;

import com.github.xzzpig.pigutils.websocket.framing.CloseFrame;

public class InvalidHandshakeException extends InvalidDataException {

	/**
	 * Serializable
	 */
	private static final long serialVersionUID = -1426533877490484964L;

	public InvalidHandshakeException() {
		super(CloseFrame.PROTOCOL_ERROR);
	}

	public InvalidHandshakeException(String arg0) {
		super(CloseFrame.PROTOCOL_ERROR, arg0);
	}

	public InvalidHandshakeException(String arg0, Throwable arg1) {
		super(CloseFrame.PROTOCOL_ERROR, arg0, arg1);
	}

	public InvalidHandshakeException(Throwable arg0) {
		super(CloseFrame.PROTOCOL_ERROR, arg0);
	}

}
