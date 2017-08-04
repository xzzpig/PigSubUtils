package com.github.xzzpig.pigutils.pipeline;

import java.util.concurrent.LinkedBlockingDeque;

public class PipeLine<T> implements PipeNode<T> {
	/**
	 * 根据PipeLineCreater创建流水线
	 * 
	 * @param p
	 * @return 流水线，如果p=null,则返回null
	 */
	public static <T> PipeLine<T> createPipeLine(PipeLineCreater<T> p) {
		if (p == null)
			return null;
		return new PipeLine<>(p);
	}

	private PipeLineConsumer<?, T> consumer;
	private boolean finish;
	private PipeLine<?> head, next;
	private PipeLineLimiter<T> limiter;
	private PipeLineCreater<T> pc;
	private LinkedBlockingDeque<T> solvequrey;

	private PipeLineSolver<T, ?> solver;

	private PipeLine() {
		// solvequrey = blockint < 1 ? new LinkedBlockingDeque<>() : new
		// LinkedBlockingDeque<>(blockint);
	}

	private PipeLine(PipeLineCreater<T> p) {
		this();
		pc = p;
	}

	@SuppressWarnings("unchecked")
	private void add(Object object) {
		try {
			this.solvequrey.put((T) object);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.xzzpig.pigapi.pipeline.PipeEnd#finish(S,
	 * com.github.xzzpig.pigapi.pipeline.PipeLineConsumer)
	 */
	@Override
	public <S> S finish(S stayobj, PipeLineConsumer<S, T> c) {
		if (c == null)
			throw new IllegalArgumentException("PipeLineConsumer不可为空");
		this.consumer = c;
		PipeLine<?> p = this;
		while (p != null) {
			p.solvequrey = new LinkedBlockingDeque<>();
			p = p.head;
		}
		solveall();
		while (true) {
			try {
				T obj = solvequrey.take();
				if (limiter != null && limiter.isEnd(obj))
					obj = null;
				if (obj == null)
					break;
				c.consume(stayobj, obj);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.finish = true;
		return stayobj;
	}

	public boolean isFinish() {
		return finish;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.xzzpig.pigapi.pipeline.PipeNode#limit(com.github.xzzpig.pigapi
	 * .pipeline.PipeLineLimiter)
	 */
	@Override
	public PipeNode<T> limit(PipeLineLimiter<T> l) {
		this.limiter = l;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.xzzpig.pigapi.pipeline.PipeNode#next(com.github.xzzpig.pigapi.
	 * pipeline.PipeLineSolver)
	 */
	@Override
	public <R> PipeNode<R> next(PipeLineSolver<T, R> solver) {
		if (solver == null)
			throw new IllegalArgumentException("solver不可为空");
		if (this.solver != null)
			throw new IllegalArgumentException("不可调用该方法");
		PipeLine<R> r = new PipeLine<>();
		this.solver = solver;
		this.next = r;
		r.head = this;
		return r;
	}

	private void solve() {
		if (consumer != null) {
			return;
		}
		new Thread(() -> {
			while (true) {
				try {
					T obj = solvequrey.take();
					if (limiter != null && limiter.isEnd(obj)) {
						obj = null;
					}
					if (obj == null)
						break;
					next.add(solver.solve(solvequrey.take()));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			this.finish = true;
		}).start();
		next.solve();
	}

	private void solveall() {
		if (head != null) {
			head.solveall();
			return;
		}
		new Thread(() -> {
			T obj = pc.create();
			while (obj != null) {
				add(obj);
				obj = pc.create();
			}
		}).start();
		solve();
	}
}
