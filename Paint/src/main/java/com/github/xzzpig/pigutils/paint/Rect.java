package com.github.xzzpig.pigutils.paint;

public class Rect {
	public int top, left, height, width;

	public Rect() {
	}

	public Rect(int top, int left, int height, int width) {
		this.top = top;
		this.left = left;
		this.height = height;
		this.width = width;
	}

	public Rect(Rect r) {
		top = r.top;
		left = r.left;
		height = r.height;
		width = r.width;
	}

	public boolean contains(int x, int y) {
		if (x < getLeft() || x > getRight() || y < getTop() || y > getBottom())
			return false;
		return true;
	}

	public int getBottom() {
		return top + height;
	}

	public int getHeight() {
		return height;
	}

	public int getLeft() {
		return left;
	}

	public int getRight() {
		return left + width;
	}

	public int getTop() {
		return top;
	}

	public int getWidth() {
		return width;
	}

	public Rect setBottom(int bottom) {
		this.height = bottom - top;
		return this;
	}

	public Rect setHeight(int height) {
		this.height = height;
		return this;
	}

	public Rect setLeft(int left) {
		this.left = left;
		return this;
	}

	public Rect setRight(int right) {
		this.width = right - left;
		return this;
	}

	public Rect setTop(int top) {
		this.top = top;
		return this;
	}

	public Rect setWidth(int width) {
		this.width = width;
		return this;
	}

	@Override
	public String toString() {
		return "Rect{" + getLeft() + "," + getTop() + "," + getWidth() + "," + getHeight() + "}";
	}
}
