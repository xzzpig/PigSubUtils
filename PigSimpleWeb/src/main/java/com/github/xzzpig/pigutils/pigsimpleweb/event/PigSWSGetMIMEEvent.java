package com.github.xzzpig.pigutils.pigsimpleweb.event;

import com.github.xzzpig.pigutils.pigsimpleweb.MIME;
import com.github.xzzpig.pigutils.pigsimpleweb.PigSimpleWebServer;

/*
 * 获取某指定类型的MIME
 */
public class PigSWSGetMIMEEvent extends PigSWSEvent {

	MIME m;

	String t;

	public PigSWSGetMIMEEvent(PigSimpleWebServer psws, String type) {
		super(psws);
		t = type;
	}

	public MIME getMIME() {
		return m;
	}

	public String getType() {
		return t;
	}

	public void setMIME(MIME mime) {
		m = mime;
	}
}
