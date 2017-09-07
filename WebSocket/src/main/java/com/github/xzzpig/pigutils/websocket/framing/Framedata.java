package com.github.xzzpig.pigutils.websocket.framing;

import java.nio.ByteBuffer;

import com.github.xzzpig.pigutils.websocket.exceptions.InvalidFrameException;

public interface Framedata {
    void append(Framedata nextframe) throws InvalidFrameException;

    Opcode getOpcode();

    ByteBuffer getPayloadData();// TODO the separation of the application

    boolean getTransfereMasked();
    // data and the extension data is yet to
										// be done

    boolean isFin();

    enum Opcode {
        CONTINUOUS, TEXT, BINARY, PING, PONG, CLOSING
        // more to come
    }
}
