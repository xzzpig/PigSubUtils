package com.github.xzzpig.pigutils.pack;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.github.xzzpig.pigutils.annoiation.NotNull;
import com.github.xzzpig.pigutils.annoiation.Nullable;
import com.github.xzzpig.pigutils.reflect.ClassUtils;

public class WrapperPackage extends Package {

	protected Package pack;

	protected WrapperPackage() {
	}

	public WrapperPackage(@Nullable String type, @Nullable byte[] data) {
		ByteArrayInputStream bin = new ByteArrayInputStream(data);
		unWrapPackage(type, bin);
		try {
			bin.close();
		} catch (IOException e) {
		}
	}

	protected void unWrapPackage(String type, ByteArrayInputStream data) {
		byte[] bs = new byte[data.available()];
		try {
			data.read(bs);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		pack = new Package(type, bs);
	}

	public WrapperPackage(@NotNull Package pack) {
		ClassUtils.checkThisConstructorArgs(pack);
		this.pack = pack;
	}

	public Package getWrappedPackage() {
		return pack;
	}

	@Override
	public byte[] getData() {
		return pack.getData();
	}

	@Override
	public String getType() {
		return pack.getType();
	}
}
