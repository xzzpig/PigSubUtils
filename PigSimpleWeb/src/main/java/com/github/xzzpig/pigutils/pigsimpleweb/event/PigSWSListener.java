package com.github.xzzpig.pigutils.pigsimpleweb.event;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.request.Method;
import org.nanohttpd.protocols.http.response.Response;
import org.nanohttpd.protocols.http.response.Status;

import com.github.xzzpig.pigutils.TScript;
import com.github.xzzpig.pigutils.event.Event;
import com.github.xzzpig.pigutils.event.EventHandler;
import com.github.xzzpig.pigutils.event.EventRunLevel;
import com.github.xzzpig.pigutils.event.Listener;
import com.github.xzzpig.pigutils.pigsimpleweb.MIME;
import com.github.xzzpig.pigutils.pigsimpleweb.PigSWPage;

public class PigSWSListener implements Listener {
	public File findFile(File dir, String filename) {
		if (dir == null || !dir.exists() || !dir.isDirectory()) {
			return null;
		}
		for (File file : dir.listFiles()) {
			if (file.isFile() && file.getName().equalsIgnoreCase(filename)) {
				return file;
			} else if (file.isDirectory()) {
				File file2 = findFile(file, filename);
				if (file2 != null) {
					return file2;
				}
			}
		}
		return null;
	}

	@EventHandler(mainLevel = EventRunLevel.Lowest)
	public void onPigSWSGetMIMEEVent(PigSWSGetMIMEEvent event) {
		switch (event.getType()) {
		case "html":
		case "htm":
			event.setMIME(MIME.text_html);
			break;
		case "pswp":
			event.setMIME(MIME.pigswpage);
			break;
		case "pjsp":
			event.setMIME(MIME.pigjspage);
			break;
		}
	}

	@EventHandler(mainLevel = EventRunLevel.Highest)
	public void onPigSWSServerEvent_get_default(PigSWSServeEvent event) {
		IHTTPSession session = event.getSession();
		if (session.getMethod() != Method.GET) {
			return;
		}
		String uri = session.getUri();
		if (uri.endsWith("/")) {
			PigSWSUriIsDicEvent pigSWSUriIsDicEvent = new PigSWSUriIsDicEvent(event.getPigSimpleWebServer(), uri);
			Event.callEvent(pigSWSUriIsDicEvent);
			uri += pigSWSUriIsDicEvent.getFileName();
			session.setUri(uri);
		}
		PigSWSSolveMIMEEvent pigSWSSolveMIMEEvent = new PigSWSSolveMIMEEvent(event.getPigSimpleWebServer(), session,
				event.getPigSimpleWebServer().getMIMEby(uri));
		Event.callEvent(pigSWSSolveMIMEEvent);
		event.setResponse(pigSWSSolveMIMEEvent.getResponse());
	}

	@EventHandler(mainLevel = EventRunLevel.Highest)
	public void onPigSWSSolveMIMEEvent_pjsp_default(PigSWSSolveMIMEEvent event) {
		File jsFile = new File(event.getPigSimpleWebServer().getRootDir() + event.getSession().getUri());
		if (!jsFile.exists()) {
			PigSWSFileNotFoundEvent pigSWSFileNotFoundEvent = new PigSWSFileNotFoundEvent(event.getPigSimpleWebServer(),
					event.getSession());
			event.setResponse(pigSWSFileNotFoundEvent.getResponse());
			return;
		}
		ScriptEngine engine = TScript.getJavaScriptEngine();
		Response response = null;
		engine.put("event", event);
		engine.put("webserver", event.getPigSimpleWebServer());
		engine.put("session", event.getSession());
		try {
			FileReader reader = new FileReader(jsFile);
			engine.eval(reader);
			try {
				response = Response.newFixedLengthResponse((String) engine.get("response"));
			} catch (Exception e) {
				response = Response.newFixedLengthResponse("Script No Response");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ScriptException e) {
			e.printStackTrace();
			response = Response.newFixedLengthResponse("Script Run Error\n" + e);
		}
		event.setResponse(response);
	}

	@EventHandler(mainLevel = EventRunLevel.Highest)
	public void onPigSWSSolveMIMEEvent_pswp_default(PigSWSSolveMIMEEvent event) {
		if (event.getMIME().getSolveTyle().equalsIgnoreCase("pswp")) {
			String url = event.getSession().getUri();
			int endIndex = url.lastIndexOf("/");
			String path = url.substring(0, endIndex + 1);
			String filename = url.substring(endIndex + 1, url.lastIndexOf("."));
			File dir = new File(event.getPigSimpleWebServer().getRootDir() + path);
			File classfile = findFile(dir, filename + ".class");
			if (classfile == null) {
				PigSWSFileNotFoundEvent pigSWSFileNotFoundEvent = new PigSWSFileNotFoundEvent(
						event.getPigSimpleWebServer(), event.getSession());
				event.setResponse(pigSWSFileNotFoundEvent.getResponse());
				return;
			}
			String className = classfile.getAbsolutePath().replaceAll(dir.getAbsolutePath() + "/", "").replace('/',
					'.');
			className = className.substring(0, className.lastIndexOf('.'));
			URLClassLoader urlClassLoader = null;
			try {
				urlClassLoader = new URLClassLoader(new URL[] { dir.toURI().toURL() },
						this.getClass().getClassLoader());
				@SuppressWarnings("unchecked")
				Class<? extends PigSWPage> class1 = (Class<? extends PigSWPage>) urlClassLoader.loadClass(className);
				PigSWPage page = class1.newInstance();
				event.setResponse(page.getResponse(event.getPigSimpleWebServer(), event.getSession()));
				return;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} finally {
				try {
					urlClassLoader.close();
				} catch (Exception e2) {
				}
			}
		}
	}

	@EventHandler(mainLevel = EventRunLevel.Highest)
	public void onPigSWSSolveMIMEEvent_text_default(PigSWSSolveMIMEEvent event) {
		if (event.getMIME().getSolveTyle().equalsIgnoreCase("text")
				|| event.getMIME().getSolveTyle().equalsIgnoreCase("file")) {
			File file = new File(event.getPigSimpleWebServer().getRootDir() + event.getSession().getUri());
			if (!file.exists()) {
				PigSWSFileNotFoundEvent pigSWSFileNotFoundEvent = new PigSWSFileNotFoundEvent(
						event.getPigSimpleWebServer(), event.getSession());
				event.setResponse(pigSWSFileNotFoundEvent.getResponse());
				return;
			}
			try {
				Response response = Response.newFixedLengthResponse(Status.OK, event.getMIME().getName(),
						file.toURI().toURL().openStream(), file.length());
				event.setResponse(response);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@EventHandler(mainLevel = EventRunLevel.Highest)
	public void onPigSWSUriIsDicEvent(PigSWSUriIsDicEvent event) {
		event.setFileName("index.html");
	}
}
