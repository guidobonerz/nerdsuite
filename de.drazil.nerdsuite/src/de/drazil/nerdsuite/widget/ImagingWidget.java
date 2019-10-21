package de.drazil.nerdsuite.widget;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.enums.GridType;
import de.drazil.nerdsuite.enums.PencilMode;
import de.drazil.nerdsuite.enums.RedrawMode;
import de.drazil.nerdsuite.imaging.service.IServiceCallback;
import de.drazil.nerdsuite.imaging.service.ITileManagementListener;
import de.drazil.nerdsuite.imaging.service.ITileSelectionListener;
import de.drazil.nerdsuite.imaging.service.PaintTileService;
import de.drazil.nerdsuite.imaging.service.ServiceFactory;
import de.drazil.nerdsuite.imaging.service.TileRepositoryService;
import de.drazil.nerdsuite.model.TileLocation;

public class ImagingWidget extends BaseImagingWidget implements IDrawListener, PaintListener, IServiceCallback,
		ITileSelectionListener, ITileManagementListener, ITileListener {

	private boolean keyPressed = false;
	private int currentKeyCodePressed = 0;
	private char currentCharacterPressed = 0;
	private boolean altS = false;

	private int selectedTileIndexX = 0;
	private int selectedTileIndexY = 0;
	private int selectedTileIndex = 0;

	private int oldCursorX = 0;
	private int oldCursorY = 0;
	private int cursorX = 0;
	private int cursorY = 0;
	private int oldTileX = 0;
	private int oldTileY = 0;
	private int tileX = 0;
	private int tileY = 0;
	private int tileCursorX = 0;
	private int tileCursorY = 0;

	private boolean updateCursorLocation = false;

	private RedrawMode redrawMode = RedrawMode.DrawNothing;

	private boolean mouseIn = false;

	private ScrollBar hBar = null;
	private ScrollBar vBar = null;
	private List<IDrawListener> drawListenerList = null;
	private List<TileLocation> tileSelectionList = null;
	private List<TileLocation> selectionRangeBuffer = null;

	private PaintTileService paintTileService;
	private TileRepositoryService tileRepositoryService;

	private Tile tile = null;

	private IColorPaletteProvider colorPaletteProvider;

	public ImagingWidget(Composite parent, int style) {
		super(parent, style);
	}

	public void init(String owner, IColorPaletteProvider colorPaletteProvider) {
		conf.setServiceOwner(owner);

		this.colorPaletteProvider = colorPaletteProvider;
		selectionRangeBuffer = new ArrayList<>();
		tileSelectionList = new ArrayList<>();
		drawListenerList = new ArrayList<IDrawListener>();

		hBar = getHorizontalBar();
		vBar = getVerticalBar();

		tileRepositoryService = ServiceFactory.getService(conf.getServiceOwnerId(), TileRepositoryService.class);
		paintTileService = ServiceFactory.getService(conf.getServiceOwnerId(), PaintTileService.class);
		paintTileService.setTileRepositoryService(tileRepositoryService);
		paintTileService.setImagePainterFactory(tileRepositoryService.getImagePainterFactory());
		addPaintListener(this);
		getParent().getDisplay().getActiveShell().addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event event) {
				doDrawAllTiles();
			}
		});
	}

	@Override
	public void leftMouseButtonClicked(int modifierMask, int x, int y) {
		computeCursorPosition(x, y);
		if (supportsPainting()) {
			paintTileService.setPixel(tile, cursorX, cursorY, conf);
			doDrawPixel();
			fireDoDrawTile(ImagingWidget.this);
		} else if (supportsSingleSelection() || supportsMultiSelection()) {

			selectedTileIndexX = tileX;
			selectedTileIndexY = tileY;
			selectedTileIndex = computeTileIndex(tileX, tileY);
			computeSelection(false, (modifierMask & SWT.CTRL) == SWT.CTRL);
			if (selectedTileIndex < tileRepositoryService.getSize()) {
				tileRepositoryService.setSelectedTile(selectedTileIndex);
			} else {
				System.out.println("tile selection outside range...");
			}
			// fireSetSelectedTile(ImagingWidget.this, tile);

			doDrawAllTiles();
		}
	}

	@Override
	public void mouseMove(int modifierMask, int x, int y) {
		computeCursorPosition(x, y);
		if (supportsSingleSelection() || supportsMultiSelection()) {
			if (oldTileX != tileX || oldTileY != tileY) {
				oldTileX = tileX;
				oldTileY = tileY;
				doDrawAllTiles();
			}
		}
		// System.out.printf("%10s x:%2d y:%2d\n", conf.widgetName, tileCursorX,
		// tileCursorY);
	}

	@Override
	public void mouseExit(int modifierMask, int x, int y) {
		mouseIn = false;
		if (supportsPainting()) {
			doDrawTile();
		} else {
			doDrawAllTiles();
		}
	}

	@Override
	public void mouseEnter(int modifierMask, int x, int y) {
		setFocus();
		mouseIn = true;
		if (supportsPainting()) {
			doDrawTile();
		} else {
			doDrawAllTiles();
		}
	}

	@Override
	public void mouseDragged(int modifierMask, int x, int y) {
		computeCursorPosition(x, y);
		if (supportsPainting()) {
			if (oldCursorX != cursorX || oldCursorY != cursorY) {
				oldCursorX = cursorX;
				oldCursorY = cursorY;
				paintTileService.setPixel(tile, cursorX, cursorY, conf);
				doDrawPixel();
				fireDoDrawTile(ImagingWidget.this);
			}
		} else if (supportsMultiSelection()) {
			computeSelection(false, (modifierMask & SWT.CTRL) == SWT.CTRL);
			doDrawAllTiles();
		}
	}

	@Override
	public void leftMouseButtonReleased(int modifierMask, int x, int y) {
		computeCursorPosition(x, y);
		if (supportsMultiSelection() && tileSelectionList.size() > 1) {
			tileRepositoryService.setSelectedTiles(tileSelectionList);
		}
	}

	@Override
	public void leftMouseButtonPressed(int modifierMask, int x, int y) {
		computeCursorPosition(x, y);
		if (supportsMultiSelection() || supportsSingleSelection()) {
			// System.out.printf("tile x:%2d tile y:%2d\n", tileX, tileY);
		}
		if (supportsSingleSelection()) {
			resetSelectionList();
		}
	}

	public void selectAll() {
		if (supportsMultiSelection()) {
			resetSelectionList();
			// computeSelection(true, false);
			doDrawAllTiles();
		}
	}

	private boolean hasTile(int x, int y) {
		for (TileLocation tl : tileSelectionList) {
			if (tl.x == x && tl.y == y) {
				return true;
			}
		}
		return false;
	}

	private int computeTileIndex(int x, int y) {
		return (x + (y * conf.columns));
	}

	private void computeSelection(boolean selectAll, boolean addNewSelectionRange) {
		if (addNewSelectionRange) {
			System.out.println("add new selection range");
		}
		if (selectionRangeBuffer.isEmpty()) {
			if (selectAll) {
				// selectionRangeBuffer.add(new TileLocation(0, 0));
				// selectionRangeBuffer.add(new TileLocation(columns - 1, rows - 1));
			} else {
				selectionRangeBuffer.add(new TileLocation(tileX, tileY));
				selectionRangeBuffer.add(new TileLocation(tileX, tileY));
			}
		}
		if (!selectAll) {
			selectionRangeBuffer.get(1).x = tileX;
			selectionRangeBuffer.get(1).y = tileY;
		}
		int i1 = computeTileIndex(selectionRangeBuffer.get(0).x, selectionRangeBuffer.get(0).y);
		int i2 = computeTileIndex(selectionRangeBuffer.get(1).x, selectionRangeBuffer.get(1).y);
		int a = 0;
		int b = 1;

		if (i1 > i2) {
			a = 1;
			b = 0;
		}

		int xs = selectionRangeBuffer.get(a).x;
		int ys = selectionRangeBuffer.get(a).y;
		tileSelectionList = new ArrayList<>();
		for (;;) {
			if (xs < conf.columns) {
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
	}

	protected void computeCursorPosition(int x, int y) {
		cursorX = x / conf.currentPixelWidth;
		cursorY = y / conf.currentPixelHeight;
		// tileX = x / (conf.currentWidth * conf.currentPixelWidth * conf.tileColumns);
		// tileY = y / (conf.height * conf.currentPixelHeight * conf.tileRows);
		tileX = x / conf.getScaledTileWidth();
		tileY = y / conf.getScaledTileHeight();
		tileCursorX = (cursorX - (tileX * conf.width));
		tileCursorY = (cursorY - (tileY * conf.height));
	}

	private void resetSelectionList() {
		tileSelectionList = new ArrayList<>();
		selectionRangeBuffer = new ArrayList<>();
	}

	private boolean checkKeyPressed(int modifierKey, char charCode) {
		return (modifierMask & modifierKey) == modifierKey && currentCharacterPressed == charCode && keyPressed;
	}

	public void paintControl(PaintEvent e) {
		paintControl(e.gc, redrawMode, conf.isPixelGridEnabled(), conf.isSeparatorEnabled(), conf.isTileGridEnabled(),
				conf.isTileSubGridEnabled(), true, conf.isTileCursorEnabled(), true);
	}

	private void paintControl(GC gc, RedrawMode redrawMode, boolean paintPixelGrid, boolean paintSeparator,
			boolean paintTileGrid, boolean paintTileSubGrid, boolean paintSelection, boolean paintTileCursor,
			boolean paintTelevisionMode) {
		if (redrawMode == RedrawMode.DrawPixel) {
			paintTileService.paintPixel(gc, tileRepositoryService.getSelectedTile(), cursorX, cursorY, conf,
					colorPaletteProvider);
		} else if (redrawMode == RedrawMode.DrawTile) {
			paintTileService.paintTile(gc, tileRepositoryService.getSelectedTile(), conf, colorPaletteProvider);
		} else if (redrawMode == RedrawMode.DrawAllTiles) {
			if (supportsPainting()) {
				paintTileService.paintTile(gc, tileRepositoryService.getSelectedTile(), conf, colorPaletteProvider);
			} else {
				paintTileService.paintAllTiles(this, gc, conf, colorPaletteProvider);
			}
		}

		if (paintPixelGrid) {
			paintPixelGrid(gc);
		}

		if (paintSeparator) {
			paintSeparator(gc);
		}

		if (paintTileGrid) {
			paintTileGrid(gc);
		}

		if (paintTileSubGrid) {
			paintTileSubGrid(gc);
		}

		paintSelection(gc);

		if (paintTileCursor) {
			paintTileCursor(gc, mouseIn, updateCursorLocation);
		}
		/*
		 * if (paintTelevisionMode && supportsSingleSelection()) {
		 * paintTelevisionRaster(gc); }
		 */
		/*
		 * if (supportsDrawCursor()) { paintPixelCursor(gc); }
		 */
		redrawMode = RedrawMode.DrawNothing;

	}

	private void paintTelevisionRaster(GC gc) {
		int height = conf.height * conf.tileRows * conf.rows * conf.currentPixelHeight;
		int length = conf.width * conf.tileColumns * conf.columns * conf.currentPixelWidth;
		for (int y = 0; y < height; y += 2) {
			gc.setAlpha(60);
			gc.setForeground(Constants.BLACK);
			gc.drawLine(0, y, length, y);
		}
	}

	private void paintSelection(GC gc) {
		gc.setBackground(Constants.SELECTION_TILE_MARKER_COLOR);
		gc.setAlpha(150);
		for (TileLocation tilelocation : tileSelectionList) {
			gc.fillRectangle(tilelocation.x * conf.scaledTileWidth, tilelocation.y * conf.scaledTileHeight,
					conf.scaledTileWidth, conf.scaledTileHeight);
		}
	}

	private void paintTileCursor(GC gc, boolean mouseIn, boolean updateCursorLocation) {

		if (mouseIn) {
			gc.setAlpha(40);
			gc.setBackground(Constants.RED);
			gc.fillRectangle(tileX * conf.scaledTileWidth, tileY * conf.scaledTileHeight, conf.scaledTileWidth,
					conf.scaledTileHeight);
		}
		if (updateCursorLocation) {
			gc.setAlpha(255);
			gc.setLineWidth(3);
			gc.setForeground(Constants.LIGHT_GREEN2);
			gc.drawRectangle(tileX * conf.scaledTileWidth, tileY * conf.scaledTileHeight, conf.scaledTileWidth,
					conf.scaledTileHeight);
		}
	}

	private void paintPixelGrid(GC gc) {
		for (int x = 0; x <= conf.currentWidth * conf.tileColumns; x++) {
			for (int y = 0; y <= conf.height * conf.tileRows; y++) {
				gc.setForeground(Constants.PIXEL_GRID_COLOR);
				if (conf.gridStyle == GridType.Line) {
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

	private void paintPixelCursor(GC gc) {
		gc.setBackground(Constants.WHITE);
		gc.setForeground(Constants.WHITE);
		gc.fillRectangle((cursorX * conf.currentPixelWidth) + 1 + (conf.currentPixelWidth / 2) - conf.pixelSize / 8,
				(cursorY * conf.pixelSize) + 1 + (conf.pixelSize / 2) - conf.pixelSize / 8, conf.pixelSize / 4,
				conf.pixelSize / 4);
	}

	private void paintSeparator(GC gc) {
		gc.setForeground(Constants.BYTE_SEPARATOR_COLOR);
		int bc = conf.pixelConfig.bitCount;
		int step = (8 * bc);
		for (int x = step; x < (conf.scaledTileWidth) / bc; x += step) {
			gc.drawLine(x * conf.currentPixelWidth, 0, x * conf.currentPixelWidth, conf.scaledTileHeight);
		}
	}

	private void paintTileSubGrid(GC gc) {
		gc.setForeground(Constants.TILE_SUB_GRID_COLOR);
		for (int y = conf.height; y < conf.height * conf.tileRows; y += conf.height) {
			gc.drawLine(0, y * conf.pixelSize, conf.scaledTileWidth, y * conf.pixelSize);
		}
		gc.setForeground(Constants.TILE_SUB_GRID_COLOR);
		for (int x = conf.currentWidth; x < conf.currentWidth * conf.tileColumns; x += conf.currentWidth) {
			gc.drawLine(x * conf.currentPixelWidth, 0, x * conf.currentPixelWidth, conf.scaledTileHeight);
		}
	}

	private void paintTileGrid(GC gc) {
		gc.setLineWidth(1);
		gc.setLineStyle(SWT.LINE_SOLID);
		gc.setForeground(Constants.TILE_GRID_COLOR);
		for (int x = 0; x < conf.columns; x++) {
			for (int y = 0; y < conf.rows; y++) {
				gc.drawRectangle(x * conf.scaledTileWidth, y * conf.scaledTileHeight, conf.scaledTileWidth,
						conf.scaledTileHeight);
			}
		}
	}

	public void recalc() {
		int pixmul = conf.pixelConfig.pixmul;
		conf.currentPixelWidth = conf.pixelSize * pixmul;
		conf.currentWidth = conf.width / pixmul;
		// fireSetSelectedTile(ImagingWidget.this, tile);
		doDrawAllTiles();
		Composite composite = getParent();
		composite.layout();
	}

	@Override
	public void doDrawPixel(BaseImagingWidget source, int x, int y, PencilMode pencilMode) {
		conf.pencilMode = pencilMode;
		cursorX = x + (selectedTileIndexX * conf.width * conf.tileColumns);
		cursorY = y + (selectedTileIndexY * conf.height * conf.tileRows);
		doDrawPixel();
	}

	public void doDrawPixel() {
		redrawMode = RedrawMode.DrawPixel;
		redraw(tileCursorX * conf.pixelSize, tileCursorY * conf.pixelSize, conf.pixelSize, conf.pixelSize, true);
	}

	@Override
	public void doDrawTile() {
		redrawMode = RedrawMode.DrawTile;
		if (conf.supportsPainting) {
			redraw(selectedTileIndexX * conf.width * conf.pixelSize * conf.tileColumns,
					selectedTileIndexY * conf.height * conf.pixelSize * conf.tileRows,
					conf.width * conf.pixelSize * conf.tileColumns, conf.height * conf.pixelSize * conf.tileRows, true);
		} else {
			redraw(selectedTileIndexX * conf.scaledTileWidth, selectedTileIndexY * conf.scaledTileHeight,
					conf.scaledTileWidth, conf.scaledTileHeight, true);

		}
	}

	@Override
	public void doDrawAllTiles() {
		redrawMode = RedrawMode.DrawAllTiles;
		// setNotification(selectedTileOffset, conf.getTileSize());
		redraw();

	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		Point hsb = hBar != null ? hBar.getSize() : new Point(0, 0);
		Point vsb = vBar != null ? vBar.getSize() : new Point(0, 0);
		int width = (conf.currentWidth * conf.currentPixelWidth * conf.tileColumns * conf.columns)
				+ (conf.cursorLineWidth * (conf.columns + 1)) + vsb.x - conf.columns;

		int height = (conf.height * conf.currentPixelHeight * conf.tileRows * conf.rows)
				+ (conf.cursorLineWidth * (conf.rows + 1)) + hsb.x - conf.rows;

		return new Point(width, height);
	}

	protected boolean supportsPainting() {
		return conf.supportsPainting;
	}

	protected boolean supportsMultiTileView() {
		return conf.supportsMultiTileView;
	}

	protected boolean supportsSingleSelection() {
		return conf.supportsSingleSelection;
	}

	protected boolean supportsMultiSelection() {
		return conf.supportsMultiSelection;
	}

	protected boolean supportsReferenceIndexSelection() {
		return conf.supportsReferenceIndexSelection;
	}

	protected boolean supportsDrawCursor() {
		return conf.supportsDrawCursor;
	}

	public void addDrawListener(IDrawListener redrawListener) {
		drawListenerList.add(redrawListener);
	}

	public void removeDrawListener(IDrawListener redrawListener) {
		drawListenerList.remove(redrawListener);
	}

	private void fireDoDrawTile(BaseImagingWidget source) {
		drawListenerList.forEach(l -> l.doDrawTile());
	}

	private void fireDoDrawAllTiles(BaseImagingWidget source) {
		drawListenerList.forEach(l -> l.doDrawAllTiles());
	}

	@Override
	public void beforeRunService() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRunService(int offset, int x, int y, boolean updateCursorLocation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterRunService() {
		// TODO Auto-generated method stub

	}

	@Override
	public void tileAdded(Tile tile) {
		tileSelected(tile);
	}

	@Override
	public void tileRemoved() {
		// TODO Auto-generated method stub

	}

	@Override
	public void tileReordered() {
		// TODO Auto-generated method stub

	}

	@Override
	public void tilesSelected(List<TileLocation> tileLocationList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void tileSelected(Tile tile) {
		if (this.tile != null) {
			this.tile.removeTileListener(this);
		}
		this.tile = tile;
		if (!conf.supportsPainting) {
			selectedTileIndex = tileRepositoryService.getSelectedTileIndex();
			selectedTileIndexX = (selectedTileIndex % conf.getColumns());
			selectedTileIndexY = (selectedTileIndex / conf.getColumns());
			tileX = selectedTileIndexX;
			tileY = selectedTileIndexY;
			computeSelection(false, (modifierMask & SWT.CTRL) == SWT.CTRL);
		}
		tile.addTileListener(this);
		doDrawAllTiles();
	}

	@Override
	public void layerRemoved() {
		// TODO Auto-generated method stub

	}

	@Override
	public void layerAdded() {
		// TODO Auto-generated method stub

	}

	@Override
	public void activeLayerChanged(int layer) {
		doDrawAllTiles();
	}

	@Override
	public void layerContentChanged(int layer) {
		doDrawTile();

	}

	@Override
	public void layerReordered() {
		// TODO Auto-generated method stub

	}

	@Override
	public void layerVisibilityChanged(int layer) {
		redraw();
	}

}