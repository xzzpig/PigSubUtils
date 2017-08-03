package com.github.xzzpig.pigutils.pack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import com.github.xzzpig.pigutils.annoiation.NotNull;
import com.github.xzzpig.pigutils.annoiation.Nullable;

public class Package {

	public static Package read(InputStream in) throws IOException {
		int typeSize = 0;
		int i = 0;
		byte[] bs = null;
		do {
			i = in.read();
			typeSize += i;
		} while (i == 255);
		bs = new byte[typeSize];
		in.read(bs);
		String type = new String(bs);
		int dataSize = 0;
		do {
			i = in.read();
			dataSize += i;
		} while (i == 255);
		bs = new byte[dataSize];
		int len = in.read(bs);
		if (len != bs.length) {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			bout.write(bs, 0, len);
			while (len != bs.length) {
				bs = new byte[bs.length - len];
				len = in.read(bs);
				bout.write(bs, 0, len);
			}
			bs = bout.toByteArray();
		}
		return new Package(type, bs);
	}

	protected byte[] data;

	protected String type;

	Package() {
	}

	public Package(@Nullable String type, @Nullable byte[] data) {
		this(type, data, data.length);
	}

	public Package(@Nullable String type, @Nullable byte[] data, @Nullable int dataSize) {
		this.type = type;
		this.data = Arrays.copyOf(data, dataSize);
	}

	public @NotNull byte[] getData() {
		return data == null ? new byte[0] : data;
	}

	public int getSize() {
		return getData().length;
	}

	public @NotNull String getType() {
		return (type == null || type.equals("")) ? "default" : type;
	}

	protected final byte[] int2Bytes(int i) {
		int num = i / 255;
		int res = i % 255;
		byte[] bs = new byte[num + 1];
		for (int j = 0; j < num; j++) {
			bs[j] = (byte) 255;
		}
		bs[num] = (byte) res;
		return bs;
	}

	public Package write(OutputStream out) throws IOException {
		int typeSize = getType().length();
		out.write(int2Bytes(typeSize));
		out.write(getType().getBytes());
		out.write(int2Bytes(getSize()));
		out.write(getData());
		return this;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "{" + getType() + ":" + Arrays.toString(getData()) + "}";
	}
}
