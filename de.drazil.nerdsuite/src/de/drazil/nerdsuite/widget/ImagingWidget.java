package de.drazil.nerdsuite.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.log.Console;
import lombok.AllArgsConstructor;
import lombok.Data;

public class ImagingWidget extends Canvas implements IDrawListener, PaintListener {
	private final static int MOUSE_BUTTON_LEFT = 1;
	private final static int MOUSE_BUTTON_MIDDLE = 2;
	private final static int MOUSE_BUTTON_RIGHT = 3;
	protected final static int DRAW_NOTHING = 0;
	protected final static int SET_DRAW_ALL_TILES = 1;
	protected final static int SET_DRAW_TILE = 2;
	protected final static int SET_DRAW_PIXEL = 4;
	protected final static int LEFT_BUTTON_PRESSED = 1;
	protected final static int LEFT_BUTTON_RELEASED = 2;
	protected final static int RIGHT_BUTTON_PRESSED = 4;
	protected final static int RIGHT_BUTTON_RELEASED = 8;

	protected int width = 8;
	protected int currentWidth = 0;
	protected int height = 8;
	protected int tileColumns = 1;
	protected int tileRows = 1;
	protected int columns = 1;
	protected int rows = 1;
	protected int pixelSize = 15;
	protected int layerCount = 0;
	protected int activeLayer = 0;
	protected int maxLayerCount = 4;
	protected int currentPixelWidth;
	protected int currentPixelHeight;
	protected int selectedTileIndexX = 0;
	protected int selectedTileIndexY = 0;
	protected int bytesPerRow;
	protected int cursorX = 0;
	protected int cursorY = 0;
	protected int tileX = 0;
	protected int tileY = 0;
	protected int tileCursorX = 0;
	protected int tileCursorY = 0;
	protected int selectedColorIndex;
	protected int monoColorDefaultIndex;
	protected int colorCount;
	protected int selectedTileOffset = 0;
	protected int cursorLineWidth = 1;
	protected int leftButtonMode = 0;
	protected int rightButtonMode = 0;
	public int drawMode = DRAW_NOTHING;

	protected byte byteArray[];

	protected boolean animationIsRunning = false;
	protected boolean pixelGridEnabled = true;
	protected boolean tileGridEnabled = true;
	protected boolean tileSubGridEnabled = true;
	protected boolean multiColorEnabled = true;
	protected boolean tileCursorEnabled = false;
	protected boolean separatorEnabled = true;
	protected boolean layerViewEnabled = false;
	protected boolean paintMode = true;
	protected boolean mouseIn = false;

	protected GridStyle gridStyle = GridStyle.LINE;
	protected Map<String, Color> palette;
	protected IColorProvider colorProvider;
	protected ScrollBar hBar = null;
	protected ScrollBar vBar = null;
	protected List<IDrawListener> drawListenerList = null;
	protected List<TileRange> tileRangeList = null;
	protected List<TileLocation> tileLocationList = null;
	protected String widgetName = "<unknown>";

	protected WidgetMode widgetMode;

	private Timer animationTimer;

	public enum WidgetMode {
		SELECTOR, PAINTER
	};

	public enum GridStyle {
		PIXEL, LINE
	};

	@Data
	@AllArgsConstructor
	private class TileRange {
		private int x1;
		private int y1;
		private int x2;
		private int y2;
	}

	@Data
	@AllArgsConstructor
	private class TileLocation {
		private int x;
		private int y;
	}

	private int animationIndexX;
	private int animationIndexY;

	public class Animator extends TimerTask {
		public void run() {
			animate();
		}
	}

	public ImagingWidget(Composite parent, int style) {
		super(parent, style);
		setTileColumns(1);
		setTileRows(1);
		setColumns(1);
		setRows(1);

		tileLocationList = new ArrayList<>();
		tileLocationList.add(new TileLocation(0, 0));
		tileLocationList.add(new TileLocation(1, 0));
		tileLocationList.add(new TileLocation(2, 0));
		tileLocationList.add(new TileLocation(3, 0));
		tileLocationList.add(new TileLocation(4, 0));
		tileLocationList.add(new TileLocation(5, 0));
		tileRangeList = new ArrayList<>();
		tileRangeList.add(new TileRange(0, 0, 4, 0));

		hBar = getHorizontalBar();
		vBar = getVerticalBar();

		setBackground(Constants.BLACK);

		drawListenerList = new ArrayList<IDrawListener>();

		addPaintListener(this);

		addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
			}

