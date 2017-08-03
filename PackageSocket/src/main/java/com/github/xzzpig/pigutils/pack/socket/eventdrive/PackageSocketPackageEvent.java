package com.github.xzzpig.pigutils.pack.socket.eventdrive;

import com.github.xzzpig.pigutils.pack.Package;
import com.github.xzzpig.pigutils.pack.socket.PackageSocket;

public class PackageSocketPackageEvent extends PackageSocketEvent {

	private com.github.xzzpig.pigutils.pack.Package pack;

	public PackageSocketPackageEvent(PackageSocket socket, com.github.xzzpig.pigutils.pack.Package pack) {
		super(socket);
		this.pack = pack;
	}

	public Package getPackage() {
		return pack;
	}
}
