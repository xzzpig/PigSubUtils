package com.github.xzzpig.pigutils.pack.socket;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.xzzpig.pigutils.annoiation.NotNull;
import com.github.xzzpig.pigutils.pack.Package;
import com.github.xzzpig.pigutils.reflect.ClassUtils;

public abstract class PackageSocketClient extends PackageSocket implements Runnable {

	private final String ip;
	private final int port;

	private AtomicBoolean started;
	private Thread thread;

	public PackageSocketClient(@NotNull String ip, int port) {
		ClassUtils.checkThisConstructorArgs(ip, port);
		this.ip = ip;
		this.port = port;
		this.started = new AtomicBoolean(false);
	}

	public synchronized boolean isStarted() {
		return started.get();
	}

	public abstract void onClose();

	public abstract void onError(Exception exception);

	public abstract void onOpen();

	public abstract void onPackage(Package pack);

	@Override
	public void run() {
		try {
			this.socket = new Socket(ip, port);
			started.set(true);
		} catch (IOException e) {
			onError(e);
			return;
		}
		onOpen();
		while (!thread.isInterrupted() && !getSocket().isClosed()) {
			try {
				Package pack = Package.read(getSocket().getInputStream());
				onPackage(pack);
			} catch (IOException e) {
				onError(e);
			} catch (NegativeArraySizeException e) {
				break;
			}
		}
		onClose();
	}

	public void start() {
		thread = new Thread(this);
		thread.start();
	}

	public void stop() {
	}
}
