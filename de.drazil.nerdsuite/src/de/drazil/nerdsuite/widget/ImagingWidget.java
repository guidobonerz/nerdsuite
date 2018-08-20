package de.drazil.nerdsuite.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.imaging.actions.IImagingAction;
import de.drazil.nerdsuite.log.Console;
import de.drazil.nerdsuite.model.TileLocation;

public class ImagingWidget extends BaseImagingWidget implements IDrawListener, PaintListener {

	protected final static int SET_DRAW_NOTHING = 0;
	protected final static int SET_DRAW_ALL_TILES = 1;
	protected final static int SET_DRAW_TILE = 2;
	protected final static int SET_DRAW_PIXEL = 4;

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
	private int animationTimerDelay = 200;
	private int paintControlMode = SET_DRAW_NOTHING;
	private PaintMode drawMode = PaintMode.Simple;
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
	protected boolean mouseIn = false;

	protected Map<String, Color> palette;
	protected IColorProvider colorProvider;
	protected ScrollBar hBar = null;
	protected ScrollBar vBar = null;
	protected List<IDrawListener> drawListenerList = null;
	private List<TileLocation> tileSelectionList = null;
	private List<TileLocation> selectionRangeBuffer = null;
	protected String widgetName = "<unknown>";

	private Map<String, IImagingAction> actionMap = null;

	protected GridStyle gridStyle = GridStyle.Line;
	protected WidgetMode widgetMode;

	private Animator animator = null;

	public enum WidgetMode {
		Selector, Painter, Viewer, BitmapViewer
	};

	public enum GridStyle {
		Dot, Line
	};

	public enum ImagingService {
		All("All"), Shift("Shift"), Mirror("Mirror"), Flip("Flip"), Rotate("Rotate"), Purge("Purge",
				false), Swap("Swap", false, true), Animation("Animation", false);
		private final String name;
		private final boolean convert;
		private final boolean ignoreSelectionList;

		ImagingService(String name) {
			this(name, true);
		}

		ImagingService(String name, boolean convert) {
			this(name, convert, false);
		}

		ImagingService(String name, boolean convert, boolean ignoreSelectionList) {
			this.name = name;
			this.convert = convert;
			this.ignoreSelectionList = ignoreSelectionList;
		}

		public String getName() {
			return name;
		}

		public boolean doConvert() {
			return convert;
		}

		public boolean ignoreSelectionList() {
			return ignoreSelectionList;
		}
	}

	public enum PixelBits {
		OneBit("OneBit", 1), TwoBit("TwoBit", 2), Byte("Byte", 8);
		private final String name;
		private final int bits;

		PixelBits(String name, int bits) {
			this.name = name;
			this.bits = bits;
		}

		public String getName() {
			return name;
		}

		public int getBits() {
			return bits;
		}
	}

	public enum ImagingServiceAction {
		Up, Down, Left, Right, UpperHalf, LowerHalf, LeftHalf, RightHalf, Horizontal, Vertical, CCW, CW, Start, Stop
	}

	public enum ClipboardAction {
		Cut, Copy, Paste, Off
	};

	public enum PaintMode {
		Simple, VerticalMirror, HorizontalMirror, Kaleidoscope
	}

	public enum PencilMode {
		Draw, Erase
	}

	public enum Brush {
		Dot, Pattern
	}

	public enum ConversionMode {
		toWorkArray, toBitplane
	}

	public class Animator implements Runnable {
		public synchronized void run() {
			Collections.rotate(tileSelectionList, -1);
			TileLocation tl = tileSelectionList.get(0);
			animationIndexX = tl.x;
			animationIndexY = tl.y;
			selectedTileOffset = computeTileOffset(animationIndexX, animationIndexY);
			fireSetSelectedTileOffset(selectedTileOffset);
			doDrawAllTiles();
			getDisplay().timerExec(animationTimerDelay, this);
		}
	}

