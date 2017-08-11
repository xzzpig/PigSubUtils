package com.github.xzzpig.pigutils.crypt.md5;

import static com.github.xzzpig.pigutils.core.MD5.GetMD5Code;

import com.github.xzzpig.pigutils.crypt.Cryptable;

public class MD5Crypt implements Cryptable {

	private String str;

	public MD5Crypt(String str) {
		this.str = str;
	}

	@Override
	public String crypt() {
		return GetMD5Code(str);
	}

	@Override
	public boolean match(Object obj) {
		return crypt().equalsIgnoreCase(GetMD5Code(obj + ""));
	}

}
