package com.github.xzzpig.pigutils.websocket.framing;

import java.nio.ByteBuffer;

import com.github.xzzpig.pigutils.websocket.exceptions.InvalidDataException;

public interface FrameBuilder extends Framedata {

    void setFin(boolean fin);

    void setOptcode(Opcode optcode);

    void setPayload(ByteBuffer payload) throws InvalidDataException;

    void setTransferemasked(boolean transferemasked);

}