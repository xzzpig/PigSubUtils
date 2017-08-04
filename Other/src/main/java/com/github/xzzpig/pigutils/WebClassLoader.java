package com.github.xzzpig.pigutils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

public class WebClassLoader extends ClassLoader {
	public static byte[] readStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = inStream.read(buffer)) != -1) {
			outSteam.write(buffer, 0, len);
		}
		outSteam.close();
		inStream.close();
		return outSteam.toByteArray();
	}

	String host;

	int port;

	public WebClassLoader(String url) {
		if (url.endsWith("/")) {
			host = url;
		} else {
			host = url + '/';
		}
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		try {
			Class<?> r = super.findClass(name);
			return r;
		} catch (ClassNotFoundException e1) {
		}
		byte[] b;
		try {
			b = loadClassData(name);
			return defineClass(name, b, 0, b.length);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private byte[] loadClassData(String name) throws Exception {
		URL url = new URL(host + name.replace('.', '/') + ".class");
		InputStream inputStream = url.openStream();
		return readStream(inputStream);
	}
}
