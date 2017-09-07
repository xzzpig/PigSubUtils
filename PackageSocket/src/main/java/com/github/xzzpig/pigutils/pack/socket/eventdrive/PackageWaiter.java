package com.github.xzzpig.pigutils.pack.socket.eventdrive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.github.xzzpig.pigutils.event.EventAdapter;
import com.github.xzzpig.pigutils.event.EventRunner;
import com.github.xzzpig.pigutils.pack.Package;

public class PackageWaiter {

	AtomicReference<Package> pack = new AtomicReference<>();
	EventAdapter socket;

	private Thread thread;

	private List<String> type;

	public PackageWaiter(EDPackageSocketClient client) {
		this.socket = client;
	}

	public PackageWaiter(EDPackageSocketServer server) {
		this.socket = server;
	}

	private void onPackage(PackageSocketPackageEvent event) {
		if (type != null && type.contains(event.getPackage().getType())) {
			pack.set(event.getPackage());
			if (thread != null && thread.isAlive())
				thread.interrupt();
		}
	}

	public synchronized Package waitForPackage(String type) {
		return waitForPackage(type, -1);
	}

	public synchronized Package waitForPackage(String type2, int timeout) {
		return waitForPackage(timeout, type2);
	}

	public synchronized Package waitForPackage(int timeout, String... types) {
		this.type = new ArrayList<>(Arrays.asList(types));
		thread = new Thread() {
			public void run() {
				if (timeout > 0)
					try {
						Thread.sleep(timeout);
					} catch (InterruptedException e) {
					}
				else
					while (!this.isInterrupted()) {
					}
            }
        };
        EventRunner<PackageSocketPackageEvent> runner = this::onPackage;
		socket.regRunner(runner);
		thread.setDaemon(true);
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
		}
		Package p = pack.get();
		pack.set(null);
		socket.unregRunner(runner::equals);
		return p;
	}

}
