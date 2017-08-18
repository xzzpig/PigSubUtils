package com.github.xzzpig.pigutils.plugin.java;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import com.github.xzzpig.pigutils.json.JSONObject;
import com.github.xzzpig.pigutils.json.JSONTokener;
import com.github.xzzpig.pigutils.plugin.Plugin;
import com.github.xzzpig.pigutils.plugin.PluginManager;
import com.github.xzzpig.pigutils.plugin.url.URLPluginInfo;
import com.github.xzzpig.pigutils.plugin.url.URLPluginLoader;

public class JavaPluginLoader extends URLPluginLoader {
	public JavaPluginLoader() {
	}

	@Override
	public int order() {
		return PluginLoaderOrder.HIGH;
	}

	@Override
	public Plugin loadPlugin(PluginManager manager, Object obj, AtomicReference<PluginLoadResult> result) {
		File file = (File) obj;
		Object[] objs = new Object[2];
		try {
			objs[0] = file.toURI().toURL();
		} catch (MalformedURLException e) {
			result.set(PluginLoadResult.FAILED);
			return null;
		}
		JarFile jarFile = null;
		try {
			jarFile = new JarFile(file);
			ZipEntry jarEntry = jarFile.getEntry("plugin.json");
			JSONObject info = new JSONObject(new JSONTokener(jarFile.getInputStream(jarEntry)));
			URLPluginInfo pluginInfo = new URLPluginInfo(info.optString("name"), info.optString("main"),
					info.optJSONArray("depends").toList().toArray(new String[0]), json2Map(info));
			objs[1] = pluginInfo;
		} catch (IOException e) {
			result.set(PluginLoadResult.FAILED);
			return null;
		} finally {
			if (jarFile != null)
				try {
					jarFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return super.loadPlugin(manager, objs, result);
	}

	private Map<String, String> json2Map(JSONObject json) {
		Map<String, String> map = new HashMap<>();
		json.keySet().stream().forEach(key -> map.put(key, json.optString(key)));
		return map;
	}

	@Override
	public boolean accept(Object obj) {
		if (!(obj instanceof File))
			return false;
		File file = (File) obj;
		if (!file.isFile())
			return false;
		if (!file.getAbsolutePath().endsWith(".jar"))
			return false;
		return true;
	}

}
