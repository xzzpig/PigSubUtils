package com.github.xzzpig.pigutils.plugin;

import java.util.concurrent.atomic.AtomicReference;

import com.github.xzzpig.pigutils.annoiation.NotNull;
import com.github.xzzpig.pigutils.annoiation.Nullable;

public interface PluginLoader {
	/**
	 * @return {@link PluginLoader}调用的优先级,越小越高
	 */
	public int order();

	@NotNull
	Plugin loadPlugin(@NotNull PluginManager manager, @NotNull Object obj,
			@NotNull AtomicReference<PluginLoadResult> result);

	void unloadPlugin(Plugin plugin);

	void reloadPlugin(@NotNull Plugin plugin);

	boolean accept(@Nullable Object obj);

	boolean needSuccessNodify();

	void successNodify(PluginManager manager, Plugin plugin);

	boolean needOtherSuccessNodify();

	void othersuccessNodify(Plugin plugin);

	boolean needFailedNodify();

	void failedNodify(Object obj);

	boolean needOtherUnloadNodify();

	void otherunloadNodify(Plugin plugin);

	boolean needUnloadNodify();

	void unloadNodify(@NotNull Plugin plugin);

	boolean needWaitNodify();

	void waitNodify(@NotNull PluginManager manager, @NotNull Object obj, @Nullable Plugin plugin);

}
