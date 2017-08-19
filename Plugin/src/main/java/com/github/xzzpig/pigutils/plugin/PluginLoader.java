package com.github.xzzpig.pigutils.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.github.xzzpig.pigutils.annoiation.NotNull;
import com.github.xzzpig.pigutils.annoiation.Nullable;

public interface PluginLoader {

	/**
	 * 几个内置的 {@link PluginLoader#order()} 建议值
	 */
	public static class PluginLoaderOrder {
		public static final int DEFAULT = 0;
		public static final int HIGH = 100;
		public static final int LOW = -100;
	}

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

	default void reloadPlugin(@NotNull Plugin plugin) {
		List<Plugin> subPlugins = plugin.getPluginManager().plugins.stream().filter(p -> p.isDependOn(plugin.getName()))
				.collect(ArrayList<Plugin>::new, ArrayList::add, ArrayList::addAll);
		Object rawObject = plugin.getRawObject();
		PluginManager manager = plugin.getPluginManager();
		manager.unloadPlugin(plugin);
		subPlugins.forEach(p -> p.getPluginManager().deepReloadPlugin(p));
		manager.loadPlugin(rawObject);
	}

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

	boolean needOtherUnloadNodify();

	default void otherunloadNodify(Plugin plugin) {
	}

	boolean needUnloadNodify();

	default void unloadNodify(@NotNull Plugin plugin) {
		plugin.onDisable();
		plugin.getPluginManager().plugins.stream().filter(p -> p.isDependOn(plugin.getName()))
				.collect(ArrayList<Plugin>::new, ArrayList::add, ArrayList::addAll)
				.forEach(plugin.getPluginManager()::unloadPlugin);
	}

	boolean needWaitNodify();

	default void waitNodify(@NotNull PluginManager manager, @NotNull Object obj, @Nullable Plugin plugin) {
	}

}
