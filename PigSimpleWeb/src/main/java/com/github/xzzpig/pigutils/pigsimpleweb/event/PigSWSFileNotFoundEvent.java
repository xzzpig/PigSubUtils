package com.github.xzzpig.pigutils.pigsimpleweb.event;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.response.Response;

import com.github.xzzpig.pigutils.pigsimpleweb.PigSimpleWebServer;

/*
 * 当GET的文件不存在时触发
 * 用于返回404的Response
 */
public class PigSWSFileNotFoundEvent extends PigSWSEvent {

	Response r;
	IHTTPSession session;

	public PigSWSFileNotFoundEvent(PigSimpleWebServer psws, IHTTPSession session) {
		super(psws);
		this.session = session;
	}

	Response getResponse() {
		return r;
	}

	public IHTTPSession getSession() {
		return this.session;
	}

	public void setResponse(Response response) {
		r = response;
	}
}
