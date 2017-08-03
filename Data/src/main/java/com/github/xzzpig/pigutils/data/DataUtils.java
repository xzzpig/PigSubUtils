package com.github.xzzpig.pigutils.data;

import static java.util.Arrays.asList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.xzzpig.pigutils.YieldIterator;
import com.github.xzzpig.pigutils.annoiation.BaseOnClass;
import com.github.xzzpig.pigutils.annoiation.NotNull;
import com.github.xzzpig.pigutils.reflect.MethodUtils;

/**
 * 数据工具类
 * 
 * @author xzzpig
 */
public class DataUtils {

	public static interface EachConsumer<E> {
		EachResult consume(E e);
	}

	public static enum EachResult {
		/**
		 * 同for循环中的break关键词
		 */
		BREAK,
		/**
		 * 同for循环中的continue关键词
		 */
		CONTNUE,
		/**
		 * 同for循环中的return关键词<br/>
		 * 不保证必定返回<br/>
		 */
		RETURN
	}

	public static interface WithIndexEachConsumer<E> {
		EachResult consume(E e, int index);
	}

	/**
	 * 将数组变为 {@link Map}
	 * 
	 * @param kclazz
	 *            K
	 * @param vclazz
	 *            V
	 * @param objects
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> array2KVMap(@NotNull Class<K> kclazz, @NotNull Class<V> vclazz,
			@NotNull Object... objects) {
		return (Map<K, V>) array2Map(objects);
	}

	public static Map<?, ?> array2Map(@NotNull Object... objects) {
		return list2Map(Arrays.asList(objects));
	}

	public static Byte[] bytes2Bytes(byte[] bytes) {
		Byte[] integers = new Byte[bytes.length];
		for (int i = 0; i < bytes.length; i++)
			integers[i] = bytes[i];
		return integers;
	}

	public static byte[] bytes2Bytes(Byte[] bytes) {
		byte[] integers = new byte[bytes.length];
		for (int i = 0; i < bytes.length; i++)
			integers[i] = bytes[i];
		return integers;
	}

	/**
	 * @param list
	 * @param consumer
	 * @return should reutrn
	 */
	public static <E> boolean forEach(E[] list, EachConsumer<E> consumer) {
		return forEach(asList(list), consumer);
	}

	/**
	 * @param list
	 * @param consumer
	 * @return should reutrn
	 */
	public static <E> boolean forEach(List<E> list, EachConsumer<E> consumer) {
		for (E e : list) {
			EachResult result = consumer.consume(e);
			if (result == EachResult.BREAK)
				break;
			if (result == EachResult.RETURN)
				return true;
		}
		return false;
	}

	public static <E> boolean forEachWithIndex(E[] list, WithIndexEachConsumer<E> consumer) {
		return forEachWithIndex(asList(list), consumer);
	}

	/**
	 * @param list
	 * @param consumer
	 * @return should reutrn
	 */
	public static <E> boolean forEachWithIndex(List<E> list, WithIndexEachConsumer<E> consumer) {
		int i = 0;
		for (E e : list) {
			EachResult result = consumer.consume(e, i);
			i++;
			if (result == EachResult.BREAK)
				break;
			if (result == EachResult.RETURN)
				return true;
		}
		return false;
	}

	public static Integer[] ints2Integers(int[] ints) {
		Integer[] integers = new Integer[ints.length];
		for (int i = 0; i < ints.length; i++)
			integers[i] = ints[i];
		return integers;
	}

	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> list2KVMap(@NotNull Class<K> kclazz, @NotNull Class<V> vclazz,
			@NotNull List<?> list) {
		return (Map<K, V>) list2Map(list);
	}

	public static Map<?, ?> list2Map(@NotNull List<?> list) {
		MethodUtils.checkArgs(DataUtils.class, "list2Map", list);
		Map<Object, Object> map = new HashMap<>();
		Object key = null, value;
		for (int i = 0; i < list.size(); i++) {
			if (i % 2 == 0) {
				key = list.get(i);
			} else {
				value = list.get(i);
				map.put(key, value);
			}
		}
		return map;
	}

	/**
	 * @param end
	 * @return [0,end)数组
	 */
	public static int[] range(int end) {
		return range(0, end);
	}

	/**
	 * @param start
	 * @param end
	 * @return [start,end)数组
	 */
	public static int[] range(int start, int end) {
		return range(start, end, 1);
	}

	/**
	 * @param start
	 * @param end
	 * @param step
	 *            数组元素步长
	 * @return
	 */
	@BaseOnClass(YieldIterator.class)
	public static int[] range(int start, int end, int step) {
		YieldIterator<Integer> yieldIterator = new YieldIterator<>(yield -> {
			int i = start;
			while (i < end) {
				yield.adapt(i);
				i += step;
			}
		});
		int[] is = new int[(end - start) % step == 0 ? (end - start) / step : (end - start) / step + 1];
		int i = 0;
		while (yieldIterator.hasNext()) {
			is[i] = yieldIterator.next();
			i++;
		}
		return is;
	}

	private DataUtils() {
	}
}
