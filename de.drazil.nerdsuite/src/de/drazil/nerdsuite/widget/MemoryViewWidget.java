package de.drazil.nerdsuite.widget;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import de.drazil.nerdsuite.Constants;

public class MemoryViewWidget extends Canvas implements PaintListener {

	private Image image = null;
	private int width = 660;
	private int height = 960;

	public MemoryViewWidget(Composite parent, int style) {
		super(parent, style);
		prepareImage(width, height);
		addPaintListener(this);
	}

	private void prepareImage(int width, int height) {
		image = new Image(getDisplay(), width, 960);
		GC gc = new GC(image);
		gc.fillRectangle(0, 0, width, 960);
		gc.setForeground(Constants.WHITE);
		gc.drawString("$0000-$1fff", 0, 210);
		gc.drawString("$2000-$3fff", 0, 440);
		gc.drawString("$4000-$5fff", 0, 670);
		gc.drawString("$6000-$7fff", 0, 900);
		gc.drawString("$8000-$9fff", 330, 210);
		gc.drawString("$a000-$bfff", 330, 440);
		gc.drawString("$c000-$dfff", 330, 670);
		gc.drawString("$e000-$ffff", 330, 900);
		gc.dispose();
	}

	public void paintControl(PaintEvent e) {
		e.gc.drawImage(image, 0, 0);
	}

	public void setByte(int address, int value, boolean rw) {

		int segmentStartY = ((address / 0x2000) % 4) * 230;
		int segmentStartX = (address / 0x8000) * 330;
		int si = address % 0x2000;
		int x = segmentStartX + ((si / 8) % 40 * 8);
		int y = segmentStartY + ((si % 8) + (si / 320) * 8);

		GC gc = new GC(image);

		int i = 1;
		int x2 = 8;
		while (i < 256) {
			if ((value & i) == i) {
				gc.setForeground(rw ? Constants.GREEN : Constants.RED);
			} else {
				gc.setForeground(Constants.BLACK);
			}
			gc.drawPoint(x + x2, y);
			i = i << 1;
			x2--;
		}

		gc.dispose();
		redraw(x, y, 8, 1, false);
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point(640, 819);
	}
}
