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
import de.drazil.nerdsuite.model.Key;
import de.drazil.nerdsuite.model.PlatformColor;

public class KeyboardElement extends Canvas implements PaintListener {

	private final static int SIZE = 30;

	private Key key;
	private int calculatedSize;
	private Color backgroundColor;
	private List<IHitKeyListener> list;

	private List<PlatformColor> colorList;

	public KeyboardElement(Composite parent, int style, Key key, List<PlatformColor> colorList) {
		super(parent, style);
		this.colorList = colorList;
		list = new ArrayList<IHitKeyListener>();
		this.key = key;
		this.calculatedSize = (int) (SIZE * key.getSize());
		backgroundColor = getDefaultBackgroundColor();
		setFont(Constants.C64_Pro_Mono_FONT_12);
		addPaintListener(this);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {

			}

			@Override
			public void mouseDown(MouseEvent e) {
				if (key.isToggleButton()) {
					key.setToggleState(!key.isToggleState());
					if (key.isToggleState()) {
						backgroundColor = Constants.BRIGHT_ORANGE;
					} else {
						backgroundColor = Constants.DARK_GREY;
					}
					fireHitKey(key);
				} else {
					backgroundColor = Constants.DARK_GREY;
					fireHitKey(key);
				}
				redraw();
			}
		});
		addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseEnter(MouseEvent e) {
				if (key.isToggleButton() && key.isToggleState()) {
					backgroundColor = Constants.BRIGHT_ORANGE;
				} else {
					backgroundColor = Constants.DARK_GREY;
				}
				redraw();
			}

			@Override
			public void mouseExit(MouseEvent e) {
				if (key.isToggleButton() && key.isToggleState()) {
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
		if (!key.getType().equals("FILLER")) {
			Point textBounds = e.gc.stringExtent(key.getText());
			int xText = (calculatedSize - textBounds.x) / 2;
			int yText = (SIZE - textBounds.y) / 2;
			e.gc.setBackground(backgroundColor);
			e.gc.fillRectangle(2, 2, calculatedSize - 4, SIZE - 4);
			e.gc.setForeground(Constants.BLACK);
			e.gc.drawRectangle(2, 2, calculatedSize - 4, SIZE - 4);
			if (!key.getType().equals("KEY") && !key.getType().equals("COLOR")) {
				e.gc.drawString(key.getText(), xText, 10);
			} else {
				e.gc.drawString(key.getDisplay(), xText, 10);
			}
		}
	}

	public Point getDimension() {
		return new Point(calculatedSize, SIZE);
	}

	private Color getDefaultBackgroundColor() {
		return key.getType().equals("COLOR") && key.getIndex() != null ? colorList.get(key.getIndex()).getColor()
				: Constants.WHITE;
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

	private void fireHitKey(Key key) {
		list.forEach(k -> k.keyPressed(key));
	}

	public void refresh() {
		redraw();
	}
}
