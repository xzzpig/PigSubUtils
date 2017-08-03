package com.github.xzzpig.pigutils.pack.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.xzzpig.pigutils.annoiation.NotNull;
import com.github.xzzpig.pigutils.pack.Package;

public abstract class PackageSocketServer implements Runnable {

	ServerSocket ss;

	private final int port;
	private AtomicBoolean started;
	private Thread thread;
	private List<PackageSocket> packageSockets;
	private Map<PackageSocket, Thread> packThreads;

	public PackageSocketServer(@NotNull int port) {
		this.port = port;
		started = new AtomicBoolean(false);
		packageSockets = new Vector<>();
		packThreads = new Hashtable<>();
	}

	public synchronized boolean isStarted() {
		return started.get();
	}

	public abstract void onClose(PackageSocket socket);

	public abstract void onError(PackageSocket socket, Exception exception);

	public abstract void onOpen(PackageSocket socket);

	public abstract void onPackage(PackageSocket socket, Package pack);

	private void onPSClose(PackageSocket socket) {
		packageSockets.remove(socket);
		packThreads.remove(socket);
		onClose(socket);
	}

	private void onPSOpen(PackageSocket socket) {
		if (socket == null)
			return;
		packageSockets.add(socket);
		onOpen(socket);
		Thread t = new Thread() {
			@Override
			public void run() {
				while (!isInterrupted() && !socket.getSocket().isClosed()) {
					try {
						Package pack = Package.read(socket.getSocket().getInputStream());
						onPackage(socket, pack);
					} catch (IOException e) {
						onError(socket, e);
					} catch (NegativeArraySizeException e) {
						break;
					}
				}
				onPSClose(socket);
			}
		};
		packThreads.put(socket, t);
		t.start();
	}

	@Override
	public final void run() {
		try {
			ss = new ServerSocket(port);
			started.set(true);
		} catch (IOException e) {
			onError(null, e);
			return;
		}
		while (!thread.isInterrupted()) {
			PackageSocket socket = null;
			try {
				socket = new PackageSocket(ss.accept());
			} catch (IOException e) {
				onError(socket, e);
			}
			onPSOpen(socket);
		}
	}

	public void start() {
		thread = new Thread(this);
		thread.start();
	}

	public void stop() {
		for (Entry<PackageSocket, Thread> entry : packThreads.entrySet()) {
			try {
				entry.getKey().getSocket().close();
			} catch (IOException e) {
				onError(entry.getKey(), e);
			}
			entry.getValue().interrupt();
			// onPSClose(entry.getKey());
		}
		try {
			ss.close();
		} catch (IOException e) {
			onError(null, e);
		}
		thread.interrupt();
		started.set(false);
		onClose(null);
	}
}
