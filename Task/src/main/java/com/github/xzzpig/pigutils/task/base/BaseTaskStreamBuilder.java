package com.github.xzzpig.pigutils.task.base;

import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import com.github.xzzpig.pigutils.task.TaskObject;
import com.github.xzzpig.pigutils.task.TaskState;
import com.github.xzzpig.pigutils.task.TaskStream;
import com.github.xzzpig.pigutils.task.TaskStreamBuilder;

public class BaseTaskStreamBuilder implements TaskStreamBuilder {

	private LinkedList<Consumer<TaskObject>> steps;

	private BaseTaskStreamBuilder builderRef;

	public BaseTaskStreamBuilder() {
		steps = new LinkedList<>();
		builderRef = this;
	}

	@Override
	public TaskStreamBuilder endWhile() {
		if (this.builderRef.isSelf()) {
			this.builderRef = this;
		} else {
			this.builderRef.endWhile();
		}
		return this;
	}

	@Override
	public TaskStreamBuilder endFor() {
		if (this.builderRef.isSelf()) {
			this.builderRef = this;
		} else {
			this.builderRef.endFor();
		}
		return this;
	}

	@Override
	public TaskStreamBuilder endIf() {
		if (this.builderRef.isSelf()) {
			this.builderRef = this;
			this.then(task -> task.remove(this.hashCode() + "if"));
		} else {
			this.builderRef.endIf();
		}
		return this;
	}

	@Override
	public TaskStreamBuilder else_() {
		if (this.isSelf())
			throw new RuntimeException(new NoSuchMethodException("IfTaskStreamBuilder#else_ can not be invoked"));
		if (this.builderRef.isSelf()) {
			final BaseTaskStreamBuilder builder = new BaseTaskStreamBuilder();
			steps.add((TaskObject task) -> {
				if (!task.get(this.hashCode() + "if", Boolean.class, true))
					builder.build().input(task);
			});
			builderRef = builder;
		} else {
			this.builderRef.else_();
		}
		return this;
	}

	@Override
	public TaskStream build() {
		return new BaseTaskStream(steps);
	}

	@Override
	public TaskStreamBuilder then(Consumer<TaskObject> consumer) {
		if (builderRef == this) {
			steps.add(consumer);
		} else {
			builderRef.then(consumer);
		}
		return this;
	}

	@Override
	public TaskStreamBuilder then(Runnable run) {
		return this.then(task -> run.run());
	}

	@Override
	public TaskStreamBuilder if_(Predicate<TaskObject> predicate) {
		if (builderRef == this) {
			final BaseTaskStreamBuilder builder = new BaseTaskStreamBuilder();
			this.then(task -> {
				if (predicate.test(task))
					builder.build().input(task);
				else
					task.set(this.hashCode() + "if", false);
			});
			builderRef = builder;
		} else {
			builderRef.if_(predicate);
		}
		return this;
	}

	private boolean isSelf() {
		return this == builderRef;
	}

	@Override
	public TaskStreamBuilder for_(int start, int end, int step) {
		if (this == builderRef) {
			final BaseTaskStreamBuilder builder = new BaseTaskStreamBuilder();
			steps.add(task -> {
				TaskStream stream = builder.build();
				for (int i = start; i < end && task.getState() != TaskState.BREAK
						&& task.getState() != TaskState.SKIPED; i += step) {
					task.set("forIndex", i);
					stream.input(task);
					if (task.getState() == TaskState.CONTINUE)
						task.setState(TaskState.DEFAULT);
				}
				task.remove("forIndex");
				if (task.getState() == TaskState.BREAK)
					task.setState(TaskState.DEFAULT);
				else if (task.getState() == TaskState.CONTINUE)
					task.setState(TaskState.DEFAULT);
			});
			builderRef = builder;
		} else {
			this.builderRef.for_(start, end, step);
		}
		return this;
	}

	@Override
	public TaskStreamBuilder while_(Predicate<TaskObject> predicate) {
		if (this == builderRef) {
			final BaseTaskStreamBuilder builder = new BaseTaskStreamBuilder();
			steps.add(task -> {
				TaskStream stream = builder.build();
				while (predicate.test(task) && task.getState() != TaskState.BREAK
						&& task.getState() != TaskState.SKIPED) {
					stream.input(task);
					if (task.getState() == TaskState.CONTINUE)
						task.setState(TaskState.DEFAULT);
				}
				if (task.getState() == TaskState.BREAK)
					task.setState(TaskState.DEFAULT);
				else if (task.getState() == TaskState.CONTINUE)
					task.setState(TaskState.DEFAULT);
			});
			builderRef = builder;
		} else {
			this.builderRef.while_(predicate);
		}
		return this;
	}

	@Override
	public TaskStreamBuilder do_While(Predicate<TaskObject> predicate) {
		if (this == builderRef) {
			final BaseTaskStreamBuilder builder = new BaseTaskStreamBuilder();
			steps.add(task -> {
				TaskStream stream = builder.build();
				do {
					stream.input(task);
					if (task.getState() == TaskState.CONTINUE)
						task.setState(TaskState.DEFAULT);
				} while (predicate.test(task) && task.getState() != TaskState.BREAK
						&& task.getState() != TaskState.SKIPED);
				if (task.getState() == TaskState.BREAK)
					task.setState(TaskState.DEFAULT);
				else if (task.getState() == TaskState.CONTINUE)
					task.setState(TaskState.DEFAULT);
			});
			builderRef = builder;
		} else {
			this.builderRef.while_(predicate);
		}
		return this;
	}

	public TaskStreamBuilder setState(TaskState state) {
		return this.then(task -> task.setState(state));
	}

	@Override
	public TaskStreamBuilder break_() {
		return setState(TaskState.BREAK);
	}

	@Override
	public TaskStreamBuilder skip() {
		return setState(TaskState.SKIPED);
	}

	@Override
	public TaskStreamBuilder continue_() {
		return setState(TaskState.CONTINUE);
	}

	@Override
	public TaskStreamBuilder filter(Predicate<TaskObject> predicate) {
		return this.if_(predicate.negate()).skip().endIf();
	}

	@Override
	public TaskStreamBuilder skip(Predicate<TaskObject> predicate) {
		return this.if_(predicate).skip().endIf();
	}

	@Override
	public TaskStreamBuilder then(TaskStream substream) {
		return this.then(substream::input);
	}

	@Override
	public TaskStreamBuilder map(String key, Function<Object, Object> function) {
		return this.map(key, function, Object.class, Object.class);
	}

	@Override
	public <T, R> TaskStreamBuilder map(String key, Function<T, R> function, Class<T> clazz1, Class<R> clazz2) {
		return this.then(task -> task.set(key, function.apply(task.get(key, clazz1))));
	}

	@Override
	public TaskStreamBuilder elseIf(Predicate<TaskObject> predicate) {
		if (this.isSelf())
			throw new RuntimeException(new NoSuchMethodException("IfTaskStreamBuilder#else_ can not be invoked"));
		if (this.builderRef.isSelf()) {
			final BaseTaskStreamBuilder builder = new BaseTaskStreamBuilder();
			steps.add(task -> {
				if (task.get(this.hashCode() + "if", Boolean.class, true) == false)
					if (predicate.test(task)) {
						builder.build().input(task);
						task.set(this.hashCode() + "if", true);
					} else
						task.set(this.hashCode() + "if", false);
			});
			builderRef = builder;
		} else {
			this.builderRef.else_();
		}
		return this;
	}

}
