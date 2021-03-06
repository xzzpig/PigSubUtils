package com.github.xzzpig.pigutils.websocket.framing;

import java.nio.ByteBuffer;

import com.github.xzzpig.pigutils.websocket.exceptions.InvalidFrameException;

public interface Framedata {
	public enum Opcode {
		CONTINUOUS, TEXT, BINARY, PING, PONG, CLOSING
		// more to come
	}

	public abstract void append(Framedata nextframe) throws InvalidFrameException;

	public Opcode getOpcode();

	public ByteBuffer getPayloadData();// TODO the separation of the application
										// data and the extension data is yet to
										// be done

	public boolean getTransfereMasked();

	public boolean isFin();
}
