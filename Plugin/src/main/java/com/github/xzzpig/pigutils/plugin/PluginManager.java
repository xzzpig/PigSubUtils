package com.github.xzzpig.pigutils.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.github.xzzpig.pigutils.TJython;
import com.github.xzzpig.pigutils.TScript;
import com.github.xzzpig.pigutils.event.TransformEvent.Transformer;

public class PluginManager {

	public static final Map<String, Transformer<File, Plugin>> MIMEs = new HashMap<>();

	static {
		MIMEs.put("jar", PluginManager::loadJarPlugin);
	}

	static List<PluginInfo> handleplugins = new ArrayList<>();
	static Map<String, Plugin> plugins = new HashMap<>();

	public static void loadHandlePlugin() {
		List<PluginInfo> list = new ArrayList<>(handleplugins);
		list.forEach((PluginInfo info) -> {
			boolean load = (loadPlugin(info) != null);
			if (load) {
				handleplugins.remove(info);
			}
		});
	}

	private static PluginInfo loadInfoFromDir(File dir) {
		File info_f = new File(dir, "info.json");
		if (!info_f.exists())
			return null;
		PluginInfo info;
		try {
			info = new PluginInfo(new FileInputStream(info_f));
		} catch (FileNotFoundException e) {
			return null;
		}
		return info;
	}

	@SuppressWarnings({ "unchecked", "resource" })
	public static Plugin loadJarPlugin(File jar) {
		loadHandlePlugin();
		try {
			JarFile jarFile;
			jarFile = new JarFile(jar);
			ZipEntry jarEntry = jarFile.getEntry("info.json");
			PluginInfo info;
			InputStream inputStream = jarFile.getInputStream(jarEntry);
			info = new PluginInfo(inputStream);
			jarFile.close();
			info.loader = new PluginLoader() {
				@Override
				public Plugin loadPlugin() {
					try {
						URLClassLoader urlClassLoader = new URLClassLoader(new URL[] { jar.toURI().toURL() });
						String main = info.getMain();
						Class<? extends Plugin> c = (Class<? extends Plugin>) urlClassLoader.loadClass(main);
						Plugin plugin = c.newInstance();
						plugin.pluginInfo = info;
						return plugin;
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}
				}
			};
			return loadPlugin(info);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Plugin loadJythonDirPlugin(File dir) {
		if (!dir.isDirectory())
			return null;
		PluginInfo info = loadInfoFromDir(dir);
		if (info == null)
			return null;
		String main = info.getMain();
		if (main == null)
			return null;
		if (!main.endsWith(".py"))
			main = main + ".py";
		String main_ = main;
		info.loader = new PluginLoader() {
			@Override
			public Plugin loadPlugin() {
				TJython.build();
				Plugin p = new Plugin() {
					ScriptEngine engine = TScript.getJythonScriptEngine();

					@Override
					public void onDisable() {
						String main = info.jsonObject.optString("disable");
						if (main == null)
							return;
						if (!main.endsWith(".py"))
							main = main + ".py";
						File main_f = new File(dir, main.replaceAll(".", "/"));
						FileReader fr = null;
						try {
							fr = new FileReader(main_f);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
						try {
							engine.eval(fr);
						} catch (ScriptException e) {
							e.printStackTrace();
						}
						try {
							fr.close();
						} catch (IOException e) {
						}
					}

					@Override
					public void onEnable() {
						File main_f = new File(dir, main_.replaceAll(".", "/"));
						FileReader fr = null;
						try {
							fr = new FileReader(main_f);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
						engine.put("engine", engine);
						engine.put("dir", dir);
						engine.put("info", info);
						try {
							engine.eval(fr);
						} catch (ScriptException e) {
							e.printStackTrace();
						}
						try {
							fr.close();
						} catch (IOException e) {
						}
					}
				};
				return p;
			}
		};

		return loadPlugin(info);
	}

	public static Plugin loadPlugin(File file) {
		if (file.isDirectory()) {
			PluginInfo info = loadInfoFromDir(file);
			if (info == null)
				return null;
			String mime = info.jsonObject.optString("mime", null);
			if (mime == null || !MIMEs.containsKey(mime) || MIMEs.get(mime) == null)
				return null;
			return MIMEs.get(mime).transform(file);
		}
		String[] names = file.getName().split(".");
		if (names.length == 1)
			return null;
		String mime = names[names.length - 1];
		if (!MIMEs.containsKey(mime) || MIMEs.get(mime) == null)
			return null;
		return MIMEs.get(mime).transform(file);
	}

	private static Plugin loadPlugin(PluginInfo info) {
		info.getDependence().forEach((String name) -> {
			if (plugins.containsKey(name)) {
				handleplugins.add(info);
			}
		});
		if (handleplugins.contains(info)) {
			return null;
		}
		Plugin plugin = info.loader.loadPlugin();
		if (plugin == null) {
			return null;
		}
		plugin.onEnable();
		plugins.put(plugin.pluginInfo.getName(), plugin);
		return plugin;
	}

	public static void unloadPlugin(String name) {
		if (!plugins.containsKey(name)) {
			return;
		}
		Plugin plugin = plugins.get(name);
		plugin.onDisable();
		plugins.remove(name);
	}

	private PluginManager() {
	}
}