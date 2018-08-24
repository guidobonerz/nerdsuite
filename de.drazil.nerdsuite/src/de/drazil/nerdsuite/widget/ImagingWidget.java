package de.drazil.nerdsuite.widget;

import java.util.ArrayList;
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
import de.drazil.nerdsuite.imaging.service.AbstractService;
import de.drazil.nerdsuite.imaging.service.AnimationService;
import de.drazil.nerdsuite.imaging.service.ClipboardService;
import de.drazil.nerdsuite.imaging.service.FlipService;
import de.drazil.nerdsuite.imaging.service.IImagingService;
import de.drazil.nerdsuite.imaging.service.InvertService;
import de.drazil.nerdsuite.imaging.service.MirrorService;
import de.drazil.nerdsuite.imaging.service.PurgeService;
import de.drazil.nerdsuite.imaging.service.RotationService;
import de.drazil.nerdsuite.imaging.service.ShiftService;
import de.drazil.nerdsuite.imaging.service.SwapService;
import de.drazil.nerdsuite.log.Console;
import de.drazil.nerdsuite.model.TileLocation;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration.GridStyle;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration.PencilMode;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration.WidgetMode;

public class ImagingWidget extends BaseImagingWidget implements IDrawListener, PaintListener, IImagingCallback {

	private final static int SET_DRAW_NOTHING = 0;
	private final static int SET_DRAW_ALL_TILES = 1;
	private final static int SET_DRAW_TILE = 2;
	private final static int SET_DRAW_PIXEL = 4;

	private int layerCount = 0;
	private int activeLayer = 0;
	private int maxLayerCount = 4;

	private boolean keyPressed = false;
	private int currentKeyCodePressed = 0;
	private char currentCharacterPressed = 0;
	private boolean altS = false;

	private int selectedTileIndexX = 0;
	private int selectedTileIndexY = 0;

	private int cursorX = 0;
	private int cursorY = 0;
	private int oldTileX = 0;
	private int oldTileY = 0;
	private int tileX = 0;
	private int tileY = 0;
	private int tileCursorX = 0;
	private int tileCursorY = 0;
	private boolean updateCursorLocation = false;

	private int selectedColorIndex;
	private int selectedTileOffset = 0;
	private int monoColorDefaultIndex;
	private int navigationOffset = 0;
	private int colorCount;

	private int paintControlMode = SET_DRAW_NOTHING;

	private byte bitplane[];

	private boolean mouseIn = false;
	private Map<String, Color> palette;
	private IColorProvider colorProvider;
	private ScrollBar hBar = null;
	private ScrollBar vBar = null;
	private List<IDrawListener> drawListenerList = null;
	private List<TileLocation> tileSelectionList = null;
	private List<TileLocation> selectionRangeBuffer = null;

	private Map<String, IImagingService> serviceCacheMap = null;

	public enum ImagingServiceDescription {
		All("All", null), Shift("Shift", ShiftService.class), Mirror("Mirror", MirrorService.class), Flip("Flip",
				FlipService.class), Rotate("Rotate", RotationService.class), Purge("Purge", PurgeService.class), Swap(
						"Swap", SwapService.class), Invert("Invert", InvertService.class), Animation("Animation",
								AnimationService.class), Clipboard("Clipboard", ClipboardService.class);
		private final String name;
		private final Class<?> cls;

		ImagingServiceDescription(String name, Class<?> cls) {
			this.name = name;
			this.cls = cls;
		}

		public String getName() {
			return name;
		}

		public Class<?> getCls() {
			return cls;
		}
	}

	public ImagingWidget(Composite parent, int style) {
		this(parent, style, null);
	}

