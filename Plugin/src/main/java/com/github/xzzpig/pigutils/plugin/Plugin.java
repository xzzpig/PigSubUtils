package com.github.xzzpig.pigutils.plugin;

import java.util.Map;
import java.util.stream.Stream;

public interface Plugin {
	String[] getDepends();

	Map<String, String> getInfos();

	String getName();

	void onDisable();

	void onEnable();

	default void onReload() {
		onDisable();
		onEnable();
	}

	PluginLoader getPluginLoader();

	PluginManager getPluginManager();

	default boolean isDependOn(String pluginName) {
		if (getDepends() == null)
			return false;
		return Stream.of(getDepends()).anyMatch(depend -> depend.equals(pluginName));
	}
}
