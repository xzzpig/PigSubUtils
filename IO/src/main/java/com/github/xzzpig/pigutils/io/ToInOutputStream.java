package com.github.xzzpig.pigutils.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Vector;

public class ToInOutputStream extends OutputStream {

	class ToInInputStream extends InputStream {
		@Override
		public int available() throws IOException {
			return buffers.size();
		}

		public ToInOutputStream getOutputStream() {
			return ToInOutputStream.this;
		}

		@Override
		public int read() throws IOException {
			while (buffers.size() == 0)
				;
			synchronized (buffers) {
				return buffers.remove(0);
			}
		}
	}

	private List<Integer> buffers = new Vector<>();

	@Override
	public void close() throws IOException {
		super.close();
		synchronized (buffers) {
			buffers.add(-1);
		}
	}

	public ToInInputStream getInputStream() {
		return new ToInInputStream();
	}

	public byte[] toByteArray() {
		return toByteArray(false);
	}

	public byte[] toByteArray(boolean consume) {
		synchronized (buffers) {
			byte[] bytes = new byte[buffers.size()];
			for (int i = 0; i < bytes.length; i++)
				bytes[i] = (byte) (int) buffers.get(i);
			if (consume)
				buffers.clear();
			return bytes;
		}
	}

	public int[] toIntArray() {
		return toIntArray(false);
	}

	public int[] toIntArray(boolean consume) {
		synchronized (buffers) {
			int[] ints = new int[buffers.size()];
			for (int i = 0; i < ints.length; i++)
				ints[i] = (int) buffers.get(i);
			if (consume)
				buffers.clear();
			return ints;
		}
	}

	@Override
	public void write(int b) throws IOException {
		synchronized (buffers) {
			buffers.add(b);
		}
	}
}
