package com.github.xzzpig.pigutils.pipeline;

public interface PipeLineSolver<T, R> {
    R solve(T t);
}