	public ImagingWidget(Composite parent, int style) {
		super(parent, style);

		setTileColumns(1);
		setTileRows(1);
		setColumns(1);
		setRows(1);

		animator = new Animator();

		if (widgetMode == WidgetMode.Selector) {
			actionMap = new HashMap<>();
			actionMap.put(ImagingService.Shift.getName(), null);
			actionMap.put(ImagingService.Mirror.getName(), null);
			actionMap.put(ImagingService.Flip.getName(), null);
			actionMap.put(ImagingService.Rotate.getName(), null);
			actionMap.put(ImagingService.Animation.getName(), null);
			actionMap.put(ImagingService.Swap.getName(), null);
			actionMap.put(ImagingService.Purge.getName(), null);
		}
		selectionRangeBuffer = new ArrayList<>();
		tileSelectionList = new ArrayList<>();

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

					if (tileY > 0) {
						tileY--;
						selectedTileOffset = computeTileOffset(tileX, tileY);
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

					if (tileY < rows - 1) {
						tileY++;
						selectedTileOffset = computeTileOffset(tileX, tileY);
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

					if (tileX > 0) {
						tileX--;
					} else {
						tileX = columns - 1;
					}
					selectedTileOffset = computeTileOffset(tileX, tileY);
					fireSetSelectedTileOffset(selectedTileOffset);
					doDrawAllTiles();
					break;
				}
				case SWT.ARROW_RIGHT: {

					if (tileX < columns - 1) {
						tileX++;
					} else {
						tileX = 0;
					}
					selectedTileOffset = computeTileOffset(tileX, tileY);
					fireSetSelectedTileOffset(selectedTileOffset);
					doDrawAllTiles();
					break;
				}
				}
			}
		});
	}

	@Override
	public void rightMouseButtonClicked(int modifierMask, int x, int y) {
		// System.out.println("right clicked");
		if (widgetMode == WidgetMode.Painter) {
			pencilMode = pencilMode == PencilMode.Draw ? PencilMode.Erase : PencilMode.Draw;
			Console.println("PencilMode:" + pencilMode);
		}
	}

	@Override
	public void leftMouseButtonClicked(int modifierMask, int x, int y) {
		// System.out.println("left click");
		setCursorPosition(x, y);
		if (widgetMode == WidgetMode.Selector) {
			paintControlMode = 0;
			selectedTileIndexX = tileX;
			selectedTileIndexY = tileY;
			selectedTileOffset = computeTileOffset(selectedTileIndexX, selectedTileIndexY);
			fireSetSelectedTileOffset(selectedTileOffset);
			computeSelection(false, (modifierMask & SWT.CTRL) == SWT.CTRL);
			doDrawAllTiles();
		} else if (widgetMode == WidgetMode.Painter) {
			doDrawPixel();
			fireDoDrawAllTiles();
		}
	}

	@Override
	public void mouseMove(int modifierMask, int x, int y) {
		// System.out.println("moved");
		setCursorPosition(x, y);
		doDrawAllTiles();
	}

	@Override
	public void mouseExit(int modifierMask, int x, int y) {
		// System.out.println("exit");
		mouseIn = false;
		doDrawAllTiles();
	}

	@Override
	public void mouseEnter(int modifierMask, int x, int y) {
		// System.out.println("enter");
		setFocus();
		mouseIn = true;
		doDrawAllTiles();
	}

	@Override
	public void mouseDragged(int modifierMask, int x, int y) {
		// System.out.println("dragged");
		setCursorPosition(x, y);
		if (widgetMode == WidgetMode.Painter) {
			doDrawPixel();
			fireDoDrawTile();
		} else if (widgetMode == WidgetMode.Selector) {
			computeSelection(false, (modifierMask & SWT.CTRL) == SWT.CTRL);
			doDrawAllTiles();
		}
	}

	@Override
	public void leftMouseButtonPressed(int modifierMask, int x, int y) {
		// System.out.println("left pressed");
		if (widgetMode == WidgetMode.Selector) {
			resetSelectionList();
		}
	}

	public void selectAll() {
		if (widgetMode == WidgetMode.Selector) {
			resetSelectionList();
			computeSelection(true, false);
			doDrawAllTiles();
		}
	}

	protected void setCursorPosition(int x, int y) {
		cursorX = x / currentPixelWidth;
		cursorY = y / currentPixelHeight;
		tileX = x / (currentWidth * currentPixelWidth * tileColumns);
		tileY = y / (height * currentPixelHeight * tileRows);
		tileCursorX = (cursorX - (tileX * width));
		tileCursorY = (cursorY - (tileY * height));
	}

	private void resetSelectionList() {
		tileSelectionList = new ArrayList<>();
		selectionRangeBuffer = new ArrayList<>();
	}

	private void computeSelection(boolean selectAll, boolean addNewSelectionRange) {
		if (addNewSelectionRange) {
			System.out.println("add new selection range");
		}
		if (selectionRangeBuffer.isEmpty()) {
			if (selectAll) {
				selectionRangeBuffer.add(new TileLocation(0, 0));
				selectionRangeBuffer.add(new TileLocation(columns - 1, rows - 1));
			} else {
				selectionRangeBuffer.add(new TileLocation(tileX, tileY));
				selectionRangeBuffer.add(new TileLocation(tileX, tileY));
			}
		}
		if (!selectAll) {
			selectionRangeBuffer.get(1).x = tileX;
			selectionRangeBuffer.get(1).y = tileY;
		}
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
		tileSelectionList = new ArrayList<>();
		for (;;) {
			if (xs < columns) {
				if (!hasTile(xs, ys)) {
					tileSelectionList.add(new TileLocation(xs, ys));
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
		setHasTileSelection(tileSelectionList.size());

	}

	public void paintControl(PaintEvent e) {

		if ((paintControlMode & SET_DRAW_ALL_TILES) == SET_DRAW_ALL_TILES) {
			paintControlTiles(e.gc);
		}
		if ((paintControlMode & SET_DRAW_TILE) == SET_DRAW_TILE) {
			paintControlTile(e.gc, selectedTileIndexX, selectedTileIndexY);
		}
		if (widgetMode != WidgetMode.Viewer && widgetMode != WidgetMode.BitmapViewer) {
			if (paintControlMode == SET_DRAW_PIXEL) {
				switch (drawMode) {
				case Simple: {
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

			paintControlSelection(e.gc);

			if (isTileCursorEnabled()) {
				paintControlTileCursor(e.gc, mouseIn, isAnimationRunning());
			}
			/*
			 * if (widgetMode == WidgetMode.Painter) {
			 * paintControlPixelCursor(e.gc, 0, 0); }
			 */

		}
		paintControlMode = SET_DRAW_NOTHING;

	}

	public void paintControlSelection(GC gc) {
		gc.setBackground(Constants.SELECTION_TILE_MARKER_COLOR);
		gc.setAlpha(150);
		for (TileLocation tilelocation : tileSelectionList) {
			gc.fillRectangle(tilelocation.x * width * pixelSize * tileColumns,
					tilelocation.y * height * pixelSize * tileRows, width * pixelSize * tileColumns,
					height * pixelSize * tileRows);
		}
	}

	public void paintControlTileCursor(GC gc, boolean mouseIn, boolean isAnimationRunning) {

		if (mouseIn) {
			gc.setAlpha(150);
			gc.setBackground(Constants.RED);
			gc.fillRectangle(tileX * width * pixelSize * tileColumns, tileY * height * pixelSize * tileRows,
					width * pixelSize * tileColumns, height * pixelSize * tileRows);
		}
		if (isAnimationRunning) {
			gc.setAlpha(255);
			gc.setLineWidth(3);
			gc.setForeground(Constants.LIGHT_GREEN2);
			gc.drawRectangle(animationIndexX * width * pixelSize * tileColumns,
					animationIndexY * height * pixelSize * tileRows, width * pixelSize * tileColumns,
					height * pixelSize * tileRows);
		}
	}

	public void paintControlPixelGrid(GC gc) {
		for (int x = 0; x <= currentWidth * tileColumns; x++) {
			for (int y = 0; y <= height * tileRows; y++) {
				gc.setForeground(Constants.PIXEL_GRID_COLOR);
				if (gridStyle == GridStyle.Line) {
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
		gc.setBackground(Constants.RED);
		gc.setForeground(Constants.RED);
		gc.fillRectangle((cursorX * currentPixelWidth) + 1 + (currentPixelWidth / 2) - pixelSize / 8,
				(cursorY * pixelSize) + 1 + (pixelSize / 2) - pixelSize / 8, pixelSize / 4, pixelSize / 4);

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
		if (widgetMode == WidgetMode.Painter || widgetMode == WidgetMode.Viewer) {
			byteOffset = selectedTileOffset;
		} else if (widgetMode == WidgetMode.Selector || widgetMode == WidgetMode.BitmapViewer) {
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

		if (widgetMode == WidgetMode.Painter) {
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
		int inset = isPixelGridEnabled() ? 1 : 0;

		switch (drawMode) {
		case Simple: {
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
		if (tileSelectionList.size() < 1) {
			showNotification(null, null, "You have to select an animation range first.", null);
		} else if (tileSelectionList.size() == 1) {
			showNotification(null, null, "You have to select at least two tiles to start the animation.", null);
		} else {
			animationIsRunning = true;
			showNotification(ImagingService.Animation, ImagingServiceAction.Start,
					isAnimationRunning() ? "Stop Animation (" + (animationTimerDelay) + " ms)" : "Start Animation",
					animationIsRunning);
			getDisplay().timerExec(0, animator);
		}
	}

	public void stopAnimation() {
		animationIsRunning = false;
		showNotification(ImagingService.Animation, ImagingServiceAction.Start,
				isAnimationRunning() ? "Stop Animation (" + (animationTimerDelay) + " ms)" : "Start Animation",
				animationIsRunning);
		getDisplay().timerExec(-1, animator);
		doDrawAllTiles();
	}

	public boolean isAnimationRunning() {
		return animationIsRunning;
	}

	public boolean isAnimatable() {
		return tileSelectionList.size() > 1;
	}

	public void changeAnimationTimerDelay(int delay) {
		animationTimerDelay = delay;
		if (isAnimationRunning()) {
			showNotification(ImagingService.Animation, ImagingServiceAction.Start,
					isAnimationRunning() ? "Stop Animation (" + (animationTimerDelay) + " ms)" : "Start Animation",
					animationIsRunning);
			getDisplay().timerExec(delay, animator);
		}
	}

	private boolean checkIfSquareBase() {
		int w = currentWidth * tileColumns;
		int h = height * tileRows;
		return w == h;
	}

	protected void setHasTileSelection(int count) {
	}

	protected void setNotification(int offset, int tileSize) {

	}

	protected boolean isConfirmed(ImagingService type, ImagingServiceAction mode, int tileCount) {
		return true;
	}

	protected void showNotification(ImagingService type, ImagingServiceAction mode, String notification, Object data) {

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

	public void action(boolean allSelected, ImagingService type) {
		action(allSelected, type, null);
	}

	public void action(boolean allSelected, ImagingService type, ImagingServiceAction mode) {
		int fh = height * tileRows;
		int fw = width * tileColumns;
		int size = fh * fw;
		int tsize = computeTileSize();
		byte workArray[] = null;
		if (isConfirmed(ImagingService.All, null, tileSelectionList.size())) {
			for (int i = 0; i < (type.ignoreSelectionList ? 1 : tileSelectionList.size()); i++) {
				if (type.convert) {
					workArray = createWorkArray();
					convert(workArray, bitplane, tileSelectionList.get(i).x, tileSelectionList.get(i).y,
							ConversionMode.toWorkArray);
				}
				switch (type) {
				case Purge: {
					int offset = computeTileOffset(tileSelectionList.get(i).x, tileSelectionList.get(i).y);
					for (int n = 0; n < tsize; n++) {
						bitplane[offset + n] = 0;
					}
					break;
				}
				case Swap: {
					if (tileSelectionList.size() == 2) {
						int swapSourceOffset = computeTileOffset(tileSelectionList.get(0).x,
								tileSelectionList.get(0).y);
						int swapTargetOffset = computeTileOffset(tileSelectionList.get(1).x,
								tileSelectionList.get(1).y);

						for (int n = 0; n < computeTileSize(); n++) {
							byte buffer = bitplane[swapSourceOffset + n];
							bitplane[swapSourceOffset + n] = bitplane[swapTargetOffset + n];
							bitplane[swapTargetOffset + n] = buffer;
						}
					} else {
						showNotification(ImagingService.Swap, null, "Please select only two tiles to swap.", null);
					}
					break;
				}
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
					case Horizontal: {
						for (int y = 0; y < fh; y++) {
							for (int x = 0; x < fw / 2; x++) {
								byte a = workArray[x + (y * fw)];
								byte b = workArray[fw - 1 - x + (y * fw)];
								workArray[x + (y * fw)] = b;
								workArray[fw - 1 - x + (y * fw)] = a;
							}
						}
						break;
					}
					case Vertical: {
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
						for (int y = 0; y < fh / 2; y++) {
							for (int x = 0; x < fw; x++) {
								workArray[x + ((fh - y - 1) * fw)] = workArray[x + (y * fw)];
							}
						}
						break;
					}
					case LowerHalf: {
						for (int y = 0; y < fh / 2; y++) {
							for (int x = 0; x < fw; x++) {
								workArray[x + (y * fw)] = workArray[x + ((fh - y - 1) * fw)];

							}
						}
						break;
					}
					case LeftHalf: {
						for (int y = 0; y < fh; y++) {
							for (int x = 0; x < fw / 2; x++) {
								workArray[fw - 1 - x + (y * fw)] = workArray[x + (y * fw)];
							}
						}
						break;
					}
					case RightHalf: {
						for (int y = 0; y < fh; y++) {
							for (int x = 0; x < fw / 2; x++) {
								workArray[x + (y * fw)] = workArray[fw - 1 - x + (y * fw)];
							}
						}
						break;
					}
					}
					break;
				}
				case Rotate: {
					boolean doRotate = false;
					if (!(doRotate = checkIfSquareBase())) {
						doRotate = isConfirmed(type, mode, 0);
					}
					if (doRotate) {

						byte targetWorkArray[] = createWorkArray();
						switch (mode) {
						case CCW: {
							for (int y = 0; y < height * tileRows; y++) {
								for (int x = 0; x < width * tileColumns; x++) {
									byte b = workArray[x + (y * width * tileColumns)];
									int o = (width * height * tileRows * tileColumns) - (width * tileColumns)
											- (width * tileColumns * x) + y;
									if (o >= 0 && o < size) {
										targetWorkArray[o] = b;
									}
								}
							}
							break;
						}
						case CW: {
							for (int y = 0; y < height * tileRows; y++) {
								for (int x = 0; x < width * tileColumns; x++) {
									byte b = workArray[x + (y * width * tileColumns)];
									int o = (width * tileColumns) - y - 1 + (x * width * tileColumns);
									if (o >= 0 && o < size) {
										targetWorkArray[o] = b;
									}
								}
							}
							break;
						}
						}
						workArray = targetWorkArray;
					}
					break;
				}
				}
				if (type.convert) {
					convert(workArray, bitplane, tileSelectionList.get(i).x, tileSelectionList.get(i).y,
							ConversionMode.toBitplane);
				}
			}
		}
		if (tileSelectionList.size() == 1) {
			doDrawTile();
		} else {
			doDrawAllTiles();
		}
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

	private void convert(byte workArray[], byte bitplane[], int x, int y, ConversionMode mode) {
		int iconSize = computeIconSize();
		int tileSize = computeTileSize();
		int tileOffset = computeTileOffset(x, y);

		for (int si = 0, s = 0; si < tileSize; si += bytesPerRow, s += bytesPerRow) {
			s = (si % (iconSize)) == 0 ? 0 : s;
			int xo = ((si / iconSize) & (tileColumns - 1)) * width;
			int yo = (si / (iconSize * tileColumns)) * height * width * tileColumns;
			int ro = ((s / bytesPerRow) * width) * tileColumns;
			int wai = ro + xo + yo;

			for (int i = 0; i < bytesPerRow; i++) {
				bitplane[tileOffset + si + i] = mode == ConversionMode.toBitplane ? 0 : bitplane[tileOffset + si + i];
				for (int m = 7, ti = 0; m >= 0; m -= (isMultiColorEnabled() ? 2 : 1), ti++) {
					if (mode == ConversionMode.toWorkArray) {
						workArray[wai + (8 * i)
								+ ti] = (byte) ((bitplane[tileOffset + si + i] >> m) & (isMultiColorEnabled() ? 3 : 1));
					} else if (mode == ConversionMode.toBitplane) {
						(bitplane[tileOffset + si + i]) |= (workArray[wai + (8 * i) + ti] << m);
					}
				}
			}
		}
	}

	private byte[] createWorkArray() {
		int tileSize = computeTileSize();
		return new byte[tileSize * (isMultiColorEnabled() ? 4 : 8)];
	}

	private boolean hasTile(int x, int y) {
		for (TileLocation tl : tileSelectionList) {
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

	private int computeIconSize() {
		return bytesPerRow * height;
	}

	public void setMouseActionEnabled(boolean mouseActionEnabled) {
		ama.setMouseActionEnabled(mouseActionEnabled);
	}

}