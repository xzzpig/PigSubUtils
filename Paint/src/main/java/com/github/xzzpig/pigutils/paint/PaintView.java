package com.github.xzzpig.pigutils.paint;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class PaintView extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7776371277251636699L;

	private List<PaintItem> paints = new ArrayList<>();

	public PaintView() {
	}

	public void addPaintItem(PaintItem paintItem) {
		paints.add(paintItem);
	}

	public List<PaintItem> getPaints() {
		return paints;
	}

	@Override
	protected void paintComponent(Graphics arg0) {
		super.paintComponent(arg0);
		paints.forEach((PaintItem paint) -> {
			Rect size = paint.getSize();
			arg0.drawImage(paint.getFinalImage(), size.getLeft(), size.getTop(), null);
		});
	}
}
