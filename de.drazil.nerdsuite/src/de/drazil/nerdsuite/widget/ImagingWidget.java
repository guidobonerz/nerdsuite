package de.drazil.nerdsuite.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.log.Console;
import de.drazil.nerdsuite.model.TileLocation;

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
	private int animationIndexX;
	private int animationIndexY;
	protected int visibleRows = 0;
	protected int visibleColumns = 0;
	protected int tileCursorX = 0;
	protected int tileCursorY = 0;
	protected int selectedColorIndex;
	protected int monoColorDefaultIndex;
	private int navigationOffset = 0;
	protected int colorCount;
	protected int selectedTileOffset = 0;
	protected int cursorLineWidth = 1;
	protected int leftButtonMode = 0;
	protected int rightButtonMode = 0;
	private int paintControlMode = DRAW_NOTHING;
	private PaintMode drawMode = PaintMode.Pixel;
	private PencilMode pencilMode = PencilMode.Draw;

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
	// protected boolean paintMode = true;
	protected boolean mouseIn = false;
	protected boolean animationMarkersSet = false;

	protected GridStyle gridStyle = GridStyle.LINE;
	protected Map<String, Color> palette;
	protected IColorProvider colorProvider;
	protected ScrollBar hBar = null;
	protected ScrollBar vBar = null;
	protected List<IDrawListener> drawListenerList = null;
	protected List<TileLocation> tileLocationList = null;
	private List<TileLocation> selectionRangeBuffer = null;
	protected String widgetName = "<unknown>";

	protected WidgetMode widgetMode;

	private Timer animationTimer;

	public enum WidgetMode {
		SELECTOR, PAINTER, VIEWER, BITMAP_VIEWER
	};

	public enum GridStyle {
		PIXEL, LINE
	};

	public enum TransformationType {
		Shift, Mirror, Flip, Rotate
	}

	public enum TransformationMode {
		Up, Down, Left, Right, UpperHalf, LowerHalf, LeftHalf, RightHalf, Horizontal, Vertical, CCW, CW
	}

	public enum ClipboardAction {
		Cut, Copy, Paste, Off
	};

	public enum PaintMode {
		Pixel, VerticalMirror, HorizontalMirror, Kaleidoscope
	}

	public enum PencilMode {
		Draw, Erase
	}

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

		selectionRangeBuffer = new ArrayList<>();
		tileLocationList = new ArrayList<>();

		hBar = getHorizontalBar();
		vBar = getVerticalBar();

		setBackground(Constants.BLACK);

		drawListenerList = new ArrayList<IDrawListener>();

		addPaintListener(this);
		parent.getDisplay().getActiveShell().addListener(SWT.Resize, new Listener() {

			@Override
			public void handleEvent(Event event) {
				doDrawAllTiles();
			}
		});
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {

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
				switch (e.keyCode) {

				case SWT.ARROW_UP: {

					if (selectedTileIndexY > 0) {
						selectedTileIndexY--;
						selectedTileOffset = computeTileOffset(selectedTileIndexX, selectedTileIndexY);
					} else {
						if (navigationOffset > 0) {
							navigationOffset -= computeTileSize() * columns;
						}
					}
					fireSetSelectedTileOffset(selectedTileOffset);
					doDrawAllTiles();

					break;
				}
				case SWT.ARROW_DOWN: {

					if (selectedTileIndexY < rows - 1) {
						selectedTileIndexY++;
						selectedTileOffset = computeTileOffset(selectedTileIndexX, selectedTileIndexY);
					} else {
						if (navigationOffset < bitplane.length - (computeTileSize() * rows * columns)) {
							navigationOffset += computeTileSize() * columns;
						}
					}
					fireSetSelectedTileOffset(selectedTileOffset);
					doDrawAllTiles();
					break;
				}
				case SWT.ARROW_LEFT: {

					if (selectedTileIndexX > 0) {
						selectedTileIndexX--;
					} else {
						selectedTileIndexX = columns - 1;
					}
					selectedTileOffset = computeTileOffset(selectedTileIndexX, selectedTileIndexY);
					fireSetSelectedTileOffset(selectedTileOffset);
					doDrawAllTiles();
					break;
				}
				case SWT.ARROW_RIGHT: {

					if (selectedTileIndexX < columns - 1) {
						selectedTileIndexX++;
					} else {
						selectedTileIndexX = 0;
					}
					selectedTileOffset = computeTileOffset(selectedTileIndexX, selectedTileIndexY);
					fireSetSelectedTileOffset(selectedTileOffset);
					doDrawAllTiles();
					break;
				}
				}
			}
		});

		addMouseTrackListener(new MouseTrackAdapter() {
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
				setFocus();
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
				if (widgetMode == WidgetMode.SELECTOR) {
					// System.out.println((e.stateMask & SWT.MODIFIER_MASK) + "
					// " + e.button + " " + leftButtonMode);
				}
				setCursorPosition(e.x, e.y);
				if (widgetMode == WidgetMode.PAINTER) {
					if (leftButtonMode == LEFT_BUTTON_PRESSED) {
						doDrawPixel();
						fireDoDrawTile();
					} else {
						doDrawAllTiles();
					}
				} else if (widgetMode == WidgetMode.SELECTOR) {
					if (leftButtonMode == LEFT_BUTTON_PRESSED && (e.stateMask & SWT.SHIFT) == SWT.SHIFT) {
						if (selectionRangeBuffer.isEmpty()) {
							selectionRangeBuffer.add(new TileLocation(tileX, tileY));
							selectionRangeBuffer.add(new TileLocation(0, 0));
						} else {
							selectionRangeBuffer.get(1).x = tileX;
							selectionRangeBuffer.get(1).y = tileY;
							int o1 = computeTileOffset(selectionRangeBuffer.get(0).x, selectionRangeBuffer.get(0).y);
							int o2 = computeTileOffset(selectionRangeBuffer.get(1).x, selectionRangeBuffer.get(1).y);
							int a = 0;
							int b = 1;
							if (o1 > o2) {
								a = 1;
								b = 0;
							}
							int xs = selectionRangeBuffer.get(a).x;
							int ys = selectionRangeBuffer.get(a).y;
							tileLocationList = new ArrayList<>();
							for (;;) {
								if (xs < columns) {
									if (!hasTile(xs, ys)) {
										tileLocationList.add(new TileLocation(xs, ys));
									}
									if (xs == selectionRangeBuffer.get(b).x && ys == selectionRangeBuffer.get(b).y) {
										break;
									}
									xs++;
								} else {
									xs = 0;
									ys++;
								}
							}
						}
					}
				}
				doDrawAllTiles();
			}
		});

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				setCursorPosition(e.x, e.y);

				if (e.button == MOUSE_BUTTON_RIGHT) {

					if (widgetMode == WidgetMode.PAINTER) {
						pencilMode = pencilMode == PencilMode.Draw ? PencilMode.Erase : PencilMode.Draw;
						Console.println("PencilMode:" + pencilMode);
					}

				} else if (e.button == MOUSE_BUTTON_LEFT) {
					if (leftButtonMode == LEFT_BUTTON_PRESSED) {
						leftButtonMode = 0;
						if ((e.stateMask & SWT.CTRL) == 0 && (e.stateMask & SWT.SHIFT) == 0) {
							tileLocationList = new ArrayList<>();
							selectionRangeBuffer = new ArrayList<>();
						}
					}
					if (widgetMode == WidgetMode.SELECTOR) {
						paintControlMode = 0;
						selectedTileIndexX = tileX;
						selectedTileIndexY = tileY;
						selectedTileOffset = computeTileOffset(selectedTileIndexX, selectedTileIndexY);
						fireSetSelectedTileOffset(selectedTileOffset);
						if ((e.stateMask & SWT.CTRL) != 0) {
							if (!hasTile(tileX, tileY)) {
								tileLocationList.add(new TileLocation(tileX, tileY));
							}
						}
						doDrawAllTiles();

					} else {
						doDrawPixel();
						fireDoDrawTile();
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
	}

	public void paintControl(PaintEvent e) {

		if ((paintControlMode & SET_DRAW_ALL_TILES) == SET_DRAW_ALL_TILES) {
			paintControlTiles(e.gc);
		}
		if ((paintControlMode & SET_DRAW_TILE) == SET_DRAW_TILE) {
			paintControlTile(e.gc, selectedTileIndexX, selectedTileIndexY);
		}
		if (widgetMode != WidgetMode.VIEWER && widgetMode != WidgetMode.BITMAP_VIEWER) {
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
				paintControlPixelCursor(e.gc, 0, 0);
			}

			paintControlAnimationMarkers(e.gc);
		}
		paintControlMode = DRAW_NOTHING;

	}

	public void paintControlAnimationMarkers(GC gc) {
		gc.setBackground(Constants.ANIMATION_TILE_MARKER_COLOR);
		gc.setAlpha(150);
		for (TileLocation tilelocation : tileLocationList) {
			gc.fillRectangle(tilelocation.x * width * pixelSize * tileColumns,
					tilelocation.y * height * pixelSize * tileRows, width * pixelSize * tileColumns,
					height * pixelSize * tileRows);
		}
	}

	public void paintControlTileCursor(GC gc, boolean mouseIn, int x, int y) {
		gc.setLineWidth(cursorLineWidth);
		gc.setLineStyle(SWT.LINE_SOLID);
		gc.setBackground(Constants.TILE_SUB_GRID_COLOR);
		gc.setAlpha(150);
		gc.fillRectangle(x * width * pixelSize * tileColumns, y * height * pixelSize * tileRows,
				width * pixelSize * tileColumns, height * pixelSize * tileRows);

		if (mouseIn) {
			gc.setBackground((tileX == x && tileY == y) ? Constants.LIGHT_RED : Constants.LIGHT_RED);
			gc.fillRectangle(tileX * width * pixelSize * tileColumns, tileY * height * pixelSize * tileRows,
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

	public void paintControlPixelCursor(GC gc, int x, int y) {
		// System.out.println(cursorX + " " + cursorY);
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
		if (widgetMode == WidgetMode.PAINTER || widgetMode == WidgetMode.VIEWER) {
			byteOffset = selectedTileOffset;
		} else if (widgetMode == WidgetMode.SELECTOR || widgetMode == WidgetMode.BITMAP_VIEWER) {
			byteOffset = computeTileOffset(tx, ty);
		}

		for (int i = byteOffset, k = 0; i < (byteOffset + computeTileSize()); i++, k++) {

			int xi = (k % bytesPerRow) * (8 / (isMultiColorEnabled() ? 2 : 1));
			int xo = (k / b1) % tileColumns;
			x = xi + (xo * currentWidth) + (tx * currentWidth * tileColumns);

			int yi = (k / bytesPerRow) % height;
			int yo = (k / b2) % tileRows;
			y = yi + (yo * height) + (ty * height * tileRows);

			if (i < bitplane.length) {
				int b = (bitplane[i] & 0xff);
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
					bitplane[index + getSelectedTileOffset()] = pencilMode == PencilMode.Draw
							? (byte) (byteMask | pixelMask) : (byte) (byteMask & ((pixelMask ^ 0xff) & 0xff));
				}
			}
		}

		gc.setBackground(pencilMode == PencilMode.Draw ? palette.get(String.valueOf(selectedColorIndex))
				: Constants.BITMAP_BACKGROUND_COLOR);

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
		clipboardBuffer = new byte[computeTileSize()];
		int selectedTileOffset = computeTileOffset(selectedTileIndexX, selectedTileIndexY);
		if (vBar != null) {
			vBar.setMinimum(0);
			vBar.setMaximum(getRows());

		}
		fireSetSelectedTileOffset(selectedTileOffset);
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
		// System.out.println(getWidgetName() + ":offset=" + offset);
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

	private void fireDoDrawPixel(int x, int y, PencilMode pencilMode) {
		for (IDrawListener listener : drawListenerList) {
			listener.doDrawPixel(x, y, pencilMode);
		}
	}

	@Override
	public void doDrawPixel(int x, int y, PencilMode pencilMode) {
		this.pencilMode = pencilMode;
		cursorX = x + (selectedTileIndexX * width * tileColumns);
		cursorY = y + (selectedTileIndexY * height * tileRows);
		doDrawPixel();
	}

	protected void doDrawPixel() {
		paintControlMode = SET_DRAW_PIXEL;
		// System.out.println(getWidgetName() + ": x:" + cursorX + " y:" +
		// cursorY);
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
		setNotification(selectedTileOffset, computeTileSize());
		redraw();
	}

	public void startAnimation() {

		if (tileLocationList.size() < 1) {
			showMessage("You have to select an animation range first.");
		} else {
			resetTimer();
			animationIsRunning = true;
			notifyAnimationStarted(animationIsRunning);
			animationTimer.scheduleAtFixedRate(new Animator(), 0, 70);
		}
	}

	public void stopAnimation() {
		animationIsRunning = false;
		notifyAnimationStarted(animationIsRunning);
		animationTimer.cancel();
		animationTimer.purge();
		resetTimer();
	}

	private void resetTimer() {
		animationTimer = new Timer();
	}

	public boolean isAnimationRunning() {
		return animationIsRunning;
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
					selectedTileOffset = computeTileOffset(animationIndexX, animationIndexY);
					fireSetSelectedTileOffset(selectedTileOffset);
					doDrawAllTiles();
				}
			});
		}
	}

	public void swapTiles() {

		if (tileLocationList.size() == 2) {
			int swapSourceOffset = computeTileOffset(tileLocationList.get(0).x, tileLocationList.get(0).y);
			int swapTargetOffset = computeTileOffset(tileLocationList.get(1).x, tileLocationList.get(1).y);

			for (int i = 0; i < computeTileSize(); i++) {
				byte buffer = bitplane[swapSourceOffset + i];
				bitplane[swapSourceOffset + i] = bitplane[swapTargetOffset + i];
				bitplane[swapTargetOffset + i] = buffer;
			}
		} else {
			showMessage("Please select only two tiles to swap");
		}
		doDrawAllTiles();
		fireDoDrawAllTiles();

	}

	private boolean checkIfSquareBase() {
		int w = currentWidth * tileColumns;
		int h = height * tileRows;
		return w == h;
	}

	protected void notifyAnimationStarted(boolean state) {
	}

	protected void setNotification(int offset, int tileSize) {

	}

	protected void showMessage(String message) {

	}

	protected boolean isClearTileConfirmed(boolean allSelected) {
		return true;
	}

	protected boolean isRotationConfirmed() {
		return true;
	}

	public void clipboardAction(ClipboardAction clipboardAction) {
		int offset = computeTileOffset(tileX, tileY);
		if (clipboardAction == ClipboardAction.Cut || clipboardAction == ClipboardAction.Copy) {
			this.clipboardAction = clipboardAction;
			cutCopyOffset = offset;
		}
		if (clipboardAction == ClipboardAction.Paste && this.clipboardAction != ClipboardAction.Off) {
			pasteOffset = offset;
			for (int i = 0; i < computeTileSize(); i++) {
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

	public void clearTiles(boolean allSelected) {
		if (isClearTileConfirmed(allSelected)) {
			if (allSelected) {
				for (TileLocation tl : tileLocationList) {
					clearTile(tl.x, tl.y);
				}
			} else {
				clearTile(tileX, tileY);
			}
			doDrawAllTiles();
			fireDoDrawAllTiles();
		}
	}

	private void clearTile(int x, int y) {
		int offset = computeTileOffset(x, y);
		for (int i = 0; i < computeTileSize(); i++) {
			bitplane[offset + i] = 0;
		}
	}

	public void transform(boolean allSelected, TransformationType type, TransformationMode mode) {
		byte workArray[] = convertToWorkArray(selectedTileIndexX, selectedTileIndexY);
		int fh = height * tileRows;
		int fw = width * tileColumns;
		switch (type) {

		case Shift: {
			switch (mode) {
			case Up: {
				for (int x = 0; x < fw; x++) {
					byte b = workArray[x];
					for (int y = 0; y < fh - 1; y++) {
						workArray[x + y * fw] = workArray[x + (y + 1) * fw];
					}
					workArray[x + (fw * (fh - 1))] = b;
				}
				break;
			}
			case Down: {

				for (int x = 0; x < fw; x++) {
					byte b = workArray[x + (fw * (fh - 1))];
					for (int y = fh - 1; y > 0; y--) {
						workArray[x + y * fw] = workArray[x + (y - 1) * fw];
					}
					workArray[x] = b;
				}
				break;
			}
			case Left: {
				for (int y = 0; y < fh; y++) {
					byte b = workArray[y * fw];
					for (int x = 0; x < fw - 1; x++) {
						workArray[x + y * fw] = workArray[(x + 1) + y * fw];
					}
					workArray[(fw + y * fw) - 1] = b;
				}
				break;
			}
			case Right: {
				for (int y = 0; y < fh; y++) {
					byte b = workArray[(fw + y * fw) - 1];
					for (int x = fw - 1; x > 0; x--) {
						workArray[x + y * fw] = workArray[(x - 1) + y * fw];
					}
					workArray[y * fw] = b;
				}
				break;
			}
			}
		}
		case Flip: {
			switch (mode) {
			case Vertical: {
				for (int y = 0; y < fh; y++) {
					for (int x = 0; x < fw / 2; x++) {
						byte a = workArray[x + (y * fw)];
						byte b = workArray[(fw) - 1 - x + (y * fw)];
						workArray[x + (y * fw)] = b;
						workArray[(fw) - 1 - x + (y * fw)] = a;
					}
				}
				break;
			}
			case Horizontal: {
				for (int y = 0; y < fh / 2; y++) {
					for (int x = 0; x < fw; x++) {
						byte a = workArray[x + (y * fw)];
						byte b = workArray[x + ((fh - y - 1) * fw)];
						workArray[x + (y * fw)] = b;
						workArray[x + ((fh - y - 1) * fw)] = a;
					}

				}
				break;
			}
			}
			break;
		}
		case Mirror: {
			switch (mode) {
			case UpperHalf: {
				break;
			}
			case LowerHalf: {
				break;
			}
			case LeftHalf: {
				break;
			}
			case RightHalf: {
				break;
			}
			}
			break;
		}
		case Rotate: {
			boolean doRotate = false;
			if (!(doRotate = checkIfSquareBase())) {
				doRotate = isRotationConfirmed();
			}
			if (doRotate) {
				byte sourceWorkArray[] = convertToWorkArray(selectedTileIndexX, selectedTileIndexY);
				printResult(sourceWorkArray);
				byte targetWorkArray[] = createWorkArray();
				switch (mode) {
				case CCW: {
					for (int y = 0; y < height * tileRows; y++) {
						for (int x = 0; x < width * tileColumns; x++) {
							byte b = sourceWorkArray[x + (y * width * tileColumns)];
							int o = (width * height * tileRows * tileColumns) - (width * tileColumns)
									- (width * tileColumns * x) + y;
							targetWorkArray[o] = b;
						}
					}
					workArray = targetWorkArray;
					break;
				}
				case CW: {
					/*
					 * for (int y = 0; y < height * tileRows; y++) { for (int x
					 * = 0; x < width * tileColumns; x++) { byte b =
					 * sourceWorkArray[x + (y * width * tileColumns)]; int o =
					 * (width * tileColumns) - x + (width * tileColumns * x) +
					 * y; targetWorkArray[o] = b; } } workArray =
					 * targetWorkArray;
					 */
					break;
				}
				}
				break;
			}
		}
		}
		convertToBitplane(workArray, selectedTileIndexX, selectedTileIndexY);
		doDrawTile();
		fireDoDrawAllTiles();
	}

	private void printResult(byte workArray[]) {
		System.out.println("-----------------------------------------");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < workArray.length; i++) {
			if (i % (width * tileColumns) == 0) {
				sb.append("\n");
			}
			sb.append(workArray[i]);
		}
		System.out.println(sb);
	}

	private byte[] convertToWorkArray(int xc, int yc) {
		int iconSize = computeIconSize();
		int tileSize = computeTileSize();
		int tileOffset = computeTileOffset(xc, yc);
		byte[] workArray = createWorkArray();

		for (int si = 0, s = 0; si < tileSize; si += bytesPerRow, s += bytesPerRow) {
			s = (si % (iconSize)) == 0 ? 0 : s;
			int xo = ((si / iconSize) & (tileColumns - 1)) * width;
			int yo = (si / (iconSize * tileColumns)) * height * width * tileColumns;
			int ro = ((s / bytesPerRow) * width) * tileColumns;
			int wai = ro + xo + yo;

			for (int i = 0; i < bytesPerRow; i++) {
				for (int m = 7, ti = 0; m >= 0; m -= (isMultiColorEnabled() ? 2 : 1), ti++) {
					workArray[wai + (8 * i)
							+ ti] = (byte) ((bitplane[tileOffset + si + i] >> m) & (isMultiColorEnabled() ? 3 : 1));
				}
			}
		}
		return workArray;
	}

	private byte[] createWorkArray() {
		int tileSize = computeTileSize();
		return new byte[tileSize * (isMultiColorEnabled() ? 4 : 8)];
	}

	private void convertToBitplane(byte workArray[], int xc, int yc) {
		int iconSize = computeIconSize();
		int tileSize = computeTileSize();
		int tileOffset = computeTileOffset(xc, yc);

		for (int si = 0, s = 0; si < tileSize; si += bytesPerRow, s += bytesPerRow) {
			s = (si % (iconSize)) == 0 ? 0 : s;
			int xo = ((si / iconSize) & (tileColumns - 1)) * width;
			int yo = (si / (iconSize * tileColumns)) * height * width * tileColumns;
			int ro = ((s / bytesPerRow) * width) * tileColumns;
			int wai = ro + xo + yo;

			for (int i = 0; i < bytesPerRow; i++) {
				bitplane[tileOffset + si + i] = 0;
				for (int m = 7, ti = 0; m >= 0; m -= (isMultiColorEnabled() ? 2 : 1), ti++) {
					(bitplane[tileOffset + si + i]) |= (workArray[wai + (8 * i) + ti] << m);
				}
			}
		}
	}

	private boolean hasTile(int x, int y) {
		for (TileLocation tl : tileLocationList) {
			if (tl.x == x && tl.y == y) {
				return true;
			}
		}
		return false;
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

	private int computeTileOffset(int x, int y) {
		return computeTileSize() * (x + (y * columns)) + navigationOffset;
	}

	public int computeTileSize() {
		return computeIconSize() * tileColumns * tileRows;
	}

	public int computeIconSize() {
		return bytesPerRow * height;
	}
}