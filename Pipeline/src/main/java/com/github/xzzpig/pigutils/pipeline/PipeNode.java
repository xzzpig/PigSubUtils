package com.github.xzzpig.pigutils.pipeline;

/**
 * 流水线的处理节点
 * 
 * @author xzzpig
 * @param <T>
 *            被处理对象
 */
public interface PipeNode<T> {

	/**
	 * 
	 * 
	 * @param stayobj
	 *            保留在堆中，可作为累积器使用
	 * @param c
	 * @return stayobj
	 */
	<S> S finish(S stayobj, PipeLineConsumer<S, T> c);

	/**
	 * 用于限制
	 * 
	 * @param l
	 * @return this
	 */
	PipeNode<T> limit(PipeLineLimiter<T> l);

	/**
	 * 处理对象
	 * 
	 * @param solver
	 * @return 对象被处理的结果，用于下个处理
	 */
	<R> PipeNode<R> next(PipeLineSolver<T, R> solver);
}