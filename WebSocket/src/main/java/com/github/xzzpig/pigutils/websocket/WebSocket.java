package com.github.xzzpig.pigutils.websocket;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.NotYetConnectedException;

import com.github.xzzpig.pigutils.websocket.drafts.Draft;
import com.github.xzzpig.pigutils.websocket.framing.Framedata;
import com.github.xzzpig.pigutils.websocket.framing.Framedata.Opcode;

public interface WebSocket {

	/**
	 * The default port of WebSockets, as defined in the spec. If the nullary
	 * constructor is used, DEFAULT_PORT will be the port the WebSocketServer is
	 * binded to. Note that ports under 1024 usually require root permissions.
	 */
    int DEFAULT_PORT = 80;
    int DEFAULT_WSS_PORT = 443;

	/** Convenience function which behaves like close(CloseFrame.NORMAL) */
    void close();

    void close(int code);

	/**
	 * sends the closing handshake. may be send in response to an other
	 * handshake.
	 */
    void close(int code, String message);

	/**
	 * This will close the connection immediately without a proper close
	 * handshake. The code and the message therefore won't be transfered over
	 * the wire also they will be forwarded to onClose/onWebsocketClose.
	 **/
    void closeConnection(int code, String message);

    Draft getDraft();

	/**
	 * @return never returns null
	 */
    InetSocketAddress getLocalSocketAddress();

	/**
	 * Retrieve the WebSocket 'readyState'. This represents the state of the
	 * connection. It returns a numerical value, as per W3C WebSockets specs.
     *
     * @return Returns '0 = CONNECTING', '1 = OPEN', '2 = CLOSING' or '3 =
	 *         CLOSED'
	 */
    READYSTATE getReadyState();

	/**
	 * @return never returns null
	 */
    InetSocketAddress getRemoteSocketAddress();

	/**
	 * Returns the HTTP Request-URI as defined by
	 * http://tools.ietf.org/html/rfc2616#section-5.1.2<br>
	 * If the opening handshake has not yet happened it will return null.
	 **/
    String getResourceDescriptor();

    boolean hasBufferedData();

	/**
	 * Returns whether the close handshake has been completed and the socket is
	 * closed.
	 */
    boolean isClosed();

    boolean isClosing();

    boolean isConnecting();

	/**
	 * Returns true when no further frames may be submitted<br>
	 * This happens before the socket connection is closed.
	 */
    boolean isFlushAndClose();

    boolean isOpen();

    void send(byte[] bytes) throws IllegalArgumentException, NotYetConnectedException;

	/**
	 * Send Binary data (plain bytes) to the other end.
     *
     * @throws IllegalArgumentException
	 * @throws NotYetConnectedException
	 */
    void send(ByteBuffer bytes) throws IllegalArgumentException, NotYetConnectedException;

	/**
	 * Send Text data to the other end.
     *
     * @throws IllegalArgumentException
	 * @throws NotYetConnectedException
	 */
    void send(String text) throws NotYetConnectedException;

	/**
	 * Allows to send continuous/fragmented frames conveniently. <br>
	 * For more into on this frame type see
	 * http://tools.ietf.org/html/rfc6455#section-5.4<br>
     *
     * If the first frame you send is also the last then it is not a fragmented
	 * frame and will received via onMessage instead of onFragmented even though
	 * it was send by this method.
     *
     * @param op
	 *            This is only important for the first frame in the sequence.
	 *            Opcode.TEXT, Opcode.BINARY are allowed.
	 * @param buffer
	 *            The buffer which contains the payload. It may have no bytes
	 *            remaining.
	 * @param fin
	 *            true means the current frame is the last in the sequence.
	 **/
    void sendFragmentedFrame(Opcode op, ByteBuffer buffer, boolean fin);

    void sendFrame(Framedata framedata);

    enum READYSTATE {
        NOT_YET_CONNECTED, CONNECTING, OPEN, CLOSING, CLOSED
    }

    enum Role {
        CLIENT, SERVER
    }
}