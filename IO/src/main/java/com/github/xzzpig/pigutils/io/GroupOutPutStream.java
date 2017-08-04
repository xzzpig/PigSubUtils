package com.github.xzzpig.pigutils.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

public class GroupOutPutStream extends OutputStream {

	List<OutputStream> outs;

	public GroupOutPutStream() {
		super();
		outs = new LinkedList<>();
	}

	public GroupOutPutStream add(OutputStream... outs) {
		for (OutputStream outputStream : outs) {
			this.outs.add(outputStream);
		}
		return this;
	}

	@Override
	public void close() throws IOException {
		for (OutputStream out : outs) {
			out.close();
		}
	}

	@Override
	public void flush() throws IOException {
		for (OutputStream out : outs) {
			out.flush();
		}
	}

	protected List<OutputStream> getOutPutStreams() {
		return outs;
	}

	@Override
	public void write(int b) throws IOException {
		for (OutputStream out : outs) {
			out.write(b);
		}
	}

}
