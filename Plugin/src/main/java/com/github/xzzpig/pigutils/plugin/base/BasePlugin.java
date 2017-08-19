package com.github.xzzpig.pigutils.plugin.base;

import java.util.HashMap;
import java.util.Map;

import com.github.xzzpig.pigutils.plugin.Plugin;
import com.github.xzzpig.pigutils.plugin.PluginLoader;
import com.github.xzzpig.pigutils.plugin.PluginManager;

public abstract class BasePlugin implements Plugin {

	protected String name;
	protected Map<String, String> info = new HashMap<>();
	protected String[] depends = {};
	protected PluginLoader pluginLoader;
	protected PluginManager pluginManager;
	protected Object rawObject;

	public BasePlugin() {
	}

	@Override
	public String[] getDepends() {
		return depends;
	}

	@Override
	public Map<String, String> getInfos() {
		return info;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public PluginLoader getPluginLoader() {
		return pluginLoader;
	}

	@Override
	public PluginManager getPluginManager() {
		return pluginManager;
	}

	@Override
	public void setRawObject(Object obj) {
		this.rawObject = obj;
	}

	@Override
	public Object getRawObject() {
		return rawObject;
	}

	@Override
	public boolean onReload() {
		onDisable();
		onEnable();
		return false;
	}

	@Override
	public boolean isDependOn(String pluginName) {
		if (getDepends() == null)
			return false;
		for (String depend : getDepends()) {
			if (depend.equals(pluginName))
				return true;
		}
		return false;
	}
}
