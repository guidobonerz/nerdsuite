package de.drazil.nerdsuite.widget;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import de.drazil.nerdsuite.Constants;

public class Toolbox extends BaseWidget implements PaintListener {
	private IEventBroker broker;

	private static final int WIDTH = 180;
	private static final int HEIGHT = 100;

	public Toolbox(Composite parent, int style, IEventBroker broker) {
		super(parent, style);
		this.broker = broker;
		parent.addPaintListener(this);
	}

	@Override
	public void paintControl(PaintEvent e) {
		GC gc = e.gc;
		gc.setBackground(Constants.GREEN);
		gc.fillRectangle(0, 0, WIDTH, HEIGHT);
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point(WIDTH, HEIGHT);
	}
}
