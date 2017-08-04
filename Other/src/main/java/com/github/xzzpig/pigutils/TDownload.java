package com.github.xzzpig.pigutils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class TDownload {
	public int BUFFER_SIZE = 1024;

	private long downloadsize;
	private Exception error;
	private boolean finish;
	private String name;
	private boolean print;
	private long size;

	private URL url;

	public TDownload(String url) throws Exception {
		this(new URL(url));
	}

	public TDownload(URL url) {
		setURL(url);
	}

	public long getDownloadSize() {
		return downloadsize;
	}

	public Exception getError() {
		return error;
	}

	public String getFileName() {
		return name;
	}

	public short getPrecent() {
		if (size == 0)
			return 0;
		return (short) (((float) downloadsize / (float) size) * 100);
	}

	public long getSize() {
		return size;

	}

	public long getSpeed(int logtime) {
		long start = downloadsize;
		try {
			Thread.sleep(logtime);
		} catch (InterruptedException e) {
		}
		return (long) ((double) (downloadsize - start) / (double) logtime);
	}

	public URL getURL() {
		return url;
	}

	public boolean hasError() {
		return this.error != null;
	}

	public TDownload isBarPrint(boolean arg) {
		this.print = arg;
		return this;
	}

	public boolean isFinished() {
		return finish;
	}

	public void setURL(URL url) {
		this.url = url;
	}

	public TDownload start(final File savedFile) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					File outFile = savedFile;
					name = outFile.getName();
					URLConnection conn = url.openConnection();
					size = conn.getContentLength();
					InputStream in = conn.getInputStream();
					if (outFile.isDirectory()) {
						outFile = new File(outFile, url.getFile());
						outFile.createNewFile();
					} else if (outFile.isFile()) {
						if (!outFile.exists())
							outFile.createNewFile();
					}
					FileOutputStream out = new FileOutputStream(outFile, false);
					byte[] buffer = new byte[BUFFER_SIZE];
					short i = 0;
					while (true) {
						int length = in.read(buffer);
						if (length == -1)
							break;
						downloadsize += length;
						out.write(buffer, 0, length);
						if (print) {
							for (int j = 0; j < (getPrecent() / 10 - i); j++) {
								System.out.print("[" + i + "]");
							}
							i = (short) (getPrecent() / 10);
						}
					}
					System.out.println();
					in.close();
					out.close();
				} catch (Exception e) {
					Debuger.print(e);
					error = e;
				} finally {
					finish = true;
				}
			}
		}).start();
		return this;
	}
}