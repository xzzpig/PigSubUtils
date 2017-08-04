package com.github.xzzpig.pigutils.pigsimpleweb.event;

import com.github.xzzpig.pigutils.pigsimpleweb.PigSimpleWebServer;

/*
 * 获取服务器根目录时触发
 * 用于设置根目录
 * 默认为System.getProperty("user.dir")
 */
public class PigSWSGetRootEvent extends PigSWSEvent {
	private String root;

	public PigSWSGetRootEvent(PigSimpleWebServer psws) {
		super(psws);
		root = System.getProperty("user.dir");
	}

	public String getRoot() {
		if (root.endsWith("/")) {
			root = root.substring(0, root.length() - 1);
		}
		return root;
	}

	public void setRoot(String r) {
		root = r;
	}
}
