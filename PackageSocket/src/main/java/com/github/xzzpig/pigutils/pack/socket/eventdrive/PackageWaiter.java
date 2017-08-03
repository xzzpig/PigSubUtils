package com.github.xzzpig.pigutils.pack.socket.eventdrive;

import java.util.concurrent.atomic.AtomicReference;

import com.github.xzzpig.pigutils.event.EventAdapter;
import com.github.xzzpig.pigutils.event.EventRunner;
import com.github.xzzpig.pigutils.pack.Package;

public class PackageWaiter {

	EventAdapter socket;
	AtomicReference<Package> pack = new AtomicReference<>();

	public PackageWaiter(EDPackageSocketClient client) {
		this.socket = client;
	}

	public PackageWaiter(EDPackageSocketServer server) {
		this.socket = server;
	}

	private String type;

	private Thread thread;

	public synchronized Package waitForPackage(String type) {
		this.type = type;
		thread = new Thread() {
			public void run() {
				while (!this.isInterrupted()) {
				}
			};
		};
		thread.start();
		EventRunner<PackageSocketPackageEvent> runner = this::onPackage;
		socket.regRunner(runner);
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Package p = pack.get();
		pack.set(null);
		socket.unregRunner(runner::equals);
		return p;
	}

	private void onPackage(PackageSocketPackageEvent event) {
		if (event.getPackage().getType().equals(type)) {
			pack.set(event.getPackage());
			thread.interrupt();
		}
	}

}
