package com.github.xzzpig.pigutils.plugin;

public class PuginLoaderNoMarchedException extends RuntimeException {

	private static final long serialVersionUID = 3749265924190972965L;

	public PuginLoaderNoMarchedException() {
	}

	public PuginLoaderNoMarchedException(String message) {
		super(message);
	}

	public PuginLoaderNoMarchedException(Throwable cause) {
		super(cause);
	}

	public PuginLoaderNoMarchedException(String message, Throwable cause) {
		super(message, cause);
	}

	public PuginLoaderNoMarchedException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
