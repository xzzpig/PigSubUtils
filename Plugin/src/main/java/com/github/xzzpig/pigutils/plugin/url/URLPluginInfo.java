package com.github.xzzpig.pigutils.plugin.url;

import java.util.Map;

public class URLPluginInfo {

	String name;
	String[] depends;
	Map<String, String> infos;
	String mainClass;

	public URLPluginInfo(String name, String mainClass, String[] depends, Map<String, String> infos) {
		this.name = name;
		this.depends = depends;
		this.infos = infos;
		this.mainClass = mainClass;
	}

	public String getName() {
		return name;
	}

	public URLPluginInfo setName(String name) {
		this.name = name;
		return this;
	}

	public String[] getDepends() {
		return depends;
	}

	public URLPluginInfo setDepends(String[] depends) {
		this.depends = depends;
		return this;
	}

	public Map<String, String> getInfos() {
		return infos;
	}

	public URLPluginInfo setInfos(Map<String, String> infos) {
		this.infos = infos;
		return this;
	}

	public String getMainClass() {
		return mainClass;
	}

	public URLPluginInfo setMainClass(String mainClass) {
		this.mainClass = mainClass;
		return this;
	}
}
