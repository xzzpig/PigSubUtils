package com.github.xzzpig.pigutils.plugin.script;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.github.xzzpig.pigutils.plugin.url.URLPlugin;

public class ScirptPlugin extends URLPlugin {

	ScriptEngine engine;

	ScirptPlugin() {
	}

	ScirptPlugin setScriptEngine(ScriptEngine engine) {
		this.engine = engine;
		this.engine.put("plugin", this);
		return this;
	}

	@Override
	public void onDisable() {
		try {
			engine.eval("onDisable()");
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onEnable() {
		try {
			engine.eval("onEnable()");
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onReload() {
		return true;
	}
}
