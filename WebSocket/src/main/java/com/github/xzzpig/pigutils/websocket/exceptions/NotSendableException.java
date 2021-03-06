package com.github.xzzpig.pigutils.websocket.exceptions;

public class NotSendableException extends RuntimeException {

	/**
	 * Serializable
	 */
	private static final long serialVersionUID = -6468967874576651628L;

	public NotSendableException() {
	}

	public NotSendableException(String message) {
		super(message);
	}

	public NotSendableException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotSendableException(Throwable cause) {
		super(cause);
	}

}
