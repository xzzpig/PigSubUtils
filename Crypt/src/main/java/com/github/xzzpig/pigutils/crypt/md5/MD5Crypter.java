package com.github.xzzpig.pigutils.crypt.md5;

import com.github.xzzpig.pigutils.crypt.Cryptable;
import com.github.xzzpig.pigutils.crypt.Crypter;
import com.github.xzzpig.pigutils.crypt.Decryptable;

public class MD5Crypter extends Crypter {

	public MD5Crypter() {
	}

	@Override
	protected Cryptable crypt(Object... objs) {
		StringBuffer sb = new StringBuffer();
		for (Object obj : objs)
			sb.append(obj);
		return new MD5Crypt(sb.toString());
	}

	@Override
	protected Decryptable decrypt(Object... objs) {
		return null;
	}

	@Override
	public String getCryptType() {
		return "MD5";
	}
}
