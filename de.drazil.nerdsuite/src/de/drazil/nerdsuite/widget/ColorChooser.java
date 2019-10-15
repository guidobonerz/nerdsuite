package de.drazil.nerdsuite.widget;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.model.PlatformColor;

public class ColorChooser extends BaseWidget implements PaintListener, IColorSelectionListener {

	private static final int WIDGET_WIDTH = 150;
	private static final int COLOR_TILE_SIZE = 30;
	private static final int COLOR_OFFSET = WIDGET_WIDTH - COLOR_TILE_SIZE;
	private int maxColors;
	private int maxColorsTemp;
	private boolean isMonochrom;
	private int colorNo;
	private List<PlatformColor> platformColorList;
	private int[] platformColorIndexList;
	private ColorPaletteChooser colorChooser;
	private CustomPopupDialog popupDialog;
	private List<IColorSelectionListener> colorSelectionListener;
	private String[] colorNames = new String[] { "Background", "Color", "Color 2", "Color 3" };

	public ColorChooser(Composite parent, int style, int maxColors, List<PlatformColor> platformColorList) {
		super(parent, style);
		this.maxColors = maxColors;
		this.maxColorsTemp = maxColors;
		this.platformColorList = platformColorList;
		this.colorSelectionListener = new ArrayList<IColorSelectionListener>();
		this.platformColorIndexList = new int[maxColors];
		for (int i = 0; i < maxColors; i++) {
			platformColorIndexList[i] = i;
		}
		addPaintListener(this);
	}

	@Override
	public void paintControl(PaintEvent e) {
		e.gc.setLineWidth(3);
		for (int y = 0; y < maxColors; y++) {
			e.gc.setBackground(Constants.DARK_GREY);
			e.gc.fillRectangle(0, y * COLOR_TILE_SIZE, COLOR_OFFSET, COLOR_TILE_SIZE);
			e.gc.setForeground(Constants.WHITE);
			String s = String.format("%2s : %s", colorNames[y], platformColorList.get(y).getName());
			e.gc.drawString(s, 5, y * COLOR_TILE_SIZE + 10);
			// e.gc.setAlpha(y > maxColorsTemp ? 100 : 255);
			e.gc.setBackground(platformColorList.get(platformColorIndexList[y]).getColor());
			e.gc.fillRectangle(COLOR_OFFSET, y * COLOR_TILE_SIZE, COLOR_TILE_SIZE, COLOR_TILE_SIZE);
			if (y >= maxColorsTemp) {
				e.gc.setForeground(Constants.RED);
				e.gc.drawLine(COLOR_OFFSET, y * COLOR_TILE_SIZE, COLOR_OFFSET + COLOR_TILE_SIZE,
						(y + 1) * COLOR_TILE_SIZE);
				e.gc.drawLine(COLOR_OFFSET, (y + 1) * COLOR_TILE_SIZE, COLOR_OFFSET + COLOR_TILE_SIZE,
						y * COLOR_TILE_SIZE);
			}
		}
		// e.gc.setAlpha(255);
		// e.gc.setForeground(Constants.BLACK);
		// e.gc.drawRectangle(1, 2, WIDGET_WIDTH - 2, COLOR_TILE_SIZE * maxColors - 3);
		e.gc.setForeground(Constants.BRIGHT_ORANGE);
		e.gc.drawRectangle(1, 1 + colorNo * COLOR_TILE_SIZE, WIDGET_WIDTH - 2, COLOR_TILE_SIZE - 2);
	}

	public void setMonochrom(boolean monochrom) {
		isMonochrom = monochrom;
		maxColorsTemp = isMonochrom ? 2 : maxColors;
		redraw();
	}

	public void addColorSelectionListener(IColorSelectionListener listener) {
		colorSelectionListener.add(listener);
	}

	public void removeColorSelectionListener(IColorSelectionListener listener) {
		colorSelectionListener.remove(listener);
	}

	private void fireColorSelected(int colorIndex) {
		colorSelectionListener.forEach(l -> l.colorSelected(colorNo, colorIndex));
	}

	@Override
	public void colorSelected(int colorNo, int colorIndex) {
		platformColorIndexList[this.colorNo] = colorIndex;
		fireColorSelected(colorIndex);
		redraw();
	}

	@Override
	public void leftMouseButtonClicked(int modifierMask, int x, int y) {
		computeCursorPosition(x, y);
		closePupup();
		if (colorNo < maxColorsTemp) {
			fireColorSelected(platformColorIndexList[colorNo]);
			redraw();
		}
	}

	@Override
	public void rightMouseButtonClicked(int modifierMask, int x, int y) {
		computeCursorPosition(x, y);
		closePupup();
		if (colorNo < maxColorsTemp) {
			colorChooser = new ColorPaletteChooser(getParent(), SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED,
					platformColorList);
			colorChooser.setSelectedColor(platformColorIndexList[colorNo]);
			colorChooser.addColorSelectionListener(this);
			popupDialog = new CustomPopupDialog(getParent().getShell(), colorChooser);
			popupDialog.open();

		}
	}

	private void closePupup() {
		if (colorChooser != null) {
			colorChooser.removeColorSelectionListener(this);
			popupDialog.close();
		}
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point(100, COLOR_TILE_SIZE * maxColors);
	}

	private void computeCursorPosition(int x, int y) {
		colorNo = y / COLOR_TILE_SIZE;
		if (colorNo > maxColorsTemp) {
			colorNo = maxColorsTemp;
		}
	}
}
