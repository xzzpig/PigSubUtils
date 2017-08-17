package com.github.xzzpig.pigutils.plugin;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import com.github.xzzpig.pigutils.core.Registable;
import com.github.xzzpig.pigutils.plugin.PluginLoader.PluginLoadResult;

public class PluginManager implements Registable<PluginLoader> {

	public static final PluginManager DefaultPluginManager = new PluginManager();

	List<PluginLoader> pluginLoaders = new ArrayList<>();
	List<Plugin> plugins = new LinkedList<>();

	public PluginManager() {
	}

	@Override
	public PluginManager register(PluginLoader pluginLoader) {
		pluginLoaders.add(pluginLoader);
		pluginLoaders.sort((t1, t2) -> t1.order() - t2.order());
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
		AtomicReference<PluginLoadResult> aresult = new AtomicReference<>();
		Plugin p = pluginLoader.loadPlugin(this, obj, aresult);
		PluginLoadResult result = aresult.get();
		if (result == PluginLoadResult.SUCCESS) {
			plugins.add(p);
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

	public PluginManager unloadPlugin(Plugin plugin) {
		plugin.getPluginLoader().unloadPlugin(plugin);
		if (plugin.getPluginLoader().needUnloadNodify())
			plugin.getPluginLoader().unloadNodify(plugin);
		plugins.remove(plugin);
		return this;
	}

	private void nodiyOtherSuccess(Plugin p) {
		pluginLoaders.stream().filter(PluginLoader::needOtherSuccessNodify).forEach(pl -> pl.othersuccessNodify(p));
	}

	public PluginManager reloadPlugin(Plugin plugin) {
		plugin.onReload();
		return this;
	}

	public boolean isPluginLoaded(String name) {
		return plugins.stream().anyMatch(p -> p.getName().equals(name));
	}
	
	public Plugin getPlugin(String name){
		Optional<Plugin> pl = plugins.stream().filter(p -> p.getName().equals(name)).findFirst();
		if(pl.isPresent())
			return pl.get();
		return null;
	}
}