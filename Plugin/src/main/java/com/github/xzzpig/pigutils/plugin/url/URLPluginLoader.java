package com.github.xzzpig.pigutils.plugin.url;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import com.github.xzzpig.pigutils.plugin.Plugin;
import com.github.xzzpig.pigutils.plugin.PluginLoadResult;
import com.github.xzzpig.pigutils.plugin.PluginManager;
import com.github.xzzpig.pigutils.plugin.base.BasePlugin;
import com.github.xzzpig.pigutils.plugin.base.BasePluginLoader;

public abstract class URLPluginLoader extends BasePluginLoader {

	private class WaitFakePlugin extends BasePlugin {

		public WaitFakePlugin(URLPluginInfo info) {
			this.depends = info.depends;
			this.info = info.infos;
			this.name = info.name;
			this.pluginLoader = URLPluginLoader.this;
		}

		@Override
		public void onDisable() {
		}

		@Override
		public void onEnable() {
		}
	}

	protected URLPluginLoader() {
	}

	@Override
	public Plugin loadPlugin(PluginManager manager, Object obj, AtomicReference<PluginLoadResult> result) {
		Object[] objs;
		URL url;
		URLPluginInfo clPluginInfo;
		try {
			objs = (Object[]) obj;
			url = (URL) objs[0];
			clPluginInfo = (URLPluginInfo) objs[1];
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
		if (Arrays.asList(clPluginInfo.depends).stream().filter(name -> !manager.isPluginLoaded(name)).count() != 0) {
			result.set(PluginLoadResult.WAIT);
			return new WaitFakePlugin(clPluginInfo);
		}
		@SuppressWarnings("resource")
		PluginClassloader classloader = new PluginClassloader(this.getClass().getClassLoader(), url);
		classloader.addParents(Arrays.asList(clPluginInfo.depends).stream().map(manager::getPlugin)
				.filter(p -> p.getClass().getClassLoader() instanceof URLClassLoader)
				.map(p -> (URLClassLoader) p.getClass().getClassLoader()).toArray(URLClassLoader[]::new));
		try {
			Class<? extends URLPlugin> clazz = classloader.loadClass(clPluginInfo.mainClass)
					.asSubclass(URLPlugin.class);
			URLPlugin plugin = clazz.newInstance();
			plugin.loadInfo(clPluginInfo);
			result.set(PluginLoadResult.SUCCESS);
			return plugin;
		} catch (ClassNotFoundException | ClassCastException | InstantiationException | IllegalAccessException e) {
			throw new URLPluginLoaderNewPluginException(e);
		}
	}

	@Override
	public void unloadPlugin(Plugin plugin) {
		if (!(plugin instanceof URLPlugin))
			return;
		if (plugin.getClass().getClassLoader() instanceof PluginClassloader) {
			try {
				((PluginClassloader) plugin.getClass().getClassLoader()).close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
