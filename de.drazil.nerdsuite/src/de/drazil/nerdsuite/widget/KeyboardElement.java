package de.drazil.nerdsuite.widget;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.enums.Style;

public class KeyboardElement extends Canvas implements PaintListener {

	private final static int SIZE = 40;
	private int id;
	private Style s;
	private int calculatedSize;
	private Color backgroundColor;
	private boolean pressed;
	private boolean canToggle = false;
	private List<IHitKeyListener> list;
	private int controlType;

	public KeyboardElement(Composite parent, int style, int id, Style s) {
		this(parent, style, id, s, false, 0);
	}

	public KeyboardElement(Composite parent, int style, int id, Style s, boolean canToggle, int controlType) {
		super(parent, style);
		list = new ArrayList<IHitKeyListener>();
		this.id = id;
		this.s = s;
		this.canToggle = canToggle;
		this.controlType = controlType;
		this.calculatedSize = (int) (40 * s.getSize());
		backgroundColor = getDefaultBackgroundColor();
		addPaintListener(this);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {

			}

			@Override
			public void mouseDown(MouseEvent e) {
				if (canToggle) {
					pressed = !pressed;
					if (pressed) {
						backgroundColor = Constants.BRIGHT_ORANGE;
						fireHitKey(controlType, id);
					} else {
						backgroundColor = Constants.DARK_GREY;
					}
				} else {
					backgroundColor = Constants.DARK_GREY;
					fireHitKey(controlType, id);
				}
				redraw();
			}
		});
		addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseEnter(MouseEvent e) {
				if (canToggle && pressed) {
					backgroundColor = Constants.BRIGHT_ORANGE;
				} else {
					backgroundColor = Constants.DARK_GREY;
				}
				redraw();
			}

			@Override
			public void mouseExit(MouseEvent e) {
				if (canToggle && pressed) {
					backgroundColor = Constants.BRIGHT_ORANGE;
				} else {
					backgroundColor = getDefaultBackgroundColor();
				}
				redraw();
			}
		});
	}

	@Override
	public void paintControl(PaintEvent e) {
		if (!s.isFiller()) {

			e.gc.setBackground(backgroundColor);
			e.gc.fillRectangle(0, 0, calculatedSize - 1, SIZE - 1);
			e.gc.setForeground(Constants.BLACK);
			e.gc.drawRectangle(0, 0, calculatedSize - 1, SIZE - 1);
		}

	}

	public Point getDimension() {
		return new Point(calculatedSize, SIZE);
	}

	private Color getDefaultBackgroundColor() {
		return Constants.WHITE;
	}

	@Override
	public Point computeSize(int wHint, int hHint) {
		int width = wHint != SWT.DEFAULT ? Math.min(wHint, calculatedSize) : calculatedSize;
		int height = hHint != SWT.DEFAULT ? Math.min(hHint, SIZE) : SIZE;
		return new Point(width, height);
	}

	public void addHitKeyListener(IHitKeyListener listener) {
		list.add(listener);
	}

	public void removeHitKeyListener(IHitKeyListener listener) {
		list.remove(listener);
	}

	private void fireHitKey(int controlType, int keyCode) {
		list.forEach(k -> k.keyPressed(controlType, keyCode));
	}

}
