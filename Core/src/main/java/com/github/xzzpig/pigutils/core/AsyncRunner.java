package com.github.xzzpig.pigutils.core;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import com.github.xzzpig.pigutils.annoiation.NotNull;
import com.github.xzzpig.pigutils.annoiation.Nullable;

/**
 * 异步执行器
 * 
 * @author xzzpig
 */
public class AsyncRunner {

	@FunctionalInterface
	public static interface AsyncRunnable<R> {
		R run() throws Exception;
	}

	public static abstract class AsyncRunInstance<R> implements AsyncRunnable<R> {
		protected abstract void accept(RunResult<R> result);

		protected long timeout() {
			return -1;
		}

		private void exec() {
			try {
				R r = run();
				accept(new RunResult<R>(r, RunResult_SUCCESS, null));
			} catch (Exception e) {
				if (e instanceof InterruptedException) {
					accept(new RunResult<R>(null, RunResult_TIMEOUT, e));
					return;
				}
				accept(new RunResult<R>(null, RunResult_EXCEPTION, e));
			}
		}
	}

	public static final int RunResult_SUCCESS = 0;
	public static final int RunResult_TIMEOUT = 1;
	public static final int RunResult_EXCEPTION = 2;

	/**
	 * 执行结果
	 */
	public static class RunResult<R> {

		public final R result;
		public final int resultCode;
		public final Exception exception;

		RunResult(R result, int code, Exception error) {
			this.result = result;
			this.resultCode = code;
			this.exception = error;
		}

		@Override
		public String toString() {
			if (resultCode == RunResult_SUCCESS)
				return "Result:" + result;
			else if (resultCode == RunResult_EXCEPTION)
				return "Result:Exception|" + exception.toString();
			return "Result:Timeout";
		}
	}

	private void run(Thread thread) {
		Thread timeoutWatcher = null;
		AtomicLong time = new AtomicLong(-1);
		if (TimeoutSupport) {
			timeoutWatcher = new Thread() {
				@Override
				public void run() {
					while (!interrupted()) {
						synchronized (time) {
							try {
								time.wait();
							} catch (InterruptedException e) {
							}
						}
						long waittime = time.get();
						if (waittime > 0)
							synchronized (thread) {
								long ddl = time.addAndGet(System.currentTimeMillis());
								try {
									thread.wait(waittime);
									if (System.currentTimeMillis() >= ddl) {
										// Thread t = new Thread() {
										// @Override
										// public void run() {
										// AsyncRunner.this.run(this);
										// }
										// };
										// threadpool.add(t);
										// t.start();
										thread.interrupt();
										return;
									}
								} catch (InterruptedException e) {
								}
							}
					}
				}
			};
			timeoutWatcher.setDaemon(true);
			timeoutWatcher.start();
		}
		while (!thread.isInterrupted()) {
			AsyncRunInstance<?> asyncRunInstance = runList.pollFirst();
			if (asyncRunInstance != null) {
				time.set(asyncRunInstance.timeout());
				synchronized (time) {
					time.notifyAll();
				}
				asyncRunInstance.exec();
				synchronized (thread) {
					thread.notifyAll();
				}
			} else if (closed.get())
				break;
			else
				synchronized (runList) {
					try {
						runList.wait();
						if (threadpool.size() > poolsize.get()) {
							break;
						}
					} catch (InterruptedException e) {
						break;
					}
				}
		}
		threadpool.remove(thread);
		if (timeoutWatcher != null)
			timeoutWatcher.interrupt();
	}

	LinkedList<Thread> threadpool;

	AtomicInteger poolsize;

	public final boolean TimeoutSupport;

	/**
	 * @param poolsize
	 *            并发数
	 * @param timeoutSupport
	 *            是否支持timeout,如果支持将创建poolsize*2的线程,否则poolsize*1
	 */
	public AsyncRunner(int poolsize, boolean timeoutSupport) {
		this.TimeoutSupport = timeoutSupport;
		threadpool = new LinkedList<>();
		this.poolsize = new AtomicInteger(poolsize);
		runList = new LinkedList<>();
		updatePool();
	}

	LinkedList<AsyncRunInstance<?>> runList;

	/**
	 * 异步执行instance
	 */
	public AsyncRunner run(AsyncRunInstance<?> instance) {
		synchronized (runList) {
			runList.add(instance);
			runList.notifyAll();
		}
		return this;
	}

	/**
	 * @param runnable
	 *            异步执行的内容
	 * @param callback
	 *            执行完的回掉函数
	 * @param timeout
	 *            超时(需要 {@link AsyncRunner#TimeoutSupport}==true)
	 */
	public <T> AsyncRunner run(@NotNull AsyncRunnable<T> runnable, @Nullable Consumer<RunResult<T>> callback,
			long timeout) {
		return run(new AsyncRunInstance<T>() {
			@Override
			public T run() throws Exception {
				return runnable.run();
			}

			@Override
			public void accept(RunResult<T> result) {
				if (callback != null)
					callback.accept(result);
			}

			@Override
			protected long timeout() {
				return timeout;
			}
		});
	}

	void updatePool() {
		if (closed.get())
			return;
		synchronized (threadpool) {
			synchronized (runList) {
				runList.notifyAll();
			}
			Iterator<Thread> ir = threadpool.iterator();
			while (ir.hasNext()) {
				Thread t = ir.next();
				if (t.isInterrupted())
					ir.remove();
			}
			int size = poolsize.get();
			while (threadpool.size() < size) {
				Thread thread = new Thread() {
					@Override
					public void run() {
						AsyncRunner.this.run(this);
					}
				};
				threadpool.add(thread);
				thread.setDaemon(true);
				thread.start();
			}
		}
	}

	private AtomicBoolean closed = new AtomicBoolean(false);

	/**
	 * 待所有执行完后停止该runner(非阻塞)
	 */
	public AsyncRunner close() {
		closed.set(true);
		return this;
	}

	public int getPoolSize() {
		return poolsize.get();
	}

	public void join() {
		Thread t;
		while ((t = threadpool.pollFirst()) != null) {
			try {
				t.join();
			} catch (InterruptedException e) {
			}
		}
	}

	/**
	 * close().join()
	 */
	public void closed() {
		this.close().join();
	}

	/**
	 * 改变并发数<br/>
	 * 只有线程被挂起时能减小
	 */
	public AsyncRunner resizePool(int size) {
		poolsize.set(size);
		updatePool();
		return this;
	}

}
