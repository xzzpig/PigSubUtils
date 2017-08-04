package com.github.xzzpig.pigutils.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.github.xzzpig.pigutils.json.JSONObject;
import com.github.xzzpig.pigutils.json.JSONTokener;

/**
 * @author xzzpig
 *
 */
/**
 * @author w2xzz
 *
 */
/**
 * @author w2xzz
 *
 */
public class PluginInfo {
	public final JSONObject jsonObject;

	public PluginLoader loader;

	PluginInfo(InputStream in) {
		jsonObject = new JSONObject(new JSONTokener(in));
		try {
			in.close();
		} catch (IOException e) {
		}
	}

	/**
	 * @return 插件作者
	 */
	public String getAuther() {
		return jsonObject.optString("version");
	}

	public List<String> getDependence() {
		List<String> list = new ArrayList<>();
		try {
			jsonObject.optJSONArray("dependence").forEach((Object o) -> {
				list.add(o.toString());
			});
		} catch (Exception e) {
		}
		return list;
	}

	/**
	 * @return 插件的主类
	 */
	public String getMain() {
		return jsonObject.optString("main");
	}

	/**
	 * @return 插件名字
	 */
	public String getName() {
		return jsonObject.optString("name");
	}

	/**
	 * @return 插件版本
	 */
	public String getVersion() {
		return jsonObject.optString("version");
	}
}
