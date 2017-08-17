package com.github.xzzpig.pigutils.plugin;

import java.util.concurrent.atomic.AtomicReference;

import com.github.xzzpig.pigutils.annoiation.NotNull;
import com.github.xzzpig.pigutils.annoiation.Nullable;

public interface PluginLoader {

	/**
	 * @return {@link PluginLoader}调用的优先级,越小越高
	 */
	public int order();

	public static enum PluginLoadResult {
		SUCCESS, FAILED, WAIT
	}

	@NotNull
	default String getName() {
		return getClass().getSimpleName();
	}

	@NotNull
	Plugin loadPlugin(@NotNull PluginManager manager, @NotNull Object obj,
			@NotNull AtomicReference<PluginLoadResult> result);

	void unloadPlugin(Plugin plugin);

	boolean accept(@Nullable Object obj);

	boolean needSuccessNodify();

	default void successNodify(PluginManager manager, Plugin plugin) {
	}

	boolean needOtherSuccessNodify();

	default void othersuccessNodify(Plugin plugin) {
	}

	boolean needFailedNodify();

	default void failedNodify(Object obj) {
	}

	boolean needUnloadNodify();

	default void unloadNodify(@NotNull Plugin plugin) {
	}

	boolean needWaitNodify();

	default void waitNodify(@NotNull PluginManager manager,@NotNull Object obj, @Nullable Plugin plugin) {
	}
}
