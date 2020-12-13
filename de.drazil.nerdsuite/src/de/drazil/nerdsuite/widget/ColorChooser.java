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

	private static final int WIDGET_WIDTH = 180;
	private static final int COLOR_TILE_SIZE = 30;
	private static final int COLOR_OFFSET = WIDGET_WIDTH - COLOR_TILE_SIZE;
	private int maxColors;
	private int maxColorsTemp;
	private boolean isMulticolorEnabled;
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
			e.gc.setAlpha(255);
			e.gc.setBackground(Constants.DARK_GREY);
			e.gc.fillRectangle(0, y * COLOR_TILE_SIZE, COLOR_OFFSET, COLOR_TILE_SIZE);
			e.gc.setForeground(Constants.WHITE);
			e.gc.drawString(colorNames[y], 5, y * COLOR_TILE_SIZE + 10);
			e.gc.drawString(":" + platformColorList.get(platformColorIndexList[y]).getName(), 80, y * COLOR_TILE_SIZE + 10);
			e.gc.setBackground(platformColorList.get(platformColorIndexList[y]).getColor());
			e.gc.fillRectangle(COLOR_OFFSET, y * COLOR_TILE_SIZE, COLOR_TILE_SIZE, COLOR_TILE_SIZE);
			if (y < maxColors - 1) {
				e.gc.setForeground(Constants.BLACK);
				e.gc.drawLine(0, y * COLOR_TILE_SIZE + COLOR_TILE_SIZE, WIDGET_WIDTH, y * COLOR_TILE_SIZE + COLOR_TILE_SIZE);
			}

			if (y >= maxColorsTemp) {
				e.gc.setAlpha(170);
				e.gc.setBackground(Constants.WHITE);
				e.gc.fillRectangle(0, y * COLOR_TILE_SIZE, WIDGET_WIDTH, COLOR_TILE_SIZE);
			}
		}
		e.gc.setAlpha(255);
		e.gc.setForeground(Constants.BRIGHT_ORANGE);
		e.gc.drawRectangle(1, 1 + colorNo * COLOR_TILE_SIZE, WIDGET_WIDTH - 2, COLOR_TILE_SIZE - 2);
	}

	public void setMulticolorEnabled(boolean multicolorEnabled) {
		isMulticolorEnabled = multicolorEnabled;
		maxColorsTemp = !isMulticolorEnabled ? 2 : maxColors;
		redraw();
	}

	public void addColorSelectionListener(IColorSelectionListener listener) {
		colorSelectionListener.add(listener);
	}

	public void removeColorSelectionListener(IColorSelectionListener listener) {
		colorSelectionListener.remove(listener);
	}

	private void fireColorSelected(int colorIndex) {
		if (colorSelectionListener != null) {
			colorSelectionListener.forEach(l -> l.colorSelected(colorNo, colorIndex));
		}
	}

	@Override
	public void colorSelected(int colorNo, int colorIndex) {
		platformColorIndexList[this.colorNo] = colorIndex;
		fireColorSelected(colorIndex);
		redraw();
	}

	@Override
	protected void leftMouseButtonClickedInternal(int modifierMask, int x, int y) {
		computeCursorPosition(x, y);
		closePupup();
		if (colorNo < maxColorsTemp) {
			fireColorSelected(platformColorIndexList[colorNo]);
			redraw();
		}
	}

	@Override
	protected void rightMouseButtonClickedInternal(int modifierMask, int x, int y) {
		computeCursorPosition(x, y);
		closePupup();
		if (colorNo < maxColorsTemp) {
			colorChooser = new ColorPaletteChooser(getParent(), SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED, platformColorList);
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
