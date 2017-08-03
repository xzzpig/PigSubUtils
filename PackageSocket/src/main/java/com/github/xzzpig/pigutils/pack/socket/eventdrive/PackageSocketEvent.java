package com.github.xzzpig.pigutils.pack.socket.eventdrive;

import com.github.xzzpig.pigutils.event.Event;
import com.github.xzzpig.pigutils.pack.socket.PackageSocket;

public class PackageSocketEvent extends Event{

	private PackageSocket socket;
	
	public PackageSocketEvent(PackageSocket socket) {
		this.socket = socket;
	}
	
	public PackageSocket getPackageSocket(){
		return socket;
	}

}
