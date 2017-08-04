package com.github.xzzpig.pigutils.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Vector;

public class GroupInputStream {

	public class SubInputStream extends InputStream {

		Vector<Integer> bytes;

		SubInputStream() {
			bytes = new Vector<>();
		}

		@Override
		public int read() throws IOException {
			while (bytes.size() == 0)
				;
			return bytes.remove(0);
		}
	}

	private InputStream in;
	private Thread thread;

	List<SubInputStream> subInputStreams;

	public GroupInputStream(InputStream in) {
		this.in = in;
		subInputStreams = new Vector<>();
	}

	public SubInputStream getSub() {
		SubInputStream sin = new SubInputStream();
		subInputStreams.add(sin);
		return sin;
	}

	public GroupInputStream removeSub(SubInputStream sin) {
		subInputStreams.remove(sin);
		return this;
	}

	public GroupInputStream start() {
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				int i = 0;
				while (i != -1) {
					try {
						i = in.read();
						for (SubInputStream sin : subInputStreams)
							sin.bytes.add(i);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		thread.start();
		return this;
	}

	public void stop() {
		thread.interrupt();
	}

}
