package com.github.xzzpig.pigutils.pipeline;

public interface PipeLineSolver<T, R> {
	public R solve(T t);
}
