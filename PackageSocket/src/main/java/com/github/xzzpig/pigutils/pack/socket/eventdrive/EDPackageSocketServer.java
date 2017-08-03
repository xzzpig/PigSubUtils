package com.github.xzzpig.pigutils.pack.socket.eventdrive;

import com.github.xzzpig.pigutils.event.EventAdapter;
import com.github.xzzpig.pigutils.event.EventBus;
import com.github.xzzpig.pigutils.pack.Package;
import com.github.xzzpig.pigutils.pack.socket.PackageSocket;
import com.github.xzzpig.pigutils.pack.socket.PackageSocketServer;

public class EDPackageSocketServer extends PackageSocketServer implements EventAdapter {

	private EventBus bus = new EventBus();

	public EDPackageSocketServer(int port) {
		super(port);
	}

	@Override
	public void onClose(PackageSocket socket) {
		bus.callEvent(new PackageSocketCloseEvent(socket));
	}

	@Override
	public void onError(PackageSocket socket, Exception exception) {
		bus.callEvent(new PackageSocketErrorEvent(socket, exception));
	}

	@Override
	public void onOpen(PackageSocket socket) {
		bus.callEvent(new PackageSocketOpenEvent(socket));
	}

	@Override
	public void onPackage(PackageSocket socket, Package pack) {
		bus.callEvent(new PackageSocketPackageEvent(socket, pack));
	}

	@Override
	public EventBus getEventBus() {
		return bus;
	}

}
