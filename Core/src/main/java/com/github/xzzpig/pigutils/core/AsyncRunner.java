package com.github.xzzpig.pigutils.core;

import com.github.xzzpig.pigutils.annoiation.NotNull;
import com.github.xzzpig.pigutils.annoiation.Nullable;

import java.io.Closeable;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * 异步执行器
 *
 * @author xzzpig
 */
public class AsyncRunner implements Closeable {

    public static final int RunResult_EXCEPTION = 2;
    public static final int RunResult_SUCCESS = 0;
    public static final int RunResult_TIMEOUT = 1;
    public final boolean TimeoutSupport;
    AtomicInteger poolsize;
    ConcurrentLinkedQueue<AsyncRunInstance<?>> runList;
    ConcurrentLinkedQueue<Thread> threadpool;
    private AtomicBoolean closed = new AtomicBoolean(false);

    /**
     * @param poolsize       并发数
     * @param timeoutSupport 是否支持timeout,如果支持将创建poolsize*2的线程,否则poolsize*1
     */
    public AsyncRunner(int poolsize, boolean timeoutSupport) {
        this.TimeoutSupport = timeoutSupport;
        threadpool = new ConcurrentLinkedQueue<>();
        this.poolsize = new AtomicInteger(poolsize);
        runList = new ConcurrentLinkedQueue<>();
        updatePool();
    }

    /**
     * 待所有执行完后停止该runner(非阻塞)
     */
    public AsyncRunner closed() {
        closed.set(true);
        return this;
    }

    /**
     * close()&join()
     */
    @Override
    public void close() {
        this.close();
        this.join();
    }

    public int getPoolSize() {
        return poolsize.get();
    }

    public void join() {
        Thread t;
        while ((t = threadpool.poll()) != null) {
            try {
                t.join();
            } catch (InterruptedException e) {
            }
        }
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
     * @param runnable 异步执行的内容
     * @param callback 执行完的回掉函数
     * @param timeout  超时(需要 {@link AsyncRunner#TimeoutSupport}==true)
     */
    public <T> AsyncRunner run(@NotNull AsyncRunnable<T> runnable, @Nullable Consumer<RunResult<T>> callback,
                               long timeout) {
        return run(new AsyncRunInstance<T>() {
            @Override
            public void accept(RunResult<T> result) {
                if (callback != null)
                    callback.accept(result);
            }

            @Override
            public T run() throws Exception {
                return runnable.run();
            }

            @Override
            protected long timeout() {
                return timeout;
            }
        });
    }

    private void run(Thread thread) {
        Thread timeoutWatcher = null;
        AtomicLong time = new AtomicLong(-1);
        if (TimeoutSupport) {
            timeoutWatcher = new Thread(() -> {
                while (!Thread.interrupted()) {
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
            });
            timeoutWatcher.setDaemon(true);
            timeoutWatcher.start();
        }
        while (!thread.isInterrupted()) {
            AsyncRunInstance<?> asyncRunInstance = runList.poll();
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

    void updatePool() {
        if (closed.get())
            return;
        synchronized (threadpool) {
            synchronized (runList) {
                runList.notifyAll();
            }
            threadpool.removeIf(Thread::isInterrupted);
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

    @FunctionalInterface
    public interface AsyncRunnable<R> {
        R run() throws Exception;
    }

    public static abstract class AsyncRunInstance<R> implements AsyncRunnable<R> {
        protected abstract void accept(RunResult<R> result);

        private void exec() {
            try {
                R r = run();
                accept(new RunResult<>(r, RunResult_SUCCESS, null));
            } catch (Exception e) {
                if (e instanceof InterruptedException) {
                    accept(new RunResult<>(null, RunResult_TIMEOUT, e));
                    return;
                }
                accept(new RunResult<>(null, RunResult_EXCEPTION, e));
            }
        }

        protected long timeout() {
            return -1;
        }
    }

    /**
     * 执行结果
     */
    public static class RunResult<R> {

        public final Exception exception;
        public final R result;
        public final int resultCode;

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
}
