package com.github.xzzpig.pigutils.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class TransformManager {

	@FunctionalInterface
    public interface SimpleTransformer<F, R> extends Transformer<F, R> {
        R transform(F f);

		default R transform(F f, Map<Object, Object> extras) {
			return this.transform(f);
		}

	}

	@FunctionalInterface
    public interface Transformer<F, R> {
        default boolean accept(Object o) {
			try {
				@SuppressWarnings({ "unchecked", "unused" })
				F f = (F) o;
				return true;
			} catch (Exception e) {
				return false;
			}
		}

		default String mark() {
			return null;
        }

		default void onError(Exception error) {
        }

		default R transform(F f) {
			return transform(f, (Map<Object, Object>) null);
		}

		R transform(F f, Map<Object, Object> extras);

		default String useFor() {
			return "Default";
		}
	}

	public final static List<Transformer<?, ?>> transformers = new ArrayList<>();

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
			public String mark() {
				return mark;
			}

			@Override
			public void onError(Exception error) {
				if (errorConsumer != null)
					errorConsumer.accept(error);
			}

			@Override
			public R transform(F f, Map<Object, Object> extras) {
				return transformer.transform(f, extras);
			}

			@Override
			public String useFor() {
				return useFor;
			}
		});
	}

	public static <F, R> R transform(F from, Class<R> rc) {
		return transform(from, rc, null, null);
	}

	public static <F, R> R transform(F from, Class<R> rc, Map<Object, Object> extras) {
		return transform(from, rc, null, extras);
	}

	public static <F, R> R transform(F from, Class<R> rc, String useFor) {
		return transform(from, rc, useFor, null);
	}

	@SuppressWarnings("unchecked")
	public static <F, R> R transform(F from, Class<R> rc, String useFor, Map<Object, Object> extras) {
		for (Transformer<?, ?> transformer : transformers) {
			if (!transformer.accept(from))
				continue;
			if (useFor != null && !useFor.equalsIgnoreCase(transformer.useFor()))
				continue;
			R r = null;
			try {
				r = ((Transformer<F, R>) transformer).transform(from, extras);
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
}
