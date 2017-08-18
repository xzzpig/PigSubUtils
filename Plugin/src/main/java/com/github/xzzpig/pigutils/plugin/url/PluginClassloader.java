package com.github.xzzpig.pigutils.plugin.url;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.stream.Stream;

public class PluginClassloader extends URLClassLoader {

	public PluginClassloader(ClassLoader parent, URL... urls) {
		super(urls, parent);
	}

	public PluginClassloader addParents(URLClassLoader... parents) {
		Stream.of(parents).forEach(parent -> this.addURLs(parent.getURLs()));
		return this;
	}

	private void addURLs(URL... urls) {
		Stream.of(urls).forEach(this::addURL);
	}
}
