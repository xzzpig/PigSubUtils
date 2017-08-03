package com.github.xzzpig.pigutils.pack.socket.eventdrive;

import com.github.xzzpig.pigutils.event.EventAdapter;
import com.github.xzzpig.pigutils.event.EventBus;
import com.github.xzzpig.pigutils.pack.Package;
import com.github.xzzpig.pigutils.pack.socket.PackageSocketClient;

public class EDPackageSocketClient extends PackageSocketClient implements EventAdapter {

	private EventBus bus = new EventBus();

	public EDPackageSocketClient(String ip, int port) {
		super(ip, port);
	}

	@Override
	public void onClose() {
		bus.callEvent(new PackageSocketCloseEvent(this));
	}

	@Override
	public void onError(Exception exception) {
		bus.callEvent(new PackageSocketErrorEvent(this, exception));
	}

	@Override
	public void onOpen() {
		bus.callEvent(new PackageSocketOpenEvent(this));
	}

	@Override
	public void onPackage(Package pack) {
		bus.callEvent(new PackageSocketPackageEvent(this, pack));
	}

	@Override
	public EventBus getEventBus() {
		return bus;
	}

}
