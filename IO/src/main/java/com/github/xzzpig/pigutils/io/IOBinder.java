package com.github.xzzpig.pigutils.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOBinder<I extends InputStream, O extends OutputStream> extends Thread {

	private class DefaultIOSolver implements IOSolver {
		@Override
		public void solve(IOBinder<?, ?> binder) {
			int i = 0;
			while (binder.isAlive()) {
				try {
					i = inputStream.read();
					if (i == -1)
						break;
					outputStream.write(i);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private I inputStream;
	private O outputStream;
	private IOSolver solver;

	public IOBinder(I in, O out) {
		this.inputStream = in;
		this.outputStream = out;
		this.solver = new DefaultIOSolver();
	}

	public IOBinder(I in, O out, IOSolver solver) {
		this(in, out);
		this.solver = solver;
	}

	public I getInputStream() {
		return inputStream;
	}

	public O getOutputStream() {
		return outputStream;
	}

	@Override
	public void run() {
		solver.solve(this);
	}
}
