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
	private boolean mouseIn = false;
	private List<PlatformColor> colorList;

	public KeyboardElement(Composite parent, int style, Key key, List<PlatformColor> colorList) {
		super(parent, style);
		this.colorList = colorList;
		list = new ArrayList<IHitKeyListener>();
		this.key = key;
		this.calculatedSize = (int) (SIZE * key.getSize());
		backgroundColor = getDefaultBackgroundColor();
		if (key.isSymbol()) {
			setFont(Constants.GoogleMaterials);
		} else {
			setFont(Constants.C64_Pro_Mono_FONT_12);
		}
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
				mouseIn = true;
				if (key.isToggleButton() && key.isToggleState()) {
					backgroundColor = Constants.BRIGHT_ORANGE;
				} else {
					backgroundColor = getDefaultBackgroundColor();
				}
				redraw();
			}

			@Override
			public void mouseExit(MouseEvent e) {
				mouseIn = false;
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

			e.gc.setBackground(backgroundColor);
			e.gc.fillRoundRectangle(2, 2, calculatedSize - 4, SIZE - 4, 5, 5);
			e.gc.setForeground(Constants.WHITE);
			if ((!key.getType().equals("KEY") && !key.getType().equals("COLOR"))) {
				Point textBounds = e.gc.stringExtent(key.getText());
				int xText = (calculatedSize - textBounds.x) / 2;
				int yText = (SIZE - textBounds.y) / 2;
				e.gc.drawString(key.getText(), xText, yText);
			} else {
				Point textBounds = e.gc.stringExtent(key.getDisplay());
				int xText = (calculatedSize - textBounds.x) / 2;
				int yText = (SIZE - textBounds.y) / 2;
				if ((key.getOptionState() & 8) == 8 && !key.isSymbol()) {
					e.gc.setBackground(Constants.DARK_GREY);
					e.gc.fillRectangle(xText, yText, textBounds.x, textBounds.y);
				}
				e.gc.drawString(key.getDisplay(), xText, yText);
				if ((key.getOptionState() & 8) == 8 && !key.isSymbol()) {
					e.gc.setForeground(Constants.WHITE);
					// e.gc.setLineWidth(2);
					e.gc.drawRectangle(xText - 1, yText - 1, textBounds.x + 1, textBounds.y + 1);
				}
			}
			if (mouseIn) {
				e.gc.setForeground(Constants.BRIGHT_ORANGE);
				e.gc.setLineWidth(3);
				e.gc.drawRoundRectangle(3, 3, calculatedSize - 6, SIZE-6, 3, 3);
			}
		}
	}

	public Point getDimension() {
		return new Point(calculatedSize, SIZE);
	}

	private Color getDefaultBackgroundColor() {
		return key.getType().equals("COLOR") && key.getIndex() != null ? colorList.get(key.getIndex()).getColor()
				: Constants.DARK_GREY;
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
