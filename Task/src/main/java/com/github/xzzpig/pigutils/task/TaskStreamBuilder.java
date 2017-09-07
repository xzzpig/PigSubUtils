package com.github.xzzpig.pigutils.task;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface TaskStreamBuilder {
	/**
	 * 按照之前所添加的Task生成 {@link TaskStream}
	 */
	TaskStream build();

	/**
	 * 将consumer添加到tasks中
	 */
	TaskStreamBuilder then(Consumer<TaskObject> consumer);

	/**
	 * 结束If
	 */
	TaskStreamBuilder endIf();

	/**
	 * 当 {@link TaskStreamBuilder#if_(Predicate)}条件不成立时执行之后到
	 * {@link TaskStreamBuilder#endIf()}之间的tasks
	 * 
	 * @see TaskStreamBuilder#if_(Predicate)
	 */
	TaskStreamBuilder else_();

	/**
	 * 将run添加到tasks中
	 */
	TaskStreamBuilder then(Runnable run);

	/**
	 * 开始If<br/>
	 * 在
	 * {@link TaskStreamBuilder#if_(Predicate)}到{@link TaskStreamBuilder#endIf()}之间添加的Task都将包装在一个新的
	 * {@link TaskStream}中<br/>
	 * 并将此{@link TaskStream#input(Object)}方法添加到tasks中
	 * 
	 * @see TaskStreamBuilder#endIf()
	 * @see TaskStreamBuilder#else_()
	 * @see TaskStreamBuilder#then(Consumer)
	 * @see TaskStream#input(Object)
	 */
	TaskStreamBuilder if_(Predicate<TaskObject> predicate);
	
	TaskStreamBuilder elseIf(Predicate<TaskObject> predicate);

	/**
	 * 开始For<br/>
	 * 原理同 {@link TaskStreamBuilder#if_(Predicate)}<br/>
	 * {@link TaskStreamBuilder#break_()}({@link TaskState#BREAK}->{@link TaskObject#setState(TaskState)})可中断当前For
	 * {@link TaskStreamBuilder#continue_()}({@link TaskState#CONTINUE}->{@link TaskObject#setState(TaskState)})可跳过当前循环
	 * 
	 * @see TaskStreamBuilder#endFor()
	 * @see TaskStreamBuilder#if_(Predicate)
	 * @see TaskState#BREAK
	 * @see TaskState#CONTINUE
	 */
	TaskStreamBuilder for_(int start, int end, int step);

	/**
	 * 结束for
	 */
	TaskStreamBuilder endFor();

	/**
	 * 开始while<br/>
	 * 原理同 {@link TaskStreamBuilder#for_(int, int, int)}<br/>
	 * {@link TaskStreamBuilder#break_()}({@link TaskState#BREAK}->{@link TaskObject#setState(TaskState)})可中断当前while
	 * {@link TaskStreamBuilder#continue_()}({@link TaskState#CONTINUE}->{@link TaskObject#setState(TaskState)})可跳过当前循环
	 * 
	 * @see TaskStreamBuilder#endWhile()
	 * @see TaskStreamBuilder#if_(Predicate)
	 * @see TaskState#BREAK
	 * @see TaskState#CONTINUE
	 */
	TaskStreamBuilder while_(Predicate<TaskObject> predicate);

	/**
	 * @see TaskStreamBuilder#while_(Predicate)
	 */
	TaskStreamBuilder do_While(Predicate<TaskObject> predicate);

	TaskStreamBuilder break_();

	TaskStreamBuilder skip();

	TaskStreamBuilder continue_();

	/**
	 * 结束while/do_while
	 */
	TaskStreamBuilder endWhile();

	/**
	 * {@link Predicate#test(Object)}==false的 {@link TaskObject}将被skip
	 */
	TaskStreamBuilder filter(Predicate<TaskObject> predicate);

	/**
	 * {@link Predicate#test(Object)}==true的 {@link TaskObject}将被skip
	 */
	TaskStreamBuilder skip(Predicate<TaskObject> predicate);

	TaskStreamBuilder then(TaskStream substream);

	TaskStreamBuilder map(String key, Function<Object, Object> function);

	<T, R> TaskStreamBuilder map(String key, Function<T, R> function, Class<T> clazz1, Class<R> clazz2);

}
