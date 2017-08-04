package com.github.xzzpig.pigutils.pigsimpleweb.event;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.response.Response;

import com.github.xzzpig.pigutils.pigsimpleweb.PigSimpleWebServer;

public class PigSWSServeEvent extends PigSWSEvent {

	private Response response;
	private IHTTPSession session;

	public PigSWSServeEvent(PigSimpleWebServer psws, IHTTPSession session) {
		super(psws);
		this.session = session;
	}

	public Response getResponse() {
		return response;
	}

	public IHTTPSession getSession() {
		return session;
	}

	public void setResponse(Response response) {
		this.response = response;
	}
}
