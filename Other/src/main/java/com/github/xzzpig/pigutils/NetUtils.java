package com.github.xzzpig.pigutils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

public class NetUtils {
	/**
	 * @param ln
	 *            mac间的连接符
	 * @return mac地址
	 * @throws SocketException
	 * @throws UnknownHostException
	 */
	public static String getLocalMac(InetAddress address, String ln) throws SocketException, UnknownHostException {
		byte[] mac = NetworkInterface.getByInetAddress(address).getHardwareAddress();
		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < mac.length; i++) {
			if (i != 0) {
				sb.append(ln);
			}
			int temp = mac[i] & 0xff;
			String str = Integer.toHexString(temp);
			if (str.length() == 1) {
				sb.append("0" + str);
			} else {
				sb.append(str);
			}
		}
		return sb.toString();
	}

	private NetUtils() {
	}
}
