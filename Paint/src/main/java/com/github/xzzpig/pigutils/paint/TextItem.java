package com.github.xzzpig.pigutils.paint;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class TextItem extends PaintItem {

	private Color color;
	private Font font;
	private String str;

	public TextItem(String name, String str, Font font) {
		super(null, name, new Rect(0, 0, 10, 10));
		this.str = str;
		this.font = font;
		this.color = Color.BLACK;
		reSize();
	}

	@Override
	public TextItem clone() {
		return new TextItem(getName(), str, font);
	}

	public Color getColor() {
		return color;
	}

	@Override
	public Image getImage() {
		BufferedImage image = new BufferedImage(size.getWidth(), size.getHeight(), BufferedImage.TYPE_INT_ARGB);
		paint(image.getGraphics());
		return image;
	}

	public String getStr() {
		return str;
	}

	@Override
	public void paint(Graphics g) {
		g.setFont(font);
		g.setColor(getColor());
		g.drawString(getStr(), 0, (int) (size.getHeight() * (.6)));
	}

	private void reSize() {
		BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
		Graphics g = image.getGraphics();
		g.setFont(font);
		size.setWidth(g.getFontMetrics().stringWidth(getStr()));
		size.setHeight(g.getFontMetrics().getHeight());
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setFont(Font font) {
		this.font = font;
		reSize();
	}

	public void setLeft(int left) {
		size.left = left;
	}

	public void setTop(int top) {
		size.top = top;
	}
}
