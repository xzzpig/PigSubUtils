package com.github.xzzpig.pigutils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class TScript {
	public static ScriptEngine getEngine(String ScriptEngineName) {
		ScriptEngineManager manager = new ScriptEngineManager();
		return manager.getEngineByName(ScriptEngineName);
	}

	public static ScriptEngine getJavaScriptEngine() {
		ScriptEngineManager manager = new ScriptEngineManager();
		return manager.getEngineByName("javascript");
	}

	public static ScriptEngine getJythonScriptEngine() {
		ScriptEngineManager manager = new ScriptEngineManager();
		return manager.getEngineByName("python");
	}

	public static ScriptEngine runJavaScriptOnWeb(String url) throws ScriptException {
		ScriptEngine engine = getJavaScriptEngine();
		engine.eval(TUrl.getHtml(url));
		return engine;
	}
}
