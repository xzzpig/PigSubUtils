package com.github.xzzpig.pigutils.paint;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.github.xzzpig.pigutils.event.EventAdapter;
import com.github.xzzpig.pigutils.event.EventBus;

public abstract class PaintItem implements IContainer, Paintable, Sizeable, EventAdapter {

	private String name;
	private PaintItem parent;
	protected Rect size;
	private List<PaintItem> subs;
	private final EventBus event = new EventBus();

	public PaintItem(PaintItem parent, String name, Rect size) {
		subs = new ArrayList<>();
		setName(name);
		this.name = name;
		setSize(size);
		setParent(parent);
	}

	public PaintItem addSub(PaintItem item) {
		subs.add(item);
		item.setParent(this);
		return this;
	}

	public void clarAllSubs() {
		subs.clear();
	}

	@Override
	public abstract PaintItem clone();

	@Override
	public EventBus getEventBus() {
		return event;
	}

	@Override
	public final Image getFinalImage() {
		Image image = new BufferedImage(getSize().width, getSize().height, BufferedImage.TYPE_INT_ARGB);
		image.getGraphics().drawImage(getSizedImage(), 0, 0, null);
		for (PaintItem subItem : subs) {
			Image image2 = subItem.getFinalImage();
			image.getGraphics().drawImage(image2, subItem.getSize().left, subItem.getSize().top, null);
		}
		return image;
	}

	public final String getName() {
		return name;
	}

	@Override
	public PaintItem getParent() {
		return parent;
	}

	@Override
	public Rect getSize() {
		return size;
	}

	public Image getSizedImage() {
		return getSizedImage(getSize(), SizeType.ZOOM);
	}

	@Override
	public Image getSizedImage(Rect size, SizeType type) {
		if (type == SizeType.ZOOM) {
			return getImage().getScaledInstance(size.width, size.height, Image.SCALE_SMOOTH);
		} else {
			BufferedImage image = new BufferedImage(getSize().width, getSize().height, BufferedImage.TYPE_INT_ARGB);
			image.getGraphics().drawImage(getImage(), 0, 0, null);
			return image;
		}
	}

	@Override
	public PaintItem[] getSubs() {
		return subs.toArray(new PaintItem[0]);
	}

	public PaintItem removeSub(String name) {
		List<PaintItem> removeitems = new ArrayList<>();
		for (PaintItem item : subs) {
			if (item.name.equalsIgnoreCase(name))
				removeitems.add(item);
		}
		subs.removeAll(removeitems);
		return this;
	}

	public final PaintItem setName(String name) {
		this.name = name;
		return this;
	}

	public PaintItem setParent(PaintItem parent) {
		if (this.parent != null && this.parent != parent) {
			this.parent.removeSub(getName());
			parent.addSub(this);
		}
		this.parent = parent;
		return this;
	}

	public PaintItem setSize(Rect size) {
		this.size = new Rect(size);
		return this;
	}
}
