package com.github.xzzpig.pigutils.crypt.md5;

import static com.github.xzzpig.pigutils.MD5.GetMD5Code;

import java.io.File;
import java.io.FileNotFoundException;

import com.github.xzzpig.pigutils.crypt.Cryptable;

public class FileMD5Crypt implements Cryptable {

	private File str;

	public FileMD5Crypt(File str) {
		this.str = str;
	}

	@Override
	public String crypt() {
		try {
			return GetMD5Code(str);
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	@Override
	public boolean match(Object obj) {
		if (!(obj instanceof File))
			return false;
		File f = (File) obj;
		String cry = crypt();
		if (cry == null)
			return false;
		try {
			return cry.equalsIgnoreCase(GetMD5Code(f));
		} catch (FileNotFoundException e) {
			return false;
		}
	}

}
