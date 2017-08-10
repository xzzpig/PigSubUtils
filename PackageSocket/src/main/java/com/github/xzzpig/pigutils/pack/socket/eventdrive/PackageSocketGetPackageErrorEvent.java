package com.github.xzzpig.pigutils.pack.socket.eventdrive;

import com.github.xzzpig.pigutils.pack.socket.PackageSocket;

public class PackageSocketGetPackageErrorEvent extends PackageSocketEvent {

	private Exception error;
	boolean disconnect;

	public PackageSocketGetPackageErrorEvent(PackageSocket socket, Exception error) {
		super(socket);
		this.error = error;
	}

	public Exception getError() {
		return error;
	}

	public PackageSocketGetPackageErrorEvent setDisconnect(boolean b) {
		this.disconnect = b;
		return this;
	}
}
