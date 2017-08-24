package com.github.xzzpig.pigutils.core;

import java.util.LinkedList;
import java.util.function.Consumer;

import com.github.xzzpig.pigutils.annoiation.NotNull;

public class TaskQuery extends Thread {

	public static interface Task {
		/**
		 * @return 任务是否执行成功
		 */
		boolean run() throws Exception;
	}
	public static Task cast(@NotNull Runnable runnable) {
		return new Task() {
			@Override
			public boolean run() throws Exception {
				runnable.run();
				return true;
			}
		};
	}
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
		return this;
	}

	public TaskQuery insertTask(@NotNull Task task, int index) {
		if (index >= tasks.size())
			addTask(task);
		else
			tasks.add(index, task);
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
		}
		oninterrupted();
	}
	
}
