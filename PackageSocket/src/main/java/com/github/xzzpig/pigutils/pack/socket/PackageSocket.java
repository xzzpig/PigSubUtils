package com.github.xzzpig.pigutils.pack.socket;

import java.io.IOException;
import java.net.Socket;

import com.github.xzzpig.pigutils.pack.Package;

public class PackageSocket {

	protected Socket socket;

	protected PackageSocket() {
	}

	public PackageSocket(Socket socket) {
		this.socket = socket;
	}

	public Socket getSocket() {
		return socket;
	}

	public synchronized PackageSocket send(Package pack) {
		synchronized (socket) {
			try {
				pack.write(socket.getOutputStream());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return this;
	}
}
