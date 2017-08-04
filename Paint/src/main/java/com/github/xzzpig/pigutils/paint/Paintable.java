package com.github.xzzpig.pigutils.paint;

import java.awt.Graphics;
import java.awt.Image;

public interface Paintable {
	enum SizeType {
		CUT, ZOOM
	}

	Image getFinalImage();

	Image getImage();

	Image getSizedImage(Rect size, SizeType type);

	void paint(Graphics g);
}
