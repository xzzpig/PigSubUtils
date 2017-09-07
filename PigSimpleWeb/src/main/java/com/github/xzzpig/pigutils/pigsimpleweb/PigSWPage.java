package com.github.xzzpig.pigutils.pigsimpleweb;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.response.Response;

public interface PigSWPage {
    Response getResponse(PigSimpleWebServer pigSimpleWebServer, IHTTPSession session);
}
