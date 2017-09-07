package com.github.xzzpig.pigutils.plugin.script;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.github.xzzpig.pigutils.plugin.Plugin;
import com.github.xzzpig.pigutils.plugin.PluginLoadResult;
import com.github.xzzpig.pigutils.plugin.PluginManager;
import com.github.xzzpig.pigutils.plugin.PluginManager.PluginLoaderOrder;
import com.github.xzzpig.pigutils.plugin.base.BasePlugin;
import com.github.xzzpig.pigutils.plugin.base.BasePluginLoader;
import com.github.xzzpig.pigutils.plugin.url.PluginClassloader;
import com.github.xzzpig.pigutils.plugin.url.URLPlugin;

public class ScriptPluginLoader extends BasePluginLoader {

	public static PluginClassloader Classloader4ScriptManager = new PluginClassloader(
			ScriptPluginLoader.class.getClassLoader(), new URL[0]);

	public ScriptPluginLoader() {
	}

	@Override
	public int order() {
		return PluginLoaderOrder.HIGH;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Plugin loadPlugin(PluginManager manager, Object obj, AtomicReference<PluginLoadResult> result) {
		URL url = (URL) obj;
		String type = getExtension(url);
		PluginClassloader classloader = new PluginClassloader(Classloader4ScriptManager, new URL[0]);
		ScriptEngineManager scriptmanager = new ScriptEngineManager(classloader);
		ScriptEngine engine = scriptmanager.getEngineByExtension(type);
		if (engine == null) {
			result.set(PluginLoadResult.FAILED);
			return null;
		}
		engine.put("classloader", classloader);
		try {
			engine.eval(new InputStreamReader(url.openStream(), Charset.forName("UTF-8")));
		} catch (ScriptException | IOException e) {
			e.printStackTrace();
			result.set(PluginLoadResult.FAILED);
			return null;
		}
		String name = engine.get("name") + "";
		String[] depends = new String[0];
		Map<String, String> info = new HashMap<>();
		try {
			depends = (String[]) engine.get("depends");
		} catch (Exception e) {
		}

        if (Arrays.stream(depends).filter(pluginName -> !manager.isPluginLoaded(pluginName)).count() != 0) {
            result.set(PluginLoadResult.WAIT);
			return setPluginDepends(setPluginName(new ScirptPlugin().setScriptEngine(engine), name), depends);
		}
		try {
			info = (Map<String, String>) engine.get("info");
		} catch (Exception e) {
		}
		BasePlugin plugin = null;
		try {
			plugin = (BasePlugin) engine.get("plugin");
		} catch (Exception e) {
		}
		if (plugin == null) {
			plugin = new ScirptPlugin();
		}
		if (plugin instanceof ScirptPlugin) {
			((ScirptPlugin) plugin).setScriptEngine(engine);
		}
		setPluginName(plugin, name);
		setPluginDepends(plugin, depends);
		setPluginInfo(plugin, info);
        classloader.addParents(Arrays.stream(depends).map(manager::getPlugin)
                .filter(p -> p.getClass().getClassLoader() instanceof URLClassLoader)
				.map(p -> (URLClassLoader) p.getClass().getClassLoader()).toArray(URLClassLoader[]::new));
		return plugin;
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

	@Override
	public void unloadNodify(Plugin plugin) {
		super.unloadNodify(plugin);
		if (plugin instanceof ScirptPlugin) {
			ScirptPlugin scirptPlugin = (ScirptPlugin) plugin;
			scirptPlugin.engine = null;

		}
	}

	/**
	 * key:文件拓展名(可用 {@link ScriptPluginLoader#getExtension(URL)}获取) value:是否有对应的
	 * {@link ScriptEngine}
	 */
	public static final Map<String, Boolean> knownExtensionMap = new HashMap<>();

	@Override
	public boolean accept(Object obj) {
		if (!(obj instanceof URL))
			return false;
		URL url = (URL) obj;
		String type = getExtension(url);
		if (knownExtensionMap.containsKey(type))
            return knownExtensionMap.get(type) == Boolean.TRUE;
        ScriptEngineManager manager = new ScriptEngineManager(Classloader4ScriptManager);
        boolean exist = manager.getEngineByExtension(type) != null;
        knownExtensionMap.put(type, exist);
		return exist;
	}

	public static String getExtension(URL url) {
		String urls = url + "";
		return urls.substring(urls.lastIndexOf(".") + 1);
	}

}
