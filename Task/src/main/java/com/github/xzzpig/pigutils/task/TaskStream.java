package com.github.xzzpig.pigutils.task;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface TaskStream {
	/**
	 * Base void<br/>
	 * 将obj作为 {@link TaskObject}输入<br>
	 * 
	 * @return 处理完成的 {@link TaskObject}
	 * @param obj
	 *            如果 obj不是{@link TaskObject}则将obj作为
	 *            {@link TaskObject#getInput()}
	 */
	TaskObject input(Object obj);

	/**
	 * @param parallel
	 *            是否并行处理
	 * @see TaskStream#input(Object)
	 */
	TaskObject[] inputs(boolean parallel, Object... objs);

	/**
	 * @param parallel
	 *            是否并行处理
	 * @see TaskStream#input(Object)
	 */
	TaskObject[] inputs(boolean parallel, Collection<Object> objs);

	/**
	 * @see TaskStream#input(Object)
	 */
	<T> T collect(Supplier<T> supplier, BiConsumer<T, TaskObject> consumer, Object... objs);

	/**
	 * @see TaskStream#input(Object)
	 */
	<T> T collect(Supplier<T> supplier, BiConsumer<T, TaskObject> consumer, Collection<Object> objs);

	/**
	 * @see TaskStream#input(Object)
	 */
	void forEach(Consumer<TaskObject> consumer, Object... objs);

	/**
	 * @see TaskStream#input(Object)
	 */
	void forEach(Consumer<TaskObject> consumer, Collection<Object> objs);

}
