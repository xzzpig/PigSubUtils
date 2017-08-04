package com.github.xzzpig.pigutils.pigsimpleweb.event;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.response.Response;

import com.github.xzzpig.pigutils.pigsimpleweb.MIME;
import com.github.xzzpig.pigutils.pigsimpleweb.PigSimpleWebServer;

public class PigSWSSolveMIMEEvent extends PigSWSEvent {

	private MIME mime;
	private Response response;
	private IHTTPSession session;

	public PigSWSSolveMIMEEvent(PigSimpleWebServer psws, IHTTPSession session, MIME mime) {
		super(psws);
		this.session = session;
		this.mime = mime;
	}

	public MIME getMIME() {
		return mime;
	}

	Response getResponse() {
		return response;
	}

	public IHTTPSession getSession() {
		return session;
	}

	public void setResponse(Response r) {
		response = r;
	}

}