	public ImagingWidget(Composite parent, int style, ImagingWidgetConfiguration configuration) {
		super(parent, style, configuration);

		serviceCacheMap = new HashMap<>();
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
			@Override
			public void keyReleased(KeyEvent e) {
				keyPressed = false;
				currentCharacterPressed = e.character;
				currentKeyCodePressed = e.keyCode;
				modifierMask = e.stateMask & SWT.MODIFIER_MASK;
				altS = checkKeyPressed(SWT.ALT, 's');

			}

			public void keyPressed(KeyEvent e) {
				keyPressed = true;
				currentCharacterPressed = e.character;
				currentKeyCodePressed = e.keyCode;
				modifierMask = e.stateMask & SWT.MODIFIER_MASK;
				altS = checkKeyPressed(SWT.ALT, 's');

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
						selectedTileOffset = conf.computeTileOffset(tileX, tileY, navigationOffset);
					} else {
						if (navigationOffset > 0) {
							navigationOffset -= conf.getTileSize() * conf.columns;
						}
					}
					fireSetSelectedTileOffset(selectedTileOffset);
					doDrawAllTiles();

					break;
				}
				case SWT.ARROW_DOWN: {

					if (tileY < conf.rows - 1) {
						tileY++;
						selectedTileOffset = conf.computeTileOffset(tileX, tileY, navigationOffset);
					} else {
						if (navigationOffset < bitplane.length - (conf.getTileSize() * conf.rows * conf.columns)) {
							navigationOffset += conf.getTileSize() * conf.columns;
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
						tileX = conf.columns - 1;
					}
					selectedTileOffset = conf.computeTileOffset(tileX, tileY, navigationOffset);
					fireSetSelectedTileOffset(selectedTileOffset);
					doDrawAllTiles();
					break;
				}
				case SWT.ARROW_RIGHT: {

					if (tileX < conf.columns - 1) {
						tileX++;
					} else {
						tileX = 0;
					}
					selectedTileOffset = conf.computeTileOffset(tileX, tileY, navigationOffset);
					fireSetSelectedTileOffset(selectedTileOffset);
					doDrawAllTiles();
					break;
				}
				}
			}
		});
	}

	@Override
	public void rightMouseButtonClicked(int  modifierMask, int x, int y) {
		if (conf.widgetMode == WidgetMode.Painter) {
			conf.pencilMode = conf.pencilMode == PencilMode.Draw ? PencilMode.Erase : PencilMode.Draw;
			Console.println("PencilMode:" + conf.pencilMode);
		}
	}

	@Override
	public void leftMouseButtonClicked(int modifierMask, int x, int y) {
		computeCursorPosition(x, y);
		if (conf.widgetMode == WidgetMode.Selector) {
			paintControlMode = 0;
			selectedTileIndexX = tileX;
			selectedTileIndexY = tileY;
			selectedTileOffset = conf.computeTileOffset(selectedTileIndexX, selectedTileIndexY, navigationOffset);
			fireSetSelectedTileOffset(selectedTileOffset);
			computeSelection(false, false);
			doDrawAllTiles();
		} else if (conf.widgetMode == WidgetMode.Painter) {
			doDrawPixel();
			fireDoDrawAllTiles();
		}
	}

	@Override
	public void mouseMove(int modifierMask, int x, int y) {
		computeCursorPosition(x, y);
		doDrawAllTiles();
	}

	@Override
	public void mouseExit(int modifierMask, int x, int y) {
		mouseIn = false;
		doDrawAllTiles();
	}

	@Override
	public void mouseEnter(int modifierMask, int x, int y) {
		setFocus();
		mouseIn = true;
		doDrawAllTiles();
	}

	@Override
	public void mouseDragged(int modifierMask, int x, int y) {
		computeCursorPosition(x, y);
		if (conf.widgetMode == WidgetMode.Painter) {
			doDrawPixel();
			fireDoDrawTile();
		} else if (conf.widgetMode == WidgetMode.Selector) {
			computeSelection(false, false);
			doDrawAllTiles();
		}
	}

	@Override
	public void leftMouseButtonPressed(int modifierMask, int x, int y) {
		if (conf.widgetMode == WidgetMode.Selector) {
			resetSelectionList();
		}
	}

	public void selectAll() {
		if (conf.widgetMode == WidgetMode.Selector) {
			resetSelectionList();
			computeSelection(true, false);
			doDrawAllTiles();
		}
	}

	protected void computeCursorPosition(int x, int y) {
		cursorX = x / conf.currentPixelWidth;
		cursorY = y / conf.currentPixelHeight;
		tileX = x / (conf.currentWidth * conf.currentPixelWidth * conf.tileColumns);
		tileY = y / (conf.height * conf.currentPixelHeight * conf.tileRows);
		tileCursorX = (cursorX - (tileX * conf.width));
		tileCursorY = (cursorY - (tileY * conf.height));
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
				selectionRangeBuffer.add(new TileLocation(conf.columns - 1, conf.rows - 1));
			} else {
				selectionRangeBuffer.add(new TileLocation(tileX, tileY));
				selectionRangeBuffer.add(new TileLocation(tileX, tileY));
			}
		}
		if (!selectAll) {
			selectionRangeBuffer.get(1).x = tileX;
			selectionRangeBuffer.get(1).y = tileY;
		}
		int o1 = conf.computeTileOffset(selectionRangeBuffer.get(0).x, selectionRangeBuffer.get(0).y, navigationOffset);
		int o2 = conf.computeTileOffset(selectionRangeBuffer.get(1).x, selectionRangeBuffer.get(1).y, navigationOffset);
		int a = 0;
		int b = 1;
		if (o1 > o2) {
			a = 1;
			b = 0;
		}
		int xa = selectionRangeBuffer.get(a).x;
		int ya = selectionRangeBuffer.get(a).y;
		int xb = selectionRangeBuffer.get(b).x;
		int yb = selectionRangeBuffer.get(b).y;
		int x = xa;
		int y = ya;
		tileSelectionList = new ArrayList<>();

		for (;;) {
			if (!isTileSelected(x, y)) {
				tileSelectionList.add(new TileLocation(x, y));
			}
			if (x == selectionRangeBuffer.get(b).x && y == selectionRangeBuffer.get(b).y) {
				break;
			}

			if (x < (altS ? xb : conf.columns)) {
				x++;
			} else {
				x = (altS ? xa : 0);
				y++;
			}

			// System.out.println(x + " " + y + " " + xa + " " + xb);
		}

	}

	private boolean checkKeyPressed(int modifierKey, char charCode) {

		return (modifierMask & modifierKey) == modifierKey && currentCharacterPressed == charCode && keyPressed;
	}

	public void paintControl(PaintEvent e) {

		if ((paintControlMode & SET_DRAW_ALL_TILES) == SET_DRAW_ALL_TILES) {
			paintControlTiles(e.gc);
		}
		if ((paintControlMode & SET_DRAW_TILE) == SET_DRAW_TILE) {
			paintControlTile(e.gc, selectedTileIndexX, selectedTileIndexY);
		}
		if (conf.widgetMode != WidgetMode.Viewer && conf.widgetMode != WidgetMode.BitmapViewer) {
			if (paintControlMode == SET_DRAW_PIXEL) {
				switch (conf.paintMode) {
				case Simple: {
					paintControlPixel(e.gc, cursorX, cursorY);
					break;
				}
				case VerticalMirror: {
					paintControlPixel(e.gc, cursorX, cursorY);
					int centerX = ((conf.width * conf.tileColumns) / 2);
					int diff = centerX - cursorX - 1;
					paintControlPixel(e.gc, centerX + diff, cursorY);
					break;
				}
				case HorizontalMirror: {
					paintControlPixel(e.gc, cursorX, cursorY);
					int centerY = ((conf.height * conf.tileRows) / 2);
					int diff = centerY - cursorY - 1;
					paintControlPixel(e.gc, cursorX, centerY + diff);
					break;
				}
				case Kaleidoscope: {
					paintControlPixel(e.gc, cursorX, cursorY);
					int centerX = ((conf.width * conf.tileColumns) / 2);
					int diffX = centerX - cursorX - 1;
					paintControlPixel(e.gc, centerX + diffX, cursorY);
					int centerY = ((conf.height * conf.tileRows) / 2);
					int diffY = centerY - cursorY - 1;
					paintControlPixel(e.gc, cursorX, centerY + diffY);
					paintControlPixel(e.gc, centerX + diffX, centerY + diffY);
					break;
				}
				}
			}

			if (conf.isPixelGridEnabled()) {
				paintControlPixelGrid(e.gc);
			}
			if (conf.isSeparatorEnabled()) {
				paintControlSeparator(e.gc);
			}
			if (conf.isTileGridEnabled()) {
				paintControlTileGrid(e.gc);
			}

			if (conf.isTileSubGridEnabled()) {
				paintControlTileSubGrid(e.gc);
			}

			paintControlSelection(e.gc);

			if (conf.isTileCursorEnabled()) {
				paintControlTileCursor(e.gc, mouseIn, updateCursorLocation);
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
			gc.fillRectangle(tilelocation.x * conf.width * conf.pixelSize * conf.tileColumns,
					tilelocation.y * conf.height * conf.pixelSize * conf.tileRows,
					conf.width * conf.pixelSize * conf.tileColumns, conf.height * conf.pixelSize * conf.tileRows);
		}
	}

	public void paintControlTileCursor(GC gc, boolean mouseIn, boolean updateCursorLocation) {

		if (mouseIn) {
			gc.setAlpha(150);
			gc.setBackground(Constants.RED);
			gc.fillRectangle(tileX * conf.width * conf.pixelSize * conf.tileColumns,
					tileY * conf.height * conf.pixelSize * conf.tileRows,
					conf.width * conf.pixelSize * conf.tileColumns, conf.height * conf.pixelSize * conf.tileRows);
		}
		if (updateCursorLocation) {
			gc.setAlpha(255);
			gc.setLineWidth(3);
			gc.setForeground(Constants.LIGHT_GREEN2);
			gc.drawRectangle(tileX * conf.width * conf.pixelSize * conf.tileColumns,
					tileY * conf.height * conf.pixelSize * conf.tileRows,
					conf.width * conf.pixelSize * conf.tileColumns, conf.height * conf.pixelSize * conf.tileRows);
		}
	}

	public void paintControlPixelGrid(GC gc) {
		for (int x = 0; x <= conf.currentWidth * conf.tileColumns; x++) {
			for (int y = 0; y <= conf.height * conf.tileRows; y++) {
				gc.setForeground(Constants.PIXEL_GRID_COLOR);
				if (conf.gridStyle == GridStyle.Line) {
					gc.drawLine(x * conf.currentPixelWidth, 0, x * conf.currentPixelWidth,
							conf.height * conf.currentPixelHeight * conf.tileRows);
					gc.drawLine(0, y * conf.pixelSize, conf.width * conf.pixelSize * conf.tileColumns,
							y * conf.pixelSize);
				} else {
					gc.drawPoint(x * conf.currentPixelWidth, y * conf.currentPixelHeight);
				}
			}
		}
	}

	public void paintControlPixelCursor(GC gc, int x, int y) {
		gc.setBackground(Constants.RED);
		gc.setForeground(Constants.RED);
		gc.fillRectangle((cursorX * conf.currentPixelWidth) + 1 + (conf.currentPixelWidth / 2) - conf.pixelSize / 8,
				(cursorY * conf.pixelSize) + 1 + (conf.pixelSize / 2) - conf.pixelSize / 8, conf.pixelSize / 4,
				conf.pixelSize / 4);

	}

	public void paintControlSeparator(GC gc) {
		gc.setForeground(Constants.BYTE_SEPARATOR_COLOR);
		int step = (4 * (conf.isMultiColorEnabled() ? 1 : 2));
		for (int x = step; x < (conf.width * conf.tileColumns) / ((conf.isMultiColorEnabled() ? 2 : 1)); x += step) {
			gc.drawLine(x * conf.currentPixelWidth, 0, x * conf.currentPixelWidth,
					conf.height * conf.tileRows * conf.pixelSize);
		}
	}

	public void paintControlTileSubGrid(GC gc) {
		gc.setForeground(Constants.TILE_SUB_GRID_COLOR);
		for (int y = conf.height; y < conf.height * conf.tileRows; y += conf.height) {
			gc.drawLine(0, y * conf.pixelSize, conf.width * conf.tileColumns * conf.pixelSize, y * conf.pixelSize);
		}
		gc.setForeground(Constants.TILE_SUB_GRID_COLOR);
		for (int x = conf.currentWidth; x < conf.currentWidth * conf.tileColumns; x += conf.currentWidth) {
			gc.drawLine(x * conf.currentPixelWidth, 0, x * conf.currentPixelWidth,
					conf.height * conf.tileRows * conf.pixelSize);
		}
	}

	public void paintControlTileGrid(GC gc) {
		gc.setLineWidth(1);
		gc.setLineStyle(SWT.LINE_SOLID);
		gc.setForeground(Constants.TILE_GRID_COLOR);
		for (int x = 0; x < conf.columns; x++) {
			for (int y = 0; y < conf.rows; y++) {
				gc.drawRectangle(x * conf.width * conf.pixelSize * conf.tileColumns,
						y * conf.height * conf.pixelSize * conf.tileRows,
						conf.width * conf.pixelSize * conf.tileColumns, conf.height * conf.pixelSize * conf.tileRows);
			}
		}
	}

	private void paintControlTiles(GC gc) {
		for (int ty = 0; ty < conf.rows; ty++) {
			for (int tx = 0; tx < conf.columns; tx++) {
				paintControlTile(gc, tx, ty);
			}
		}
	}

	private void paintControlTile(GC gc, int tx, int ty) {
		int x = 0;
		int y = 0;
		int b1 = conf.bytesPerRow * conf.height;
		int b2 = b1 * conf.tileColumns;

		int byteOffset = 0;
		if (conf.widgetMode == WidgetMode.Painter || conf.widgetMode == WidgetMode.Viewer) {
			byteOffset = selectedTileOffset;
		} else if (conf.widgetMode == WidgetMode.Selector || conf.widgetMode == WidgetMode.BitmapViewer) {
			byteOffset = conf.computeTileOffset(tx, ty, navigationOffset);
		}

		for (int i = byteOffset, k = 0; i < (byteOffset + conf.getTileSize()); i++, k++) {

			int xi = (k % conf.bytesPerRow) * (8 / (conf.isMultiColorEnabled() ? 2 : 1));
			int xo = (k / b1) % conf.tileColumns;
			x = xi + (xo * conf.currentWidth) + (tx * conf.currentWidth * conf.tileColumns);

			int yi = (k / conf.bytesPerRow) % conf.height;
			int yo = (k / b2) % conf.tileRows;
			y = yi + (yo * conf.height) + (ty * conf.height * conf.tileRows);

			if (i < bitplane.length) {
				int b = (bitplane[i] & 0xff);
				if (conf.isMultiColorEnabled()) {
					for (int j = 6; j >= 0; j -= 2) {
						int bi = b;
						int colorIndex = (bi >> j) & 3;
						Color color = palette != null ? palette.get(String.valueOf(colorIndex)) : null;
						if (colorProvider != null) {
							color = colorProvider.getColorByIndex((byte) colorIndex, bitplane, tx, ty, conf.columns);
						}

						gc.setBackground(color);
						int pix = conf.isPixelGridEnabled() ? 1 : 0;
						gc.fillRectangle((x * conf.currentPixelWidth) + pix, (y * conf.currentPixelHeight) + pix,
								conf.currentPixelWidth - pix, conf.currentPixelHeight - pix);
						x++;
					}
				} else {
					for (int j = 128; j > 0; j >>= 1) {
						gc.setBackground((b & j) == j ? palette.get(String.valueOf(selectedColorIndex))
								: Constants.BITMAP_BACKGROUND_COLOR);
						int pix = conf.isPixelGridEnabled() ? 1 : 0;

						gc.fillRectangle((x * conf.currentPixelWidth) + pix, (y * conf.currentPixelHeight) + pix,
								conf.currentPixelWidth - pix, conf.currentPixelHeight - pix);
						x++;
					}
				}
			}
		}
	}

	private void paintControlPixel(GC gc, int x, int y) {

		if (conf.widgetMode == WidgetMode.Painter) {
			if (x < conf.currentWidth * conf.tileColumns && y < conf.height * conf.tileRows) {
				int ix = x % conf.currentWidth;
				int iy = y % conf.height;
				int ax = (x / conf.currentWidth);
				int ay = (y / conf.height) * conf.tileColumns;
				int offset = (ax + ay) * (conf.height * conf.bytesPerRow);
				if (conf.isMultiColorEnabled()) {
					int index = (((iy * conf.currentWidth) + ix) >> 2) + offset;
					ix &= 3;
					int mask = (3 << ((3 - ix) * 2) ^ 0xff) & 0xff;
					byte byteMask = (byte) ((bitplane[index + getSelectedTileOffset()] & mask));
					byteMask |= selectedColorIndex << ((3 - ix) * 2);
					bitplane[index + getSelectedTileOffset()] = byteMask;

				} else {
					int index = (((iy * conf.currentWidth) + ix) >> 3) + offset;
					byte byteMask = bitplane[index + getSelectedTileOffset()];
					int pixelMask = (1 << (7 - (ix % 8)) & 0xff);
					bitplane[index + getSelectedTileOffset()] = conf.pencilMode == PencilMode.Draw
							? (byte) (byteMask | pixelMask) : (byte) (byteMask & ((pixelMask ^ 0xff) & 0xff));
				}
			}
		}

		gc.setBackground(conf.pencilMode == PencilMode.Draw ? palette.get(String.valueOf(selectedColorIndex))
				: Constants.BITMAP_BACKGROUND_COLOR);

		int pix = conf.isPixelGridEnabled() ? 1 : 0;
		gc.fillRectangle((x * conf.currentPixelWidth) + pix, (y * conf.currentPixelHeight) + pix,
				conf.currentPixelWidth - pix, conf.currentPixelHeight - pix);
	}

	public void setColorProvider(IColorProvider colorProvider) {
		this.colorProvider = colorProvider;
		conf.setMultiColorEnabled(colorProvider.isMultiColorEnabled());
	}

	public void setBitlane(byte bitplane[]) {
		this.bitplane = bitplane;
	}

	public void recalc() {
		conf.currentPixelWidth = conf.pixelSize * (conf.isMultiColorEnabled() ? 2 : 1);
		conf.currentWidth = conf.width / (conf.isMultiColorEnabled() ? 2 : 1);
		int selectedTileOffset = conf.computeTileOffset(selectedTileIndexX, selectedTileIndexY, navigationOffset);
		if (vBar != null) {
			vBar.setMinimum(0);
			vBar.setMaximum(conf.rows);
		}
		fireSetSelectedTileOffset(selectedTileOffset);
		doDrawAllTiles();
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

	public boolean isLayerViewEnabled() {
		return layerCount > 0 && conf.layerViewEnabled;
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
		conf.pencilMode = pencilMode;
		cursorX = x + (selectedTileIndexX * conf.width * conf.tileColumns);
		cursorY = y + (selectedTileIndexY * conf.height * conf.tileRows);
		doDrawPixel();
	}

	protected void doDrawPixel() {
		paintControlMode = SET_DRAW_PIXEL;
		int inset = conf.isPixelGridEnabled() ? 1 : 0;

		switch (conf.paintMode) {
		case Simple: {
			redraw((cursorX * conf.currentPixelWidth) + inset, (cursorY * conf.currentPixelHeight) + inset,
					conf.currentPixelWidth - inset, conf.currentPixelHeight - inset, true);
			break;
		}
		case VerticalMirror: {
			redraw((cursorX * conf.currentPixelWidth) + inset, (cursorY * conf.currentPixelHeight) + inset,
					conf.currentPixelWidth - inset, conf.currentPixelHeight - inset, true);
			int centerX = ((conf.currentWidth * conf.tileColumns) / 2);
			int diff = centerX - cursorX - 1;
			redraw(((centerX + diff) * conf.currentPixelWidth) + inset, (cursorY * conf.currentPixelHeight) + inset,
					conf.currentPixelWidth - inset, conf.currentPixelHeight - inset, true);
			break;
		}
		case HorizontalMirror: {
			redraw((cursorX * conf.currentPixelWidth) + inset, (cursorY * conf.currentPixelHeight) + inset,
					conf.currentPixelWidth - inset, conf.currentPixelHeight - inset, true);
			int centerY = ((conf.height * conf.tileRows) / 2);
			int diff = centerY - cursorY - 1;
			redraw((cursorX * conf.currentPixelWidth) + inset, ((centerY + diff) * conf.currentPixelHeight) + inset,
					conf.currentPixelWidth - inset, conf.currentPixelHeight - inset, true);
			break;
		}
		case Kaleidoscope: {
			redraw((cursorX * conf.currentPixelWidth) + inset, (cursorY * conf.currentPixelHeight) + inset,
					conf.currentPixelWidth - inset, conf.currentPixelHeight - inset, true);
			int centerX = ((conf.currentWidth * conf.tileColumns) / 2);
			int diffX = centerX - cursorX - 1;
			redraw(((centerX + diffX) * conf.currentPixelWidth) + inset, (cursorY * conf.currentPixelHeight) + inset,
					conf.currentPixelWidth - inset, conf.currentPixelHeight - inset, true);
			int centerY = ((conf.height * conf.tileRows) / 2);
			int diffY = centerY - cursorY - 1;
			redraw((cursorX * conf.currentPixelWidth) + inset, ((centerY + diffY) * conf.currentPixelHeight) + inset,
					conf.currentPixelWidth - inset, conf.currentPixelHeight - inset, true);
			redraw(((centerX + diffX) * conf.currentPixelWidth) + inset,
					((centerY + diffY) * conf.currentPixelHeight) + inset, conf.currentPixelWidth - inset,
					conf.currentPixelHeight - inset, true);
			break;
		}
		}
	}

	@Override
	public void doDrawTile() {
		paintControlMode = SET_DRAW_TILE;
		redraw(selectedTileIndexX * conf.width * conf.pixelSize * conf.tileColumns,
				selectedTileIndexY * conf.height * conf.pixelSize * conf.tileRows,
				conf.width * conf.pixelSize * conf.tileColumns, conf.height * conf.pixelSize * conf.tileRows, true);
	}

	@Override
	public void doDrawAllTiles() {
		paintControlMode = SET_DRAW_ALL_TILES;
		// setNotification(selectedTileOffset, conf.getTileSize());
		redraw();
	}

	protected void setHasTileSelection(int count) {

	}

	public void setServiceValue(ImagingServiceDescription serviceDescription, int action, Object data) {
		IImagingService service = getService(serviceDescription);
		service.setValue(action, data);
	}

	public void executeService(ImagingServiceDescription serviceDescription) {
		executeService(serviceDescription, 0);
	}

	public void executeService(ImagingServiceDescription serviceDescription, int action) {
		IImagingService service = getService(serviceDescription);
		((AbstractService) service).setSource(this);
		((AbstractService) service).setConf(conf);
		((AbstractService) service).setCallback(this);
		((AbstractService) service).setNavigationOffset(navigationOffset);
		service.runService(action, tileSelectionList, navigationOffset, bitplane);
	}

	@Override
	public void afterRunService() {
		tileX = oldTileX;
		tileY = oldTileY;
		updateCursorLocation = false;
		doDrawAllTiles();
		fireDoDrawAllTiles();

	}

	@Override
	public void beforeRunService() {
		oldTileX = tileX;
		oldTileY = tileY;
	}

	@Override
	public void onRunService(int offset, int x, int y, boolean updateCursorLocation) {
		tileX = x;
		tileY = y;
		this.updateCursorLocation = updateCursorLocation;
		fireSetSelectedTileOffset(offset);
		doDrawAllTiles();
	}

	private boolean isTileSelected(int x, int y) {
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
				(conf.currentWidth * conf.currentPixelWidth * conf.tileColumns * conf.columns)
						+ (conf.cursorLineWidth * (conf.columns + 1)) + vsb.x - conf.columns,
				(conf.height * conf.currentPixelHeight * conf.tileRows * conf.rows)
						+ (conf.cursorLineWidth * (conf.rows + 1)) + hsb.x - conf.rows);
	}

	private IImagingService getService(ImagingServiceDescription s) {
		IImagingService service = serviceCacheMap.get(s.getName());
		if (service == null) {
			try {
				service = (IImagingService) s.getCls().newInstance();
				serviceCacheMap.put(s.getName(), service);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return service;
	}

}