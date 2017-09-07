package com.github.xzzpig.pigutils.plugin;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import com.github.xzzpig.pigutils.annoiation.NotNull;
import com.github.xzzpig.pigutils.annoiation.Nullable;
import com.github.xzzpig.pigutils.core.Registable;
import com.github.xzzpig.pigutils.plugin.java.JavaPluginLoader;
import com.github.xzzpig.pigutils.plugin.script.ScriptPluginLoader;

public class PluginManager implements Registable<PluginLoader> {

	/**
	 * 几个内置的 {@link PluginLoader#order()} 建议值
	 */
	public static class PluginLoaderOrder {
		public static final int DEFAULT = 0;
		public static final int HIGH = 100;
		public static final int LOW = -100;
	}

	public static final PluginManager DefaultPluginManager = new PluginManager().register(new JavaPluginLoader())
			.register(new ScriptPluginLoader());

	protected List<PluginLoader> pluginLoaders = new ArrayList<>();
	protected List<Plugin> plugins = new LinkedList<>();

	public PluginManager() {
	}

	@Override
	public PluginManager register(PluginLoader pluginLoader) {
		pluginLoaders.add(pluginLoader);
        pluginLoaders.sort(Comparator.comparingInt(PluginLoader::order));
        return this;
	}

	@Override
	public PluginManager unregister(PluginLoader pluginLoader) {
		pluginLoaders.remove(pluginLoader);
		return this;
	}

	public PluginManager loadPlugin(Object obj) {
		Optional<PluginLoader> loader = pluginLoaders.stream().filter(pl -> pl.accept(obj)).findFirst();
		if (!loader.isPresent())
			throw new PuginLoaderNoMarchedException();
		PluginLoader pluginLoader = loader.get();
		AtomicReference<PluginLoadResult> aresult = new AtomicReference<>(PluginLoadResult.SUCCESS);
		Plugin p = pluginLoader.loadPlugin(this, obj, aresult);
		PluginLoadResult result = aresult.get();
		if (result == PluginLoadResult.SUCCESS) {
			plugins.add(p);
			p.setRawObject(obj);
			if (pluginLoader.needSuccessNodify())
				pluginLoader.successNodify(this, p);
			nodiyOtherSuccess(p);
		} else if (result == PluginLoadResult.FAILED) {
			if (pluginLoader.needFailedNodify())
				pluginLoader.failedNodify(obj);
		} else if (result == PluginLoadResult.WAIT) {
			if (pluginLoader.needWaitNodify())
				pluginLoader.waitNodify(this, obj, p);
		}
		return this;
	}

	public PluginManager unloadPlugin(@Nullable Plugin plugin) {
		if (plugin == null || !plugins.contains(plugin))
			return this;
		plugin.getPluginLoader().unloadPlugin(plugin);
		if (plugin.getPluginLoader().needUnloadNodify())
			plugin.getPluginLoader().unloadNodify(plugin);
		nodifyOtherUnload(plugin);
		plugins.remove(plugin);
		return this;
	}

	public PluginManager unloadPlugin(@NotNull String name) {
		return unloadPlugin(getPlugin(name));
	}

	protected void nodiyOtherSuccess(Plugin p) {
		pluginLoaders.stream().filter(PluginLoader::needOtherSuccessNodify).forEach(pl -> pl.othersuccessNodify(p));
	}

	protected void nodifyOtherUnload(Plugin p) {
		pluginLoaders.stream().filter(PluginLoader::needOtherUnloadNodify).forEach(pl -> pl.otherunloadNodify(p));
	}

	public PluginManager reloadPlugin(Plugin plugin) {
		if (plugin == null)
			return this;
		if (plugin.onReload())
			deepReloadPlugin(plugin);
		return this;
	}

	public PluginManager deepReloadPlugin(Plugin plugin) {
		plugin.getPluginLoader().reloadPlugin(plugin);
		return this;
	}

	public boolean isPluginLoaded(String name) {
		return plugins.stream().anyMatch(p -> p.getName().equals(name));
	}

	public @Nullable Plugin getPlugin(String name) {
		Optional<Plugin> pl = plugins.stream().filter(p -> p.getName().equals(name)).findFirst();
        return pl.orElse(null);
    }

	public PluginManager reloadPlugin(String name) {
		return reloadPlugin(getPlugin(name));
	}

	public String[] listPlugins() {
        return plugins.stream().map(Plugin::getName).toArray(String[]::new);
    }

	public Stream<Plugin> getPluginStream() {
		return plugins.stream();
	}
}