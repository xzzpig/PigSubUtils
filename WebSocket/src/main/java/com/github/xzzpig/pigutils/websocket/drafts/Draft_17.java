package com.github.xzzpig.pigutils.websocket.drafts;

import com.github.xzzpig.pigutils.websocket.exceptions.InvalidHandshakeException;
import com.github.xzzpig.pigutils.websocket.handshake.ClientHandshake;
import com.github.xzzpig.pigutils.websocket.handshake.ClientHandshakeBuilder;

public class Draft_17 extends Draft_10 {
	@Override
	public HandshakeState acceptHandshakeAsServer(ClientHandshake handshakedata) throws InvalidHandshakeException {
		int v = readVersion(handshakedata);
		if (v == 13)
			return HandshakeState.MATCHED;
		return HandshakeState.NOT_MATCHED;
	}

	@Override
	public Draft copyInstance() {
		return new Draft_17();
	}

	@Override
	public ClientHandshakeBuilder postProcessHandshakeRequestAsClient(ClientHandshakeBuilder request) {
		super.postProcessHandshakeRequestAsClient(request);
		request.put("Sec-WebSocket-Version", "13");// overwriting the previous
		return request;
	}

}
