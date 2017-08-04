package com.github.xzzpig.pigutils.paint;

import java.awt.Font;

public class ZoomTextItem extends TextItem {

	public enum ZoomType {
		Fill, Height, Suit, Width;
	}

	private Rect tarRect;
	private ZoomType type;

	public ZoomTextItem(String name, String str, Font font, Rect targetSize, ZoomType type) {
		super(name, str, font);
		this.type = type;
		this.tarRect = targetSize;
	}

	@Override
	public Rect getSize() {
		// th:gh=tw:gw
		Rect rect = new Rect(tarRect);
		rect = getZoomSize(tarRect, super.getSize(), type);
		// if (type == ZoomType.Fill) {}
		// else if (type == ZoomType.Height) {
		// rect.width =
		// super.getSize().width*tarRect.height/super.getSize().height;
		// }
		// else if (type == ZoomType.Width) {
		// rect.height =
		// super.getSize().height*tarRect.width/super.getSize().width;
		// }else {
		// rect.width =
		// super.getSize().width*tarRect.height/super.getSize().height;
		// if (rect.width > tarRect.width) {
		// rect.height = tarRect.height*rect.width/tarRect.width;
		// rect.width = tasrRect.width;
		// }
		// }
		rect.left = super.getSize().left;
		rect.top = super.getSize().top;
		return rect;
	}

	private Rect getZoomSize(Rect tar, Rect now, ZoomType type) {
		Rect rect = new Rect(tar);
		if (type == ZoomType.Fill) {
		} else if (type == ZoomType.Height) {
			rect.width = now.width * tarRect.height / now.height;
		} else if (type == ZoomType.Width) {
			rect.height = now.height * tarRect.width / now.width;
		} else {
			rect = getZoomSize(tar, now, ZoomType.Height);
			if (rect.width > tar.width) {
				rect = getZoomSize(tar, rect, ZoomType.Width);
			}
		}
		return rect;
	}
}
