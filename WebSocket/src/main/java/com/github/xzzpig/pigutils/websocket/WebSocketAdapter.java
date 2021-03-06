package com.github.xzzpig.pigutils.websocket;

import java.net.InetSocketAddress;

import com.github.xzzpig.pigutils.websocket.drafts.Draft;
import com.github.xzzpig.pigutils.websocket.exceptions.InvalidDataException;
import com.github.xzzpig.pigutils.websocket.exceptions.InvalidHandshakeException;
import com.github.xzzpig.pigutils.websocket.framing.Framedata;
import com.github.xzzpig.pigutils.websocket.framing.FramedataImpl1;
import com.github.xzzpig.pigutils.websocket.framing.Framedata.Opcode;
import com.github.xzzpig.pigutils.websocket.handshake.ClientHandshake;
import com.github.xzzpig.pigutils.websocket.handshake.HandshakeImpl1Server;
import com.github.xzzpig.pigutils.websocket.handshake.ServerHandshake;
import com.github.xzzpig.pigutils.websocket.handshake.ServerHandshakeBuilder;

/**
 * This class default implements all methods of the WebSocketListener that can
 * be overridden optionally when advances functionalities is needed.<br>
 **/
public abstract class WebSocketAdapter implements WebSocketListener {

	/**
	 * Gets the XML string that should be returned if a client requests a Flash
	 * security policy.
	 * 
	 * The default implementation allows access from all remote domains, but
	 * only on the port that this WebSocketServer is listening on.
	 * 
	 * This is specifically implemented for gitime's WebSocket client for Flash:
	 * http://github.com/gimite/web-socket-js
	 * 
	 * @return An XML String that comforts to Flash's security policy. You MUST
	 *         not include the null char at the end, it is appended
	 *         automatically.
	 * @throws InvalidDataException
	 *             thrown when some data that is required to generate the
	 *             flash-policy like the websocket local port could not be
	 *             obtained e.g because the websocket is not connected.
	 */
	@Override
	public String getFlashPolicy(WebSocket conn) throws InvalidDataException {
		InetSocketAddress adr = conn.getLocalSocketAddress();
		if (null == adr) {
			throw new InvalidHandshakeException("socket not bound");
		}

		StringBuffer sb = new StringBuffer(90);
		sb.append("<cross-domain-policy><allow-access-from domain=\"*\" to-ports=\"");
		sb.append(adr.getPort());
		sb.append("\" /></cross-domain-policy>\0");

		return sb.toString();
	}

	@Override
	public void onWebsocketHandshakeReceivedAsClient(WebSocket conn, ClientHandshake request, ServerHandshake response)
			throws InvalidDataException {
	}

	/**
	 * This default implementation does not do anything. Go ahead and overwrite
	 * it.
	 * 
	 * @see com.github.xzzpig.pigutils.websocket.WebSocketListener#onWebsocketHandshakeReceivedAsServer(WebSocket,
	 *      Draft, ClientHandshake)
	 */
	@Override
	public ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer(WebSocket conn, Draft draft,
			ClientHandshake request) throws InvalidDataException {
		return new HandshakeImpl1Server();
	}

	/**
	 * This default implementation does not do anything which will cause the
	 * connections to always progress.
	 * 
	 * @see com.github.xzzpig.pigutils.websocket.WebSocketListener#onWebsocketHandshakeSentAsClient(WebSocket,
	 *      ClientHandshake)
	 */
	@Override
	public void onWebsocketHandshakeSentAsClient(WebSocket conn, ClientHandshake request) throws InvalidDataException {
	}

	/**
	 * This default implementation does not do anything. Go ahead and overwrite
	 * it
	 * 
	 * @see com.github.xzzpig.pigutils.websocket.WebSocketListener#onWebsocketMessageFragment(WebSocket,
	 *      Framedata)
	 */
	@Override
	public void onWebsocketMessageFragment(WebSocket conn, Framedata frame) {
	}

	/**
	 * This default implementation will send a pong in response to the received
	 * ping. The pong frame will have the same payload as the ping frame.
	 * 
	 * @see com.github.xzzpig.pigutils.websocket.WebSocketListener#onWebsocketPing(WebSocket,
	 *      Framedata)
	 */
	@Override
	public void onWebsocketPing(WebSocket conn, Framedata f) {
		FramedataImpl1 resp = new FramedataImpl1(f);
		resp.setOptcode(Opcode.PONG);
		conn.sendFrame(resp);
	}

	/**
	 * This default implementation does not do anything. Go ahead and overwrite
	 * it.
	 * 
	 * @see com.github.xzzpig.pigutils.websocket.WebSocketListener#onWebsocketPong(WebSocket,
	 *      Framedata)
	 */
	@Override
	public void onWebsocketPong(WebSocket conn, Framedata f) {
	}

}
