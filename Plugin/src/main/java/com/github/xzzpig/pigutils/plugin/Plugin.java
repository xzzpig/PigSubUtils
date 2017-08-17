package com.github.xzzpig.pigutils.plugin;

import java.util.Map;

public interface Plugin {
	String[] getDepends();
	Map<String,String> getInfos();
	String getName();
	
	void onDisable();
	void onEnable();
	default void onReload(){
		onDisable();
		onEnable();
	}
	
	PluginLoader getPluginLoader();
	
	PluginManager getPluginManager();
}
