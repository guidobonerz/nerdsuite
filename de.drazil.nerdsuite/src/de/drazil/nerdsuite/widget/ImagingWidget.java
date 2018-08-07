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
	protected int visibleRows = 0;
	protected int visibleColumns = 0;
	protected int tileCursorX = 0;
	protected int tileCursorY = 0;
	protected int selectedColorIndex;
	protected int monoColorDefaultIndex;
	protected int colorCount;
	protected int selectedTileOffset = 0;
	protected int cursorLineWidth = 1;
	protected int leftButtonMode = 0;
	protected int rightButtonMode = 0;
	private int paintControlMode = DRAW_NOTHING;
	private PaintMode drawMode = PaintMode.Pixel;

	protected byte bitplane[];
	protected byte clipboardBuffer[];
	private int cutCopyOffset;
	private int pasteOffset;
	private ClipboardAction clipboardAction = ClipboardAction.Off;

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
		SELECTOR, PAINTER, VIEWER
	};

	public enum GridStyle {
		PIXEL, LINE
	};

	public enum Direction {
		UP, DOWN, LEFT, RIGHT
	};

	public enum Orientation {
		HORIZONTAL, VERTICAL
	};

	public enum Rotate {
		CW, CCW
	};

	public enum ClipboardAction {
		Cut, Copy, Paste, Off
	};

	public enum PaintMode {
		Pixel, VerticalMirror, HorizontalMirror, Kaleidoscope
	}

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

	private List<TileLocation> swapRingBuffer = null;

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

		swapRingBuffer = new ArrayList<>();
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
					} else {
						doDrawAllTiles();
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
							paintControlMode = 0;
							selectedTileIndexX = tileX;
							selectedTileIndexY = tileY;
							selectedTileOffset = (getWidth() / 8) * getHeight() * tileColumns * tileRows
									* (selectedTileIndexX + (selectedTileIndexY * columns));
							fireSetSelectedTileOffset(selectedTileOffset);
							/*
							 * System.out.printf(getWidgetName() +
							 * ": Tile selected x:%3d  y:%3d %n",
							 * selectedTileIndexX, selectedTileIndexY);
							 */
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
		/*
		 * System.out.printf( getWidgetName() +
		 * ": mx:%3d my:%3d | px:%3d py:%3d | tx:%3d ty:%3d | tcx:%3d tcy:%3d |pixwidth: %3d \n"
		 * , x, y, cursorX, cursorY, tileX, tileY, tileCursorX, tileCursorY,
		 * currentPixelWidth);
		 */
	}

	public void paintControl(PaintEvent e) {

		if ((paintControlMode & SET_DRAW_ALL_TILES) == SET_DRAW_ALL_TILES) {
			paintControlTiles(e.gc);
		}
		if ((paintControlMode & SET_DRAW_TILE) == SET_DRAW_TILE) {
			paintControlTile(e.gc, selectedTileIndexX, selectedTileIndexY);
		}
		if (paintControlMode == SET_DRAW_PIXEL) {
			switch (drawMode) {
			case Pixel: {
				paintControlPixel(e.gc, cursorX, cursorY);
				break;
			}

			case VerticalMirror: {
				paintControlPixel(e.gc, cursorX, cursorY);
				int centerX = ((width * tileColumns) / 2);
				int diff = centerX - cursorX - 1;
				paintControlPixel(e.gc, centerX + diff, cursorY);
				break;
			}
			case HorizontalMirror: {
				paintControlPixel(e.gc, cursorX, cursorY);
				int centerY = ((height * tileRows) / 2);
				int diff = centerY - cursorY - 1;
				paintControlPixel(e.gc, cursorX, centerY + diff);
				break;
			}
			case Kaleidoscope: {
				paintControlPixel(e.gc, cursorX, cursorY);
				int centerX = ((width * tileColumns) / 2);
				int diffX = centerX - cursorX - 1;
				paintControlPixel(e.gc, centerX + diffX, cursorY);
				int centerY = ((height * tileRows) / 2);
				int diffY = centerY - cursorY - 1;
				paintControlPixel(e.gc, cursorX, centerY + diffY);
				paintControlPixel(e.gc, centerX + diffX, centerY + diffY);
				break;
			}
			}

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
		if (widgetMode == WidgetMode.PAINTER) {
			paintControlPixelGridCursor(e.gc, 0, 0);
		}

		paintControlSwapMarkers(e.gc);

		paintControlMode = DRAW_NOTHING;

	}

	public void paintControlSwapMarkers(GC gc) {
		gc.setForeground(Constants.TILE_SUB_GRID_COLOR);
		gc.setLineStyle(SWT.LINE_CUSTOM);
		gc.setLineDash(new int[] { 4, 4 });
		for (TileLocation tilelocation : swapRingBuffer) {
			gc.drawRectangle(tilelocation.x * width * pixelSize * tileColumns,
					tilelocation.y * height * pixelSize * tileRows, width * pixelSize * tileColumns,
					height * pixelSize * tileRows);
		}
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

	public void paintControlPixelGridCursor(GC gc, int x, int y) {
		System.out.println(cursorX + "   " + cursorY);
		gc.setForeground(Constants.DEFAULT_UNSTABLE_ILLEGAL_OPCODE_COLOR);
		int lineWidth = (int) (getPixelSize() * 0.1);
		gc.setLineWidth(lineWidth);
		gc.drawRectangle((cursorX * currentPixelWidth) + 1 + lineWidth / 2, (cursorY * pixelSize) + 1 + lineWidth / 2,
				currentPixelWidth - 1 - lineWidth, pixelSize - 1 - lineWidth);
	}

	public void paintControlSeparator(GC gc) {
		gc.setForeground(Constants.BYTE_SEPARATOR_COLOR);
		int step = (4 * (isMultiColorEnabled() ? 1 : 2));
		for (int x = step; x < (width * tileColumns) / ((isMultiColorEnabled() ? 2 : 1)); x += step) {
			gc.drawLine(x * currentPixelWidth, 0, x * currentPixelWidth, height * tileRows * pixelSize);
		}
	}

	public void paintControlTileSubGrid(GC gc) {
		gc.setForeground(Constants.TILE_SUB_GRID_COLOR);
		for (int y = height; y < height * tileRows; y += height) {
			gc.drawLine(0, y * pixelSize, width * tileColumns * pixelSize, y * pixelSize);
		}
		gc.setForeground(Constants.TILE_SUB_GRID_COLOR);
		for (int x = currentWidth; x < currentWidth * tileColumns; x += currentWidth) {
			gc.drawLine(x * currentPixelWidth, 0, x * currentPixelWidth, height * tileRows * pixelSize);
		}
	}

	public void paintControlTileGrid(GC gc) {
		gc.setLineWidth(1);
		gc.setLineStyle(SWT.LINE_SOLID);
		gc.setForeground(Constants.TILE_GRID_COLOR);
		for (int x = 0; x < columns; x++) {
			for (int y = 0; y < rows; y++) {
				gc.drawRectangle(x * width * pixelSize * tileColumns, y * height * pixelSize * tileRows,
						width * pixelSize * tileColumns, height * pixelSize * tileRows);
			}
		}
	}

	private void paintControlTiles(GC gc) {
		for (int ty = 0; ty < rows; ty++) {
			for (int tx = 0; tx < columns; tx++) {
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
		} else if (widgetMode == WidgetMode.SELECTOR || widgetMode == WidgetMode.VIEWER) {
			byteOffset = (getWidth() / 8) * getHeight() * tileColumns * tileRows * (tx + (ty * columns));
		}

		for (int i = byteOffset, k = 0; i < (byteOffset + getViewportSize()); i++, k++) {

			int b = (bitplane[i] & 0xff);
			int xi = (k % bytesPerRow) * (8 / (isMultiColorEnabled() ? 2 : 1));
			int xo = (k / b1) % tileColumns;
			x = xi + (xo * currentWidth) + (tx * currentWidth * tileColumns);

			int yi = (k / bytesPerRow) % height;
			int yo = (k / b2) % tileRows;
			y = yi + (yo * height) + (ty * height * tileRows);

			if (isMultiColorEnabled()) {
				for (int j = 6; j >= 0; j -= 2) {
					int bi = b;
					int colorIndex = (bi >> j) & 3;
					Color color = palette != null ? palette.get(String.valueOf(colorIndex)) : null;
					if (colorProvider != null) {
						color = colorProvider.getColorByIndex((byte) colorIndex, bitplane, tx, ty, columns);
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
					byte byteMask = (byte) ((bitplane[index + getSelectedTileOffset()] & mask));
					byteMask |= selectedColorIndex << ((3 - ix) * 2);
					bitplane[index + getSelectedTileOffset()] = byteMask;

				} else {
					int index = (((iy * currentWidth) + ix) >> 3) + offset;
					byte byteMask = bitplane[index + getSelectedTileOffset()];
					int pixelMask = (1 << (7 - (ix % 8)) & 0xff);
					bitplane[index + getSelectedTileOffset()] = paintMode ? (byte) (byteMask | pixelMask)
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
		setMultiColorEnabled(colorProvider.isMultiColorEnabled());
	}

	public void setBitlane(byte bitplane[]) {
		this.bitplane = bitplane;
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
		this.visibleColumns = columns;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
		this.visibleRows = rows;
	}

	public void setVisibleRows(int rows) {
		this.visibleRows = rows;
	}

	public int getVisibleRows() {
		return visibleRows;
	}

	public void setVisibleColumns(int columns) {
		this.visibleColumns = columns;
	}

	public int getVisibleColumns() {
		return visibleColumns;
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

	public void setWidgetName(String widgetName) {
		this.widgetName = widgetName;
	}

	public String getWidgetName() {
		return widgetName;
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

	public void setPaintMode(PaintMode drawMode) {
		this.drawMode = drawMode;
	}

	public PaintMode getDrawMode() {
		return drawMode;
	}

	public void setWidgetMode(WidgetMode widgetMode) {
		this.widgetMode = widgetMode;
	}

	public WidgetMode getWidgetMode() {
		return widgetMode;
	}

	public void recalc() {
		currentPixelWidth = getPixelSize() * (isMultiColorEnabled() ? 2 : 1);
		currentWidth = getWidth() / (isMultiColorEnabled() ? 2 : 1);
		bytesPerRow = width >> 3;
		clipboardBuffer = new byte[getViewportSize()];
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

	@Override
	public void doDrawPixel(int x, int y, boolean paintMode) {
		this.paintMode = paintMode;
		cursorX = x + (selectedTileIndexX * width * tileColumns);
		cursorY = y + (selectedTileIndexY * height * tileRows);
		doDrawPixel();
	}

	protected void doDrawPixel() {
		paintControlMode = SET_DRAW_PIXEL;
		System.out.println(getWidgetName() + ":   x:" + cursorX + "    y:" + cursorY);
		int inset = isPixelGridEnabled() ? 1 : 0;

		switch (drawMode) {
		case Pixel: {
			redraw((cursorX * currentPixelWidth) + inset, (cursorY * currentPixelHeight) + inset,
					currentPixelWidth - inset, currentPixelHeight - inset, true);
			break;
		}
		case VerticalMirror: {
			redraw((cursorX * currentPixelWidth) + inset, (cursorY * currentPixelHeight) + inset,
					currentPixelWidth - inset, currentPixelHeight - inset, true);
			int centerX = ((currentWidth * tileColumns) / 2);
			int diff = centerX - cursorX - 1;
			redraw(((centerX + diff) * currentPixelWidth) + inset, (cursorY * currentPixelHeight) + inset,
					currentPixelWidth - inset, currentPixelHeight - inset, true);
			break;
		}
		case HorizontalMirror: {
			redraw((cursorX * currentPixelWidth) + inset, (cursorY * currentPixelHeight) + inset,
					currentPixelWidth - inset, currentPixelHeight - inset, true);
			int centerY = ((height * tileRows) / 2);
			int diff = centerY - cursorY - 1;
			redraw((cursorX * currentPixelWidth) + inset, ((centerY + diff) * currentPixelHeight) + inset,
					currentPixelWidth - inset, currentPixelHeight - inset, true);
			break;
		}
		case Kaleidoscope: {
			redraw((cursorX * currentPixelWidth) + inset, (cursorY * currentPixelHeight) + inset,
					currentPixelWidth - inset, currentPixelHeight - inset, true);
			int centerX = ((currentWidth * tileColumns) / 2);
			int diffX = centerX - cursorX - 1;
			redraw(((centerX + diffX) * currentPixelWidth) + inset, (cursorY * currentPixelHeight) + inset,
					currentPixelWidth - inset, currentPixelHeight - inset, true);
			int centerY = ((height * tileRows) / 2);
			int diffY = centerY - cursorY - 1;
			redraw((cursorX * currentPixelWidth) + inset, ((centerY + diffY) * currentPixelHeight) + inset,
					currentPixelWidth - inset, currentPixelHeight - inset, true);
			redraw(((centerX + diffX) * currentPixelWidth) + inset, ((centerY + diffY) * currentPixelHeight) + inset,
					currentPixelWidth - inset, currentPixelHeight - inset, true);
			break;
		}
		}
	}

	@Override
	public void doDrawTile() {
		paintControlMode = SET_DRAW_TILE;
		redraw(selectedTileIndexX * width * pixelSize * tileColumns, selectedTileIndexY * height * pixelSize * tileRows,
				width * pixelSize * tileColumns, height * pixelSize * tileRows, true);
	}

	@Override
	public void doDrawAllTiles() {
		paintControlMode = SET_DRAW_ALL_TILES;
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

	public void swapTiles() {
		if (swapRingBuffer.size() == 2) {
			int swapSourceOffset = (getWidth() / 8) * getHeight() * tileColumns * tileRows
					* (swapRingBuffer.get(0).x + (swapRingBuffer.get(0).y * columns));
			int swapTargetOffset = (getWidth() / 8) * getHeight() * tileColumns * tileRows
					* (swapRingBuffer.get(1).x + (swapRingBuffer.get(1).y * columns));

			for (int i = 0; i < getViewportSize(); i++) {
				byte buffer = bitplane[swapSourceOffset + i];
				bitplane[swapSourceOffset + i] = bitplane[swapTargetOffset + i];
				bitplane[swapTargetOffset + i] = buffer;
			}
		}
		doDrawAllTiles();
		fireDoDrawAllTiles();
	}

	public void markAsSwapTarget() {
		if (swapRingBuffer.size() == 2) {
			swapRingBuffer.set(0, swapRingBuffer.get(1));
			swapRingBuffer.remove(1);
		}
		boolean hasMatch = false;
		for (TileLocation tl : swapRingBuffer) {
			if (tl.x == tileX && tl.y == tileY) {
				hasMatch |= true;
			}
		}
		if (!hasMatch) {
			swapRingBuffer.add(new TileLocation(tileX, tileY));
		}
		doDrawAllTiles();
	}

	public void removeSwapMarker() {
		if (isClearSwapBufferConfirmed()) {
			swapRingBuffer.clear();
			doDrawAllTiles();
		}
	}

	protected boolean isClearSwapBufferConfirmed() {
		return true;
	}

	public void clipboardAction(ClipboardAction clipboardAction) {
		int offset = (getWidth() / 8) * getHeight() * tileColumns * tileRows * (tileX + (tileY * columns));
		if (clipboardAction == ClipboardAction.Cut || clipboardAction == ClipboardAction.Copy) {
			this.clipboardAction = clipboardAction;
			cutCopyOffset = offset;
		}
		if (clipboardAction == ClipboardAction.Paste && this.clipboardAction != ClipboardAction.Off) {
			pasteOffset = offset;
			for (int i = 0; i < getViewportSize(); i++) {
				bitplane[pasteOffset + i] = bitplane[cutCopyOffset + i];
				if (this.clipboardAction == ClipboardAction.Cut) {
					bitplane[cutCopyOffset + i] = 0;
				}
			}
			this.clipboardAction = ClipboardAction.Off;
			doDrawAllTiles();
			fireDoDrawAllTiles();
		}
	}

	public void clearTile() {
		if (isClearTileConfirmed()) {
			int offset = (getWidth() / 8) * getHeight() * tileColumns * tileRows * (tileX + (tileY * columns));
			for (int i = 0; i < getViewportSize(); i++) {
				bitplane[offset + i] = 0;
			}
			doDrawAllTiles();
			fireDoDrawAllTiles();
		}
	}

	protected boolean isClearTileConfirmed() {
		return true;
	}

	public void shiftTile(int x, int y, Direction direction) {
	}

	public void flipTile(int x, int y, Orientation orientation) {
	}

	public void mirrorTile(int x, int y, Direction direction) {
	}

	public void rotateTile(int x, int y, Rotate direction) {
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		Point hsb = hBar != null ? hBar.getSize() : new Point(0, 0);
		Point vsb = vBar != null ? vBar.getSize() : new Point(0, 0);
		return new Point(
				(currentWidth * currentPixelWidth * tileColumns * columns) + (cursorLineWidth * (columns + 1)) + vsb.x
						- columns,
				(height * currentPixelHeight * tileRows * rows) + (cursorLineWidth * (rows + 1)) + hsb.x - rows);
	}

}