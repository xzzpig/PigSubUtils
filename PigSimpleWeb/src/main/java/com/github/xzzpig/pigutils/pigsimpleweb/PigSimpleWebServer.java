package com.github.xzzpig.pigutils.pigsimpleweb;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.response.Response;

import com.github.xzzpig.pigutils.event.Event;
import com.github.xzzpig.pigutils.pigsimpleweb.event.PigSWSGetMIMEEvent;
import com.github.xzzpig.pigutils.pigsimpleweb.event.PigSWSGetRootEvent;
import com.github.xzzpig.pigutils.pigsimpleweb.event.PigSWSListener;
import com.github.xzzpig.pigutils.pigsimpleweb.event.PigSWSServeEvent;

/*
 * 简单的网页服务器
 */
public class PigSimpleWebServer extends NanoHTTPD {

	private static int instancenum = 0;

	private PigSWSListener listener;

	Map<String, MIME> mimemap;

	private String root;

	public PigSimpleWebServer(int port) {
		super(port);
	}

	public MIME getMIMEby(String filename) {
		if (mimemap == null) {
			mimemap = new HashMap<>();
		}
		String type = "";
		String[] subs = filename.split("/");
		filename = subs[subs.length - 1];
		if (filename.contains(".")) {
			type = filename.substring(filename.lastIndexOf(".") + 1);
		}
		if (mimemap.containsKey(type)) {
			return mimemap.get(type);
		}
		PigSWSGetMIMEEvent pigSWSGetMIMEEvent = new PigSWSGetMIMEEvent(this, type);
		Event.callEvent(pigSWSGetMIMEEvent);
		MIME mime = pigSWSGetMIMEEvent.getMIME() == null ? MIME.application_octet_stream : pigSWSGetMIMEEvent.getMIME();
		mimemap.put(type, mime);
		return mime;
	}

	public String getRootDir() {
		if (root == null) {
			PigSWSGetRootEvent e = new PigSWSGetRootEvent(this);
			Event.callEvent(e);
			root = e.getRoot();
		}
		return root;
	}

	@Override
	public Response serve(IHTTPSession session) {
		PigSWSServeEvent event = new PigSWSServeEvent(this, session);
		Event.callEvent(event);
		if (event.getResponse() != null) {
			return event.getResponse();
		}
		return super.serve(session);
	}

	@Override
	public void start(int timeout, boolean daemon) throws IOException {
		super.start(timeout, daemon);
		if (instancenum == 0) {
			listener = new PigSWSListener();
			Event.registListener(listener);
		}
		instancenum++;
	}

	@Override
	public void stop() {
		super.stop();
		instancenum--;
		if (instancenum == 0) {
			Event.unregListener(listener);
		}
	}
}
