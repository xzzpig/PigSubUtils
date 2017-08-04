package com.github.xzzpig.pigutils.crypt.md5;

import java.io.File;

import com.github.xzzpig.pigutils.crypt.Cryptable;
import com.github.xzzpig.pigutils.crypt.Crypter;
import com.github.xzzpig.pigutils.crypt.Decryptable;

public class FileMD5Crypter extends Crypter {

	public FileMD5Crypter() {
	}

	@Override
	protected Cryptable crypt(Object... objs) {
		try {
			File file = (File) objs[0];
			return new FileMD5Crypt(file);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	protected Decryptable decrypt(Object... objs) {
		return null;
	}

	@Override
	public String getCryptType() {
		return "FileMD5";
	}
}
