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
			waitMap.get(plugin.getName()).forEach(r -> r.run());
			waitMap.remove(plugin.getName());
		}
	}
	
	@Override
	public boolean needOtherUnloadNodify() {
		return false;
	}
}
