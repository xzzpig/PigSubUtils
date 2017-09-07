package com.github.xzzpig.pigutils.plugin.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.xzzpig.pigutils.plugin.Plugin;
import com.github.xzzpig.pigutils.plugin.PluginLoader;
import com.github.xzzpig.pigutils.plugin.PluginManager;

public abstract class BasePluginLoader implements PluginLoader {

	public BasePluginLoader() {
	}

	@Override
	public boolean needSuccessNodify() {
		return true;
	}

	@Override
	public void successNodify(PluginManager manager, Plugin plugin) {
		if (plugin instanceof BasePlugin) {
			BasePlugin basePlugin = (BasePlugin) plugin;
			basePlugin.pluginLoader = this;
			basePlugin.pluginManager = manager;
		}
		plugin.onEnable();
	}

	@Override
	public boolean needUnloadNodify() {
		return true;
	}

	@Override
	public boolean needFailedNodify() {
		return false;
	}

	@Override
	public boolean needWaitNodify() {
		return true;
	}

	protected Map<String, List<Runnable>> waitMap = new HashMap<>();

	@Override
	public void waitNodify(PluginManager manager, Object obj, Plugin plugin) {
		if (plugin == null)
			return;
		Runnable r = () -> manager.loadPlugin(obj);
		for (String depend : plugin.getDepends()) {
			if (manager.isPluginLoaded(depend))
				continue;
			List<Runnable> rs;
			if (waitMap.containsKey(depend))
				rs = waitMap.get(depend);
			else {
				rs = new ArrayList<>();
				waitMap.put(depend, rs);
			}
			rs.add(r);
			break;
		}
	}

	@Override
	public boolean needOtherSuccessNodify() {
		return true;
	}

	@Override
	public void othersuccessNodify(Plugin plugin) {
		if (waitMap.containsKey(plugin.getName())) {
            waitMap.get(plugin.getName()).forEach(Runnable::run);
            waitMap.remove(plugin.getName());
		}
	}

	@Override
	public boolean needOtherUnloadNodify() {
		return false;
	}

	protected static BasePlugin setPluginName(BasePlugin plugin, String name) {
		plugin.name = name;
		return plugin;
	}

	protected static BasePlugin setPluginDepends(BasePlugin plugin, String[] depends) {
		plugin.depends = depends == null ? new String[0] : depends;
		return plugin;
	}

	protected static BasePlugin setPluginInfo(BasePlugin plugin, Map<String, String> info) {
		plugin.info = info == null ? new HashMap<>() : info;
		return plugin;
	}

	protected static Object getRawObject(BasePlugin plugin) {
		return plugin.rawObject;
	}

	@Override
	public void reloadPlugin(Plugin plugin) {
		List<Plugin> subPlugins = getSubPlugins(plugin);
		Object rawObject = plugin.getRawObject();
		PluginManager manager = plugin.getPluginManager();
		manager.unloadPlugin(plugin);
		subPlugins.forEach(p -> p.getPluginManager().deepReloadPlugin(p));
		manager.loadPlugin(rawObject);
	}

	protected List<Plugin> getSubPlugins(Plugin plugin) {
		List<Plugin> ps = new ArrayList<>();
		PluginManager manager = plugin.getPluginManager();
		for (String p : manager.listPlugins()) {
			Plugin sub = manager.getPlugin(p);
			if (sub.isDependOn(plugin.getName()))
				ps.add(sub);
		}
		return ps;
	}

	@Override
	public void unloadNodify(Plugin plugin) {
		plugin.onDisable();
		for (Plugin sub : getSubPlugins(plugin)) {
			sub.getPluginManager().unloadPlugin(sub);
		}
	}

	@Override
	public void failedNodify(Object obj) {
	}

	@Override
	public void otherunloadNodify(Plugin plugin) {
	}
}
