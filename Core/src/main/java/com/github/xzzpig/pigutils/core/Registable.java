package com.github.xzzpig.pigutils.core;

public interface Registable<T> {
	Registable<T> register(T t);
	Registable<T> unregister(T t);
}
