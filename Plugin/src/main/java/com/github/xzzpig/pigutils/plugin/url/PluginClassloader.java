package com.github.xzzpig.pigutils.plugin.url;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

public class PluginClassloader extends URLClassLoader {

	public PluginClassloader(ClassLoader parent, URL... urls) {
		super(urls, parent);
	}

	public PluginClassloader addParents(URLClassLoader... parents) {
		Arrays.asList(parents).forEach(parent -> this.addURLs(parent.getURLs()));
		return this;
	}

	private void addURLs(URL... urls) {
		Arrays.asList(urls).forEach(this::addURL);
	}
}
