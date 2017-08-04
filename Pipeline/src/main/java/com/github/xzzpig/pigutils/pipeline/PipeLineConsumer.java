package com.github.xzzpig.pigutils.pipeline;

/**
 * @author xzzpig 作为流水线最后一个环节，处理并消耗数据
 * @param <S>
 *            保留在对象堆中，可作为累积器使用
 * @param <T>
 *            被消耗的对象
 */
public interface PipeLineConsumer<S, T> {
	void consume(S stayobj, T consumeobj);
}
