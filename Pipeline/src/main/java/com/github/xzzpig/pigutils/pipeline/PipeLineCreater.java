package com.github.xzzpig.pigutils.pipeline;

/**
 * @author xzzpig 用于生成流水线的每个待处理对象 将被不断读取直到返回null
 * @param <T>
 *            待处理对象类型
 */
public interface PipeLineCreater<T> {
	/**
	 * @return 每个待处理对象
	 */
	public T create();
}
