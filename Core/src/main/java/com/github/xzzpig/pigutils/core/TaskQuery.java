package com.github.xzzpig.pigutils.core;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import com.github.xzzpig.pigutils.annoiation.NotNull;

public class TaskQuery extends Thread {

	public static Task cast(@NotNull Runnable runnable) {
        return () -> {
            runnable.run();
            return true;
        };
    }

    public interface Task {
        /**
         * @return 任务是否执行成功
         */
        boolean run() throws Exception;
    }

	private AtomicBoolean closed = new AtomicBoolean(false);

	private LinkedList<Consumer<Exception>> errorList;

	private LinkedList<Runnable> startList, interruptedList;

	private LinkedList<Task> tasks;

	public TaskQuery() {
		tasks = new LinkedList<>();
		startList = new LinkedList<>();
		interruptedList = new LinkedList<>();
		errorList = new LinkedList<>();
	}

	public TaskQuery addOnError(Consumer<Exception> consumer) {
		if (errorList != null)
			errorList.add(consumer);
		return this;
	}

	public TaskQuery addOnInterruted(Runnable run) {
		if (interruptedList != null)
			interruptedList.add(run);
		return this;
	}

	public TaskQuery addOnStart(Runnable run) {
		if (startList != null)
			startList.add(run);
		return this;
	}

	public TaskQuery addTask(@NotNull Task... tasks) {
		if (tasks != null)
			for (Task task : tasks)
				this.tasks.addLast(task);
		synchronized (this.tasks) {
			this.tasks.notifyAll();
		}
		return this;
	}

	public TaskQuery close() {
		synchronized (tasks) {
			closed.set(true);
			tasks.notifyAll();
		}
		return this;
	}

	public TaskQuery insertTask(@NotNull Task task, int index) {
		if (index >= tasks.size())
			addTask(task);
		else
			tasks.add(index, task);
		synchronized (tasks) {
			tasks.notifyAll();
		}
		return this;
	}

	private void onError(Exception error) {
		errorList.forEach(c -> c.accept(error));
		errorList.clear();
		errorList = null;
	}

	private void oninterrupted() {
		interruptedList.forEach(Runnable::run);
		interruptedList.clear();
		interruptedList = null;
	}

	private void onStart() {
		startList.forEach(Runnable::run);
		startList.clear();
		startList = null;
	}

	@Override
	public void run() {
		Task task;
		onStart();
		while (!isInterrupted()) {
			task = tasks.pollFirst();
			if (task != null)
				try {
					if (!task.run())
						break;
				} catch (Exception e) {
					onError(e);
					break;
				}
			else if (closed.get())
				break;
			else
				synchronized (tasks) {
					try {
						tasks.wait();
					} catch (InterruptedException e) {
						break;
					}
				}
		}
		oninterrupted();
	}
}
