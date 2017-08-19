package com.github.xzzpig.pigutils.plugin;

import java.util.Map;

public interface Plugin {
	String[] getDepends();

	Map<String, String> getInfos();

	String getName();

	void onDisable();

	void onEnable();

	/**
	 * @return 是否需要完全重载
	 */
	boolean onReload();

	PluginLoader getPluginLoader();

	PluginManager getPluginManager();

	boolean isDependOn(String pluginName);

	void setRawObject(Object obj);

	Object getRawObject();
}
