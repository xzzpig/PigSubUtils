package com.github.xzzpig.pigutils.logger;

import java.io.IOException;
import java.io.OutputStream;

public abstract class OutputStreamLogPrinter extends LogPrinter {

	protected OutputStream out;

	public OutputStreamLogPrinter(OutputStream out) {
		this.out = out;
	}

	@Override
	public void print(String log) {
		try {
			out.write(log.getBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
