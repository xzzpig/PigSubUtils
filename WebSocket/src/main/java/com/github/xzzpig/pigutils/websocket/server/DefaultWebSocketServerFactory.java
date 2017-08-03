package com.github.xzzpig.pigutils.websocket.server;

import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;

import com.github.xzzpig.pigutils.websocket.WebSocketAdapter;
import com.github.xzzpig.pigutils.websocket.WebSocketImpl;
import com.github.xzzpig.pigutils.websocket.drafts.Draft;
import com.github.xzzpig.pigutils.websocket.server.WebSocketServer.WebSocketServerFactory;

public class DefaultWebSocketServerFactory implements WebSocketServerFactory {
	@Override
	public WebSocketImpl createWebSocket(WebSocketAdapter a, Draft d, Socket s) {
		return new WebSocketImpl(a, d);
	}

	@Override
	public WebSocketImpl createWebSocket(WebSocketAdapter a, List<Draft> d, Socket s) {
		return new WebSocketImpl(a, d);
	}

	@Override
	public SocketChannel wrapChannel(SocketChannel channel, SelectionKey key) {
		return channel;
	}
}