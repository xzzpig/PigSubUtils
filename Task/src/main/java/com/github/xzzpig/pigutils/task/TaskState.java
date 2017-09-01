package com.github.xzzpig.pigutils.task;

/**
 * 可通过设置{@link TaskObject}的状态来对 {@link TaskStream}产生不同影响
 * 
 * @see TaskObject#setState(TaskState)
 */
public enum TaskState {
	/**
	 * {@link TaskObject}的默认状态
	 */
	DEFAULT,
	/**
	 * 终止 {@link TaskStream}并跳过当前 {@link TaskObject}<br/>
	 * ({@link TaskStream#input(Object)}不会影响)
	 */
	SKIPED,
	/**
	 * 终止<mark>当前</mark> {@link TaskStream} 并停止 for do_while while
	 * @see TaskStreamBuilder#for_(int, int, int)
	 * @see TaskStreamBuilder#while_(java.util.function.Predicate)
	 * @see TaskStreamBuilder#do_While(java.util.function.Predicate)
	 */
	BREAK,
	/**
	 * 终止<mark>当前</mark> {@link TaskStream}
	 * @see TaskStreamBuilder#for_(int, int, int)
	 * @see TaskStreamBuilder#while_(java.util.function.Predicate)
	 * @see TaskStreamBuilder#do_While(java.util.function.Predicate)
	 */
	CONTINUE
}
