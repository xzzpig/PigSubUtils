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
	public EventBus getEventBus() {
		return bus;
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

	public synchronized void waitForStarted(int timeOut) {
		if (isStarted())
			return;
		Thread thread = new Thread() {
			@Override
			public void run() {
				if (timeOut > 0)
					try {
						Thread.sleep(timeOut);
					} catch (InterruptedException e) {
					}
				else
					while (!this.isInterrupted())
						;
            }
        };
        thread.start();
		this.regRunner((PackageSocketOpenEvent event) -> thread.interrupt());
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
