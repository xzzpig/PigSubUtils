package com.github.xzzpig.pigutils;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class YieldIterator<E> implements Iterator<E>, Iterable<E> {

	public class YieldAdapter {
		public synchronized E adapt(E e) {
			item = e;
			adapted.set(true);
			while (adapted.get())
				;
			return e;
		}
	}

	Thread thread;
	Consumer<YieldAdapter> consumer;
	YieldAdapter adapter;
	E item;
	AtomicBoolean adapted;
	boolean preNexted;

	E nextItem;

	public YieldIterator(Consumer<YieldAdapter> c) {
		consumer = c;
		this.adapter = new YieldAdapter();
		adapted = new AtomicBoolean(false);
	}

	@Override
	public boolean hasNext() {
		if (consumer == null)
			return false;
		if (thread == null)
			return true;
		if (!preNexted) {
			nextItem = next();
			preNexted = true;
		}
		return thread.isAlive();
	}

	@Override
	public YieldIterator<E> iterator() {
		return this;
	}

	@Override
	public E next() {
		if (consumer == null)
			return null;
		if (thread == null) {
			thread = new Thread(() -> consumer.accept(adapter));
			thread.start();
		}
		if (preNexted) {
			preNexted = false;
			return nextItem;
		}
		while ((!adapted.get()) && thread.isAlive())
			;
		E e = item;
		item = null;
		adapted.set(false);
		return e;
	}

}
