package com.github.xzzpig.pigutils;

import java.io.File;

public class TJython {
	public static String libPath = "./lib/";

	public static boolean build() {
		load();
		if (!hasLoad()) {
			downloadJar();
			load();
		}
		return hasLoad();
	}

	public static void downloadJar() {
		try {
			System.out.println("开始下载jython.jar");
			TDownload download = new TDownload("http://www.xzzpig.com/java/jython.jar");
			download.BUFFER_SIZE = 1024 * 64;
			File lib = new File(libPath);
			lib.mkdirs();
			File file = new File(lib, "jython.jar");
			download.start(file);
			download.isBarPrint(false);
			while (!download.isFinished()) {
				System.out.printf("%d/%d(%d%% %fKb/s)\n", download.getDownloadSize(), download.getSize(),
						download.getPrecent(), ((float) download.getSpeed(1000)) / 1024);
			}
			System.out.println("jython.jar下载完成");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static boolean hasLoad() {
		try {
			Class.forName("org.python.Version");
		} catch (ClassNotFoundException e) {
			return false;
		}
		return true;
	}

	public static void load() {
		TClass.loadJar(libPath);
	}
}
