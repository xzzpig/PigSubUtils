package com.github.xzzpig.pigutils.task.base;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.github.xzzpig.pigutils.annoiation.NotNull;
import com.github.xzzpig.pigutils.task.TaskObject;
import com.github.xzzpig.pigutils.task.TaskState;
import com.github.xzzpig.pigutils.task.TaskStream;

public class BaseTaskStream implements TaskStream {

	List<Consumer<TaskObject>> consumers;

	BaseTaskStream(@NotNull List<Consumer<TaskObject>> consumers) {
		this.consumers = new LinkedList<>(consumers);
	}

	@Override
	public TaskObject input(Object obj) {
		TaskObject taskObject;
		if (obj instanceof TaskObject)
			taskObject = (TaskObject) obj;
		else
			taskObject = new TaskObject().set("input", obj);
		Iterator<Consumer<TaskObject>> ir = consumers.iterator();
		while (ir.hasNext() && taskObject.getState() != TaskState.SKIPED && taskObject.getState() != TaskState.BREAK
				&& taskObject.getState() != TaskState.CONTINUE) {
			ir.next().accept(taskObject);
		}
		return taskObject;
	}

	@Override
	public TaskObject[] inputs(boolean parallel, Object... objs) {
		return inputs(parallel, Arrays.asList(objs));
	}

	@Override
	public TaskObject[] inputs(boolean parallel, @NotNull Collection<Object> objs) {
		if (parallel)
			return objs.parallelStream().map(this::input).filter(task -> task.getState() != TaskState.SKIPED)
					.toArray(TaskObject[]::new);
		else
			return objs.stream().map(this::input).filter(task -> task.getState() != TaskState.SKIPED)
					.toArray(TaskObject[]::new);
	}

	@Override
	public <T> T collect(Supplier<T> supplier, BiConsumer<T, TaskObject> consumer, Object... objs) {
		return collect(supplier, consumer, Arrays.asList(objs));
	}

	@Override
	public <T> T collect(Supplier<T> supplier, BiConsumer<T, TaskObject> consumer, Collection<Object> objs) {
		T t = supplier.get();
		objs.stream().map(this::input).filter(task -> task.getState() != TaskState.SKIPED)
				.forEach(task -> consumer.accept(t, task));
		return t;
	}

	@Override
	public void forEach(Consumer<TaskObject> consumer, Object... objs) {
		this.forEach(consumer, Arrays.asList(objs));
	}

	@Override
	public void forEach(Consumer<TaskObject> consumer, Collection<Object> objs) {
		objs.stream().map(this::input).filter(task -> task.getState() != TaskState.SKIPED).forEach(consumer);
	}

}
