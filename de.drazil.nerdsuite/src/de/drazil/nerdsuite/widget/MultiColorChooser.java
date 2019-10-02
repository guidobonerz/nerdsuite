package de.drazil.nerdsuite.widget;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.model.PlatformColor;

public class MultiColorChooser extends BaseWidget implements PaintListener, IColorSelectionListener {

	private static final int COLOR_TILE_SIZE = 30;
	private int maxColors;
	private boolean isMonochrom;
	private int colorIndex;
	private boolean allowMouseMove = false;
	private List<PlatformColor> platformColorList;
	private int[] platformColorIndexList;
	private ColorChooser colorChooser;
	private CustomPopupDialog popupDialog;

	public MultiColorChooser(Composite parent, int style, int maxColors, List<PlatformColor> platformColorList) {
		super(parent, style);

		this.maxColors = maxColors;
		this.platformColorList = platformColorList;
		this.platformColorIndexList = new int[maxColors];
		for (int i = 0; i < maxColors; i++) {
			platformColorIndexList[i] = i;
		}
		addPaintListener(this);

	}

	@Override
	public void paintControl(PaintEvent e) {
		e.gc.setLineWidth(2);
		for (int y = 0; y < maxColors; y++) {
			e.gc.setBackground(platformColorList.get(platformColorIndexList[y]).getColor());
			e.gc.fillRectangle(0, y * COLOR_TILE_SIZE, COLOR_TILE_SIZE, COLOR_TILE_SIZE);
		}
		e.gc.setForeground(Constants.DARK_GREY);
		e.gc.drawRectangle(1, 2, COLOR_TILE_SIZE - 2, COLOR_TILE_SIZE * maxColors - 3);

		e.gc.setForeground(Constants.BRIGHT_ORANGE);
		e.gc.drawRectangle(1, 1 + colorIndex * COLOR_TILE_SIZE, COLOR_TILE_SIZE - 2, COLOR_TILE_SIZE - 2);

	}

	public void setMonochrom(boolean monochrom) {
		isMonochrom = monochrom;
	}

	@Override
	public void colorSelected(int colorIndex) {
		platformColorIndexList[this.colorIndex] = colorIndex;
		allowMouseMove = true;
		redraw();
	}

	@Override
	public void leftMouseButtonClicked(int modifierMask, int x, int y) {
		computeCursorPosition(x, y);
		redraw();
	}

	@Override
	public void rightMouseButtonClicked(int modifierMask, int x, int y) {
		allowMouseMove = false;
		computeCursorPosition(x, y);
		colorChooser = new ColorChooser(getParent(), SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED, platformColorList);
		colorChooser.addColorSelectionListener(this);
		popupDialog = new CustomPopupDialog(getParent().getShell(), colorChooser);
		popupDialog.open();

	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point(COLOR_TILE_SIZE, COLOR_TILE_SIZE * maxColors);
	}

	private void computeCursorPosition(int x, int y) {
		colorIndex = y / COLOR_TILE_SIZE;
	}

	/*
	 * 
	 * 
	 * CustomPopupDialog pd = new CustomPopupDialog(parent.getShell()) {
	 * 
	 * @Override protected Composite getContent(Composite parent) { ColorChooser cc1
	 * = new ColorChooser(parent, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED, 0,
	 * PlatformFactory.getPlatformColors(project.getTargetPlatform())); return cc1;
	 * }
	 * 
	 * };
	 */
}