			public void keyReleased(KeyEvent e) {
				switch (e.character) {
				case '1': {
					selectedColorIndex = 0;
					break;
				}
				case '2': {
					selectedColorIndex = 1;
					break;
				}
				case '3': {
					selectedColorIndex = 2;
					break;
				}
				case '4': {
					selectedColorIndex = 3;
					break;
				}
				}
			}
		});

		addMouseTrackListener(new MouseTrackListener() {

			@Override
			public void mouseHover(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExit(MouseEvent e) {
				mouseIn = false;
				if (widgetMode == WidgetMode.PAINTER) {

				} else if (widgetMode == WidgetMode.SELECTOR) {
				}
				doDrawAllTiles();
			}

			@Override
			public void mouseEnter(MouseEvent e) {
				mouseIn = true;
				if (widgetMode == WidgetMode.PAINTER) {
				} else if (widgetMode == WidgetMode.SELECTOR) {
				}
				doDrawAllTiles();
			}
		});

		addMouseMoveListener(new MouseMoveListener() {

			@Override
			public void mouseMove(MouseEvent e) {
				setCursorPosition(e.x, e.y);
				if (widgetMode == WidgetMode.PAINTER) {
					if (leftButtonMode == LEFT_BUTTON_PRESSED) {
						doDrawPixel();
						fireDoDrawTile();
					}
				} else if (widgetMode == WidgetMode.SELECTOR) {
					doDrawAllTiles();
				}
			}
		});

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (e.button == MOUSE_BUTTON_RIGHT) {
					if (widgetMode == WidgetMode.PAINTER) {
						paintMode = !paintMode;
						Console.println("drawMode:" + paintMode);
					}
				} else if (e.button == MOUSE_BUTTON_LEFT) {
					if (leftButtonMode == LEFT_BUTTON_PRESSED) {
						leftButtonMode = 0;
						if (widgetMode == WidgetMode.SELECTOR) {
							drawMode = 0;
							selectedTileIndexX = tileX;
							selectedTileIndexY = tileY;
							selectedTileOffset = (getWidth() / 8) * getHeight() * tileColumns * tileRows
									* (selectedTileIndexX + (selectedTileIndexY * columns));
							fireSetSelectedTileOffset(selectedTileOffset);
							System.out.printf(getWidgetName() + ": Tile selected x:%3d  y:%3d %n", selectedTileIndexX,
									selectedTileIndexY);
							doDrawAllTiles();
						} else {
							doDrawPixel();
							fireDoDrawTile();
						}
					}
				}
			}

			@Override
			public void mouseDown(MouseEvent e) {
				if (e.button == MOUSE_BUTTON_LEFT) {
					setCursorPosition(e.x, e.y);
					leftButtonMode = LEFT_BUTTON_PRESSED;
					if (widgetMode == WidgetMode.PAINTER) {

					}
				}
			}
		});
	}

	protected void setCursorPosition(int x, int y) {
		cursorX = x / currentPixelWidth;
		cursorY = y / currentPixelHeight;
		tileX = x / (currentWidth * currentPixelWidth * tileColumns);
		tileY = y / (height * currentPixelHeight * tileRows);
		tileCursorX = (cursorX - (tileX * width));
		tileCursorY = (cursorY - (tileY * height));
		// System.out.printf(
		// getWidgetName()
		// + ": mx:%3d my:%3d | px:%3d py:%3d | tx:%3d ty:%3d | tcx:%3d tcy:%3d
		// |pixwidth: %3d \n",
		// x, y, cursorX, cursorY, tileX, tileY, tileCursorX, tileCursorY,
		// currentPixelWidth);
	}

	public void setWidgetName(String widgetName) {
		this.widgetName = widgetName;
	}

	public String getWidgetName() {
		return widgetName;
	}

	public void paintControl(PaintEvent e) {

		if ((drawMode & SET_DRAW_ALL_TILES) == SET_DRAW_ALL_TILES) {
			paintControlTiles(e.gc);
		}
		if ((drawMode & SET_DRAW_TILE) == SET_DRAW_TILE) {
			paintControlTile(e.gc, selectedTileIndexX, selectedTileIndexY);
		}
		if (drawMode == SET_DRAW_PIXEL) {
			paintControlPixel(e.gc, cursorX, cursorY);
		}
		if (isPixelGridEnabled()) {
			paintControlPixelGrid(e.gc);
		}
		if (isSeparatorEnabled()) {
			paintControlSeparator(e.gc);
		}
		if (isTileGridEnabled()) {
			paintControlTileGrid(e.gc);
		}
		if (isTileSubGridEnabled()) {
			paintControlTileSubGrid(e.gc);
		}

		if (isTileCursorEnabled()) {
			paintControlTileCursor(e.gc, mouseIn, !animationIsRunning ? selectedTileIndexX : animationIndexX,
					!animationIsRunning ? selectedTileIndexY : animationIndexY);
		}

		drawMode = DRAW_NOTHING;

	}

	public void paintControlTileCursor(GC gc, boolean mouseIn, int x, int y) {
		gc.setLineWidth(cursorLineWidth);
		gc.setLineStyle(SWT.LINE_SOLID);

		gc.setForeground(Constants.TILE_SUB_GRID_COLOR);
		gc.drawRectangle(x * width * pixelSize * tileColumns, y * height * pixelSize * tileRows,
				width * pixelSize * tileColumns, height * pixelSize * tileRows);

		if (mouseIn) {
			gc.setForeground((tileX == x && tileY == y) ? Constants.LIGHT_RED : Constants.LIGHT_RED);
			gc.drawRectangle(tileX * width * pixelSize * tileColumns, tileY * height * pixelSize * tileRows,
					width * pixelSize * tileColumns, height * pixelSize * tileRows);
		}
	}

	public void paintControlPixelGrid(GC gc) {
		for (int x = 0; x <= currentWidth * tileColumns; x++) {
			for (int y = 0; y <= height * tileRows; y++) {
				gc.setForeground(Constants.PIXEL_GRID_COLOR);
				if (gridStyle == GridStyle.LINE) {
					gc.drawLine(x * currentPixelWidth, 0, x * currentPixelWidth,
							height * currentPixelHeight * tileRows);
					gc.drawLine(0, y * pixelSize, width * pixelSize * tileColumns, y * pixelSize);
				} else {
					gc.drawPoint(x * currentPixelWidth, y * currentPixelHeight);
				}
			}
		}
	}

	public void paintControlSeparator(GC gc) {
		gc.setForeground(Constants.BYTE_SEPARATOR_COLOR);
		int step = (4 * (isMultiColorEnabled() ? 1 : 2));
		for (int x = step; x < (width * tileColumns) / ((isMultiColorEnabled() ? 2 : 1)); x += step) {
			gc.drawLine(x * currentPixelWidth, 0, x * currentPixelWidth, height * tileRows * pixelSize);
		}
	}

	public void paintControlTileGrid(GC gc) {
		gc.setForeground(Constants.TILE_SUB_GRID_COLOR);
		for (int y = height; y < height * tileRows; y += height) {
			gc.drawLine(0, y * pixelSize, width * tileColumns * pixelSize, y * pixelSize);
		}
		gc.setForeground(Constants.TILE_SUB_GRID_COLOR);
		for (int x = currentWidth; x < currentWidth * tileColumns; x += currentWidth) {
			gc.drawLine(x * currentPixelWidth, 0, x * currentPixelWidth, height * tileRows * pixelSize);
		}
	}

	public void paintControlTileSubGrid(GC gc) {
		gc.setLineWidth(1);
		gc.setLineStyle(SWT.LINE_SOLID);
		gc.setForeground(Constants.TILE_GRID_COLOR);
		for (int x = 0; x < columns; x++) {
			for (int y = 0; y < columns; y++) {
				gc.drawRectangle(x * width * pixelSize * tileColumns, y * height * pixelSize * tileRows,
						width * pixelSize * tileColumns, height * pixelSize * tileRows);
			}
		}
	}

	private void paintControlTiles(GC gc) {

		for (int tx = 0; tx < columns; tx++) {
			for (int ty = 0; ty < rows; ty++) {
				paintControlTile(gc, tx, ty);
			}
		}
	}

	private void paintControlTile(GC gc, int tx, int ty) {
		int x = 0;
		int y = 0;
		int b1 = bytesPerRow * height;
		int b2 = b1 * tileColumns;

		int byteOffset = 0;
		if (widgetMode == WidgetMode.PAINTER) {
			byteOffset = selectedTileOffset;
		} else if (widgetMode == WidgetMode.SELECTOR) {
			byteOffset = (getWidth() / 8) * getHeight() * tileColumns * tileRows * (tx + (ty * columns));
		}

		for (int i = byteOffset, k = 0; i < (byteOffset + getViewportSize()); i++, k++) {
			int b = (byteArray[i] & 0xff);
			int xi = (k % bytesPerRow) * (8 / (isMultiColorEnabled() ? 2 : 1));
			int xo = (k / b1) % tileColumns;
			x = xi + (xo * currentWidth) + (tx * currentWidth * tileColumns);

			int yi = (k / bytesPerRow) % height;
			int yo = (k / b2) % tileRows;
			y = yi + (yo * height) + (ty * height * tileRows);
			int colorMapIndex = k / currentWidth;

			if (isMultiColorEnabled()) {
				for (int j = 6; j >= 0; j -= 2) {
					int bi = b;
					int colorIndex = (bi >> j) & 3;

					Color color = palette.get(String.valueOf(colorIndex));
					if (colorProvider != null) {
						color = colorProvider.getColorByIndex((byte) colorIndex, byteArray, byteOffset, colorMapIndex);
					}
					gc.setBackground(color);
					int pix = isPixelGridEnabled() ? 1 : 0;
					gc.fillRectangle((x * currentPixelWidth) + pix, (y * currentPixelHeight) + pix,
							currentPixelWidth - pix, currentPixelHeight - pix);
					x++;
				}
			} else {
				for (int j = 128; j > 0; j >>= 1) {
					gc.setBackground((b & j) == j ? palette.get(String.valueOf(selectedColorIndex))
							: Constants.BITMAP_BACKGROUND_COLOR);
					int pix = isPixelGridEnabled() ? 1 : 0;

					gc.fillRectangle((x * currentPixelWidth) + pix, (y * currentPixelHeight) + pix,
							currentPixelWidth - pix, currentPixelHeight - pix);
					x++;
				}
			}
		}
	}

	private void paintControlPixel(GC gc, int x, int y) {

		// System.out.println(getWidgetName() + ":drawPixel x:" + x + " y:" +
		// y);
		if (widgetMode == WidgetMode.PAINTER) {
			if (x < currentWidth * tileColumns && y < height * tileRows) {
				int ix = x % currentWidth;
				int iy = y % height;
				int ax = (x / currentWidth);
				int ay = (y / height) * tileColumns;
				int offset = (ax + ay) * (height * bytesPerRow);
				if (isMultiColorEnabled()) {
					int index = (((iy * currentWidth) + ix) >> 2) + offset;
					ix &= 3;
					int mask = (3 << ((3 - ix) * 2) ^ 0xff) & 0xff;
					byte byteMask = (byte) ((byteArray[index + getSelectedTileOffset()] & mask));
					byteMask |= selectedColorIndex << ((3 - ix) * 2);
					byteArray[index + getSelectedTileOffset()] = byteMask;

				} else {
					int index = (((iy * currentWidth) + ix) >> 3) + offset;
					byte byteMask = byteArray[index + getSelectedTileOffset()];
					int pixelMask = (1 << (7 - (ix % 8)) & 0xff);
					byteArray[index + getSelectedTileOffset()] = paintMode ? (byte) (byteMask | pixelMask)
							: (byte) (byteMask & ((pixelMask ^ 0xff) & 0xff));
				}
			}
		}

		gc.setBackground(
				paintMode ? palette.get(String.valueOf(selectedColorIndex)) : Constants.BITMAP_BACKGROUND_COLOR);

		int pix = isPixelGridEnabled() ? 1 : 0;
		gc.fillRectangle((x * currentPixelWidth) + pix, (y * currentPixelHeight) + pix, currentPixelWidth - pix,
				currentPixelHeight - pix);

	}

	public void setColorProvider(IColorProvider colorProvider) {
		this.colorProvider = colorProvider;
	}

	public void setContent(byte byteArray[]) {
		this.byteArray = byteArray;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;

	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;

	}

	public int getColumns() {
		return columns;
	}

	public void setColumns(int columns) {
		this.columns = columns;

	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;

	}

	public int getTileColumns() {
		return tileColumns;
	}

	public void setTileColumns(int tileColumns) {
		this.tileColumns = tileColumns;

	}

	public void setCursorLineWidth(int cursorLineWidth) {
		this.cursorLineWidth = cursorLineWidth;
	}

	public int getCursorLineWidth() {
		return cursorLineWidth;
	}

	public int getTileRows() {
		return tileRows;
	}

	public void setTileRows(int tileRows) {
		this.tileRows = tileRows;

	}

	public int getViewportSize() {
		int viewPortSize = (getWidth() / 8) * getHeight() * tileColumns * tileRows;
		return viewPortSize;
	}

	public int getPixelSize() {
		return pixelSize;
	}

	public void setPixelSize(int pixelSize) {
		this.pixelSize = pixelSize;
		this.currentPixelWidth = pixelSize;
		this.currentPixelHeight = pixelSize;

	}

	public boolean isPixelGridEnabled() {
		return pixelGridEnabled;
	}

	public void setPixelGridEnabled(boolean pixelGridEnabled) {
		this.pixelGridEnabled = pixelGridEnabled;

	}

	public boolean isTileSubGridEnabled() {
		return tileSubGridEnabled;
	}

	public void setTileSubGridEnabled(boolean tileSubGridEnabled) {
		this.tileSubGridEnabled = tileSubGridEnabled;

	}

	public boolean isTileGridEnabled() {
		return tileGridEnabled;
	}

	public void setTileGridEnabled(boolean tileGridEnabled) {
		this.tileGridEnabled = tileGridEnabled;

	}

	public boolean isSeparatorEnabled() {
		return separatorEnabled;
	}

	public void setSeparatorEnabled(boolean separatorEnabled) {
		this.separatorEnabled = separatorEnabled;

	}

	public boolean isTileCursorEnabled() {
		return tileCursorEnabled;
	}

	public void setTileCursorEnabled(boolean tileCursorEnabled) {
		this.tileCursorEnabled = tileCursorEnabled;

	}

	public boolean isMultiColorEnabled() {
		return multiColorEnabled;
	}

	public void setMultiColorEnabled(boolean multiColorEnabled) {
		this.multiColorEnabled = multiColorEnabled;
	}

	public void recalc() {
		currentPixelWidth = getPixelSize() * (isMultiColorEnabled() ? 2 : 1);
		currentWidth = getWidth() / (isMultiColorEnabled() ? 2 : 1);
		bytesPerRow = width >> 3;
		doDrawAllTiles();
	}

	public void setGridStyle(GridStyle gridStyle) {
		this.gridStyle = gridStyle;
	}

	public void setColor(int index, Color color) {
		if (palette == null) {
			palette = new HashMap<String, Color>();
		}
		palette.put(String.valueOf(index), color);
		redraw();
	}

	public void setSelectedColor(int index) {
		selectedColorIndex = index;
		monoColorDefaultIndex = index;
	}

	public void addLayer() {
		layerCount += (layerCount < maxLayerCount ? 1 : 0);
	}

	public void removeLayer() {
		layerCount -= (layerCount > 0 ? 1 : 0);
	}

	public void setActiveLayer(int activeLayer) {

		this.activeLayer = activeLayer < layerCount ? activeLayer : layerCount;
	}

	public int getActiveLayer() {
		return activeLayer;
	}

	public void setLayerViewEnabled(boolean layerViewEnabled) {
		this.layerViewEnabled = layerViewEnabled;
	}

	public boolean isLayerViewEnabled() {
		return layerCount > 0 && layerViewEnabled;
	}

	@Override
	public void setSelectedTileOffset(int offset) {
		this.selectedTileOffset = offset;
		System.out.println(getWidgetName() + ":offset=" + offset);
		doDrawAllTiles();
	}

	public int getSelectedTileOffset() {
		return this.selectedTileOffset;
	}

	public void addDrawListener(IDrawListener redrawListener) {
		drawListenerList.add(redrawListener);
	}

	public void removeDrawListener(IDrawListener redrawListener) {
		drawListenerList.remove(redrawListener);
	}

	private void fireDoDrawTile() {
		for (IDrawListener listener : drawListenerList) {
			listener.doDrawTile();
		}
	}

	private void fireDoDrawAllTiles() {
		for (IDrawListener listener : drawListenerList) {
			listener.doDrawAllTiles();
		}
	}

	private void fireSetSelectedTileOffset(int offset) {
		for (IDrawListener listener : drawListenerList) {
			listener.setSelectedTileOffset(offset);
		}
	}

	private void fireDoDrawPixel(int x, int y, boolean paintMode) {
		for (IDrawListener listener : drawListenerList) {
			listener.doDrawPixel(x, y, paintMode);
		}
	}

	private void setDrawMode(int drawMode) {
		this.drawMode = drawMode;
	}

	private int getDrawMode() {
		return drawMode;
	}

	@Override
	public void doDrawPixel(int x, int y, boolean paintMode) {
		this.paintMode = paintMode;
		cursorX = x + (selectedTileIndexX * width * tileColumns);
		cursorY = y + (selectedTileIndexY * height * tileRows);
		doDrawPixel();
	}

	protected void doDrawPixel() {
		drawMode = SET_DRAW_PIXEL;
		System.out.println(getWidgetName() + ":   x:" + cursorX + "    y:" + cursorY);
		int inset = isPixelGridEnabled() ? 1 : 0;
		redraw((cursorX * currentPixelWidth) + inset, (cursorY * currentPixelHeight) + inset, currentPixelWidth - inset,
				currentPixelHeight - inset, true);
	}

	@Override
	public void doDrawTile() {
		drawMode = SET_DRAW_TILE;
		redraw(selectedTileIndexX * width * pixelSize * tileColumns, selectedTileIndexY * height * pixelSize * tileRows,
				width * pixelSize * tileColumns, height * pixelSize * tileRows, true);
	}

	@Override
	public void doDrawAllTiles() {
		drawMode = SET_DRAW_ALL_TILES;
		redraw();
	}

	public void startAnimation() {
		initTimer();
		animationIsRunning = true;
		animationTimer.scheduleAtFixedRate(new Animator(), 0, 200);
	}

	public void stopAnimation() {
		animationIsRunning = false;
		animationTimer.cancel();
		animationTimer.purge();
		initTimer();
	}

	private void initTimer() {
		animationTimer = new Timer();
	}

	public void animate() {
		if (!getDisplay().isDisposed()) {
			getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					Collections.rotate(tileLocationList, -1);
					TileLocation tl = tileLocationList.get(0);
					animationIndexX = tl.x;
					animationIndexY = tl.y;
					selectedTileOffset = (getWidth() / 8) * getHeight() * tileColumns * tileRows
							* (animationIndexX + (animationIndexY * columns));
					fireSetSelectedTileOffset(selectedTileOffset);
					doDrawAllTiles();
				}
			});
		}
	}

	public void setWidgetMode(WidgetMode widgetMode) {
		this.widgetMode = widgetMode;
	}

	public WidgetMode getWidgetMode() {
		return widgetMode;
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point((currentWidth * currentPixelWidth * tileColumns * columns) + (cursorLineWidth * (columns + 1)),
				(height * currentPixelHeight * tileRows * rows) + (cursorLineWidth * (rows + 1)));
	}

}