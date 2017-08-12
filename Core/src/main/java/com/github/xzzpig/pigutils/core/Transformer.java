package com.github.xzzpig.pigutils.core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@FunctionalInterface
public interface Transformer<F, R> {
	final static List<Transformer<?, ?>> transformers = new ArrayList<>();

	R transform(F f);

	default String useFor() {
		return "Default";
	};

	default String mark() {
		return null;
	}

	default void onError(Exception error) {
	}

	default boolean accept(Object o) {
		try {
			@SuppressWarnings({ "unchecked", "unused" })
			F f = (F) o;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static <F, R> void addTransformer(Transformer<F, R> transformer) {
		transformers.add(transformer);
	}

	public static <F, R> void addTransformer(Transformer<F, R> transformer, String useFor) {
		addTransformer(transformer, useFor, null, null);
	}

	public static <F, R> void addTransformer(Transformer<F, R> transformer, String useFor, String mark,
			Consumer<Exception> errorConsumer) {
		transformers.add(new Transformer<F, R>() {
			@Override
			public R transform(F f) {
				return transformer.transform(f);
			}

			@Override
			public String mark() {
				return mark;
			}

			@Override
			public String useFor() {
				return useFor;
			}

			@Override
			public void onError(Exception error) {
				if (errorConsumer != null)
					errorConsumer.accept(error);
			}
		});
	}

	@SuppressWarnings("unchecked")
	public static <F, R> R transform(F from, Class<R> rc, String useFor) {
		for (Transformer<?, ?> transformer : transformers) {
			if (!transformer.accept(from))
				continue;
			if (useFor != null && !useFor.equalsIgnoreCase(transformer.useFor()))
				continue;
			R r = null;
			try {
				r = ((Transformer<F, R>) transformer).transform(from);
			} catch (Exception e) {
				transformer.onError(e);
			}
			if (!rc.isInstance(r))
				r = null;
			if (r != null)
				return r;
		}
		return null;
	}

	public static <F, R> R transform(F from, Class<R> rc) {
		return transform(from, rc, null);
	}
}
