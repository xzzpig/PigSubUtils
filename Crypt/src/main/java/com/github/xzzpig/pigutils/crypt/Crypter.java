package com.github.xzzpig.pigutils.crypt;

import java.util.function.Predicate;

import com.github.xzzpig.pigutils.core.Transformer;
import com.github.xzzpig.pigutils.crypt.md5.FileMD5Crypter;
import com.github.xzzpig.pigutils.crypt.md5.MD5Crypter;

/**
 * 加密器
 */
public abstract class Crypter {

	private static class CrypterTransformr implements Transformer<String, Crypter> {
		private Crypter crypter;

		public CrypterTransformr(Crypter crypter) {
			this.crypter = crypter;
		}

		@Override
		public String toString() {
			return "crypt." + crypter.getCryptType();
		}

		@Override
		public Crypter transform(String str) {
			if (str.equals("crypt." + crypter.getCryptType()))
				return crypter;
			else
				return null;
		}
	}

	static {
		regCrypter(new MD5Crypter());
		regCrypter(new FileMD5Crypter());
	}

	/**
	 * 获取type类型的 {@link Crypter} 并取得 objs 的加密结果
	 * 
	 * @param type
	 * @param objs
	 * @return
	 */
	public static Cryptable crypt(String type, Object... objs) {
		Crypter crypter = Transformer.transform("crypt." + type, Crypter.class);
		return crypter == null ? null : crypter.crypt(objs);
	}

	/**
	 * 获取type类型的 {@link Crypter} 并取得 objs 的加密结果
	 * 
	 * @param type
	 * @param objs
	 * @return
	 */
	public static Decryptable decrypt(String type, Object... objs) {
		Crypter crypter = Transformer.transform("crypt." + type, Crypter.class);
		return crypter == null ? null : crypter.decrypt(objs);
	}

	/**
	 * 注册加密器
	 * 
	 * @param crypter
	 */
	public static void regCrypter(Crypter crypter) {
		Transformer.addTransformer(new CrypterTransformr(crypter));
	}

	/**
	 * 解除注册加密器
	 * 
	 * @param crypter
	 */
	public static void unregCrypter(Crypter crypter) {
		Transformer.transformers.removeIf(((Predicate<Transformer<?, ?>>) (t -> t instanceof CrypterTransformr))
				.and((t) -> ((CrypterTransformr) t).crypter.getCryptType().equalsIgnoreCase(crypter.getCryptType())));
	}

	protected abstract Cryptable crypt(Object... objs);

	protected abstract Decryptable decrypt(Object... objs);

	/**
	 * @return 加密类型
	 */
	public abstract String getCryptType();
}
