package de.drazil.nerdsuite.widget;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.model.PlatformColor;

public class ColorChooser extends BaseWidget implements PaintListener {

	private int columns;
	private int rows;
	private int width;
	private int height;
	private int cx;
	private int cy;
	private int colorIndex;
	private int maxColors;
	private List<PlatformColor> platformColorList;
	private List<IColorSelectionListener> colorSelectionListener;

	private static final int COLOR_TILE_SIZE = 30;

	public ColorChooser(Composite parent, int style, List<PlatformColor> platformColorList) {
		super(parent, style);
		setPlatformColors(platformColorList);
		colorSelectionListener = new ArrayList<IColorSelectionListener>();
		addPaintListener(this);
	}

	public void setPlatformColors(List<PlatformColor> platformColorList) {
		this.platformColorList = platformColorList;
		columns = platformColorList.size() / 4;
		rows = (platformColorList.size() / columns);
		width = columns * COLOR_TILE_SIZE;
		height = rows * COLOR_TILE_SIZE;
		setSize(width, height + 20);
		maxColors = platformColorList.size() - 1;
	}

	public void setSelectedColor(int index) {
		cx = index % columns;
		cy = index / columns;
		colorIndex = index;
		redraw();
	}

	@Override
	public void paintControl(PaintEvent e) {
		e.gc.setLineWidth(2);
		for (int r = 0; r < columns; r++) {
			for (int c = 0; c < rows; c++) {
				e.gc.setBackground(platformColorList.get(r * columns + c).getColor());
				e.gc.fillRectangle(c * COLOR_TILE_SIZE, r * COLOR_TILE_SIZE, COLOR_TILE_SIZE, COLOR_TILE_SIZE);
				if (c == cx && r == cy) {
					e.gc.setForeground(Constants.BRIGHT_ORANGE);
					e.gc.drawRectangle(1 + cx * COLOR_TILE_SIZE, 1 + cy * COLOR_TILE_SIZE, COLOR_TILE_SIZE - 2,
							COLOR_TILE_SIZE - 2);
				}
			}
		}
		e.gc.setBackground(Constants.DARK_GREY);
		e.gc.fillRectangle(0, height, width, 20);
		e.gc.setForeground(Constants.WHITE);
		e.gc.drawString(String.format("%02X : %s", colorIndex, platformColorList.get(colorIndex).getName()), 5,
				height + 3);
	}

	@Override
	public void leftMouseButtonClicked(int modifierMask, int x, int y) {
		computeCursorPosition(x, y);
		Object o = getParent().getParent();
		if (o instanceof Shell) {
			((Shell) o).close();
		}
		fireColorSelected(colorIndex);
		colorSelectionListener.clear();
	}

	@Override
	public void mouseMove(int modifierMask, int x, int y) {
		computeCursorPosition(x, y);
		redraw();
	}

	public void addColorSelectionListener(IColorSelectionListener listener) {
		colorSelectionListener.add(listener);
	}

	public void removeColorSelectionListener(IColorSelectionListener listener) {
		colorSelectionListener.remove(listener);
	}

	private void fireColorSelected(int colorIndex) {
		colorSelectionListener.forEach(l -> l.colorSelected(-1, colorIndex));
	}

	private void computeCursorPosition(int x, int y) {
		int icx = x / COLOR_TILE_SIZE;
		int icy = y / COLOR_TILE_SIZE;
		int idx = (icx + (icy * columns));
		if (idx <= maxColors) {
			colorIndex = idx;
			cx = icx;
			cy = icy;
		}
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return getSize();
	}
}