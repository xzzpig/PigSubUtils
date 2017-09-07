package com.github.xzzpig.pigutils.websocket.handshake;

import java.util.Iterator;

public interface Handshakedata {
    byte[] getContent();

    String getFieldValue(String name);

    boolean hasFieldValue(String name);

    Iterator<String> iterateHttpFields();
}
