package com.github.xzzpig.pigutils.pack.socket.eventdrive;

import com.github.xzzpig.pigutils.pack.socket.PackageSocket;

public class PackageSocketErrorEvent extends PackageSocketEvent {

	private Exception error;

	public PackageSocketErrorEvent(PackageSocket socket, Exception error) {
		super(socket);
		this.error = error;
	}

	public Exception getError() {
		return error;
	}
}
