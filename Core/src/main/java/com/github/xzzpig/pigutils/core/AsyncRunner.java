package com.github.xzzpig.pigutils.core;

import java.util.function.Consumer;

public class AsyncRunner<T> implements Runnable {

	@FunctionalInterface
	public static interface AsyncRunnable<R> {
		R run() throws Exception;
	}

	public static final int RunResult_SUCCESS = 0;
	public static final int RunResult_TIMEOUT = 1;
	public static final int RunResult_EXCEPTION = 2;

	public static class RunResult<R> {

		public final R result;
		public final int resultCode;
		public final Exception exception;

		RunResult(R result, int code, Exception error) {
			this.result = result;
			this.resultCode = code;
			this.exception = error;
		}
	}

	AsyncRunnable<T> supplier;
	Consumer<RunResult<T>> callback;
	int timeout;

	public AsyncRunner(AsyncRunnable<T> asyncRunnable, Consumer<RunResult<T>> callback, int timeout) {
		this.supplier = asyncRunnable;
		this.callback = callback;
		this.timeout = timeout;
	}

	@Override
	public void run() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				T res = null;
				try {
					res = supplier.run();
					callback.accept(new RunResult<T>(res, RunResult_SUCCESS, null));
				} catch (Exception e) {
					if (!(e instanceof InterruptedException))
						callback.accept(new RunResult<T>(res, RunResult_EXCEPTION, e));
				}
			}
		});
		if (timeout > 0) {
			new Thread(() -> {
				try {
					Thread.sleep(timeout);
					if (thread.isAlive()) {
						thread.interrupt();
						callback.accept(new RunResult<T>(null, RunResult_TIMEOUT, null));
					}
				} catch (Exception e) {
				}
			}).start();
		}
		thread.setDaemon(true);
		thread.start();
	}

}
