package com.github.xzzpig.pigutils.websocket.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

import com.github.xzzpig.pigutils.websocket.AbstractWrappedByteChannel;

public abstract class AbstractClientProxyChannel extends AbstractWrappedByteChannel {
	protected final ByteBuffer proxyHandshake;

	/**
	 * @param towrap
	 *            The channel to the proxy server
	 **/
	public AbstractClientProxyChannel(ByteChannel towrap) {
		super(towrap);
		try {
			proxyHandshake = ByteBuffer.wrap(buildHandShake().getBytes("ASCII"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public abstract String buildHandShake();

	@Override
	public int write(ByteBuffer src) throws IOException {
		if (!proxyHandshake.hasRemaining()) {
			return super.write(src);
		} else {
			return super.write(proxyHandshake);
		}
	}

}
