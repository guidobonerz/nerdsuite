package de.drazil.nerdsuite.widget;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.enums.CursorMode;
import de.drazil.nerdsuite.enums.GridType;
import de.drazil.nerdsuite.enums.PencilMode;
import de.drazil.nerdsuite.enums.RedrawMode;
import de.drazil.nerdsuite.enums.TileSelectionModes;
import de.drazil.nerdsuite.imaging.service.IServiceCallback;
import de.drazil.nerdsuite.imaging.service.ITileBulkModificationListener;
import de.drazil.nerdsuite.imaging.service.ITileManagementListener;
import de.drazil.nerdsuite.imaging.service.ITileUpdateListener;
import de.drazil.nerdsuite.imaging.service.PaintTileService;
import de.drazil.nerdsuite.imaging.service.ServiceFactory;
import de.drazil.nerdsuite.imaging.service.TileRepositoryService;
import lombok.Getter;

public abstract class BaseImagingWidget extends BaseWidget implements IDrawListener, PaintListener, IServiceCallback,
		ITileUpdateListener, ITileManagementListener, ITileListener, ITileBulkModificationListener {

	@Getter
	protected ImagingWidgetConfiguration conf = null;
	private boolean keyPressed = false;
	private int currentKeyCodePressed = 0;
	private char currentCharacterPressed = 0;
	private boolean altS = false;

	protected int selectedTileIndexX = 0;
	protected int selectedTileIndexY = 0;
	protected int selectedTileIndex = 0;

	private int selectedPixelRangeX = 0;
	private int selectedPixelRangeY = 0;
	private int selectedPixelRangeX2 = 0;
	private int selectedPixelRangeY2 = 0;
	private boolean rangeSelectionStarted = false;

	protected int oldCursorX = 0;
	protected int oldCursorY = 0;
	protected int cursorX = 0;
	protected int cursorY = 0;
	protected int oldTileX = 0;
	protected int oldTileY = 0;
	protected int tileX = 0;
	protected int tileY = 0;
	protected int tileCursorX = 0;
	protected int tileCursorY = 0;
	private int animationIndex;

	private boolean updateCursorLocation = false;

	private RedrawMode redrawMode = RedrawMode.DrawNothing;

	private boolean mouseIn = false;

	private List<IDrawListener> drawListenerList = null;
	protected PaintTileService paintTileService;
	protected TileRepositoryService tileRepositoryService;

	protected Tile tile = null;

	private IColorPaletteProvider colorPaletteProvider;

	public BaseImagingWidget(Composite parent, int style) {
		super(parent, style);
		conf = new ImagingWidgetConfiguration();
	}

	public void init(String owner, IColorPaletteProvider colorPaletteProvider) {
		conf.setServiceOwner(owner);

		this.colorPaletteProvider = colorPaletteProvider;

		drawListenerList = new ArrayList<>();
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

	protected void leftMouseButtonClicked(int modifierMask, int x, int y) {
	}

	@Override
	protected void leftMouseButtonClickedInternal(int modifierMask, int x, int y) {
		computeCursorPosition(x, y);
		leftMouseButtonClicked(modifierMask, x, y);
	}

	protected void mouseMove(int modifierMask, int x, int y) {
	}

	@Override
	protected void mouseMoveInternal(int modifierMask, int x, int y) {
		computeCursorPosition(x, y);
		mouseMove(modifierMask, x, y);
	}

	protected void mouseExit(int modifierMask, int x, int y) {
	}

	@Override
	protected void mouseExitInternal(int modifierMask, int x, int y) {
		mouseIn = false;
		mouseExit(modifierMask, x, y);

		if (supportsPainting()) {
			doDrawTile();
		} else {
			doDrawAllTiles();
		}
	}

	protected void mouseEnter(int modifierMask, int x, int y) {
	}

	@Override
	protected void mouseEnterInternal(int modifierMask, int x, int y) {
		mouseIn = true;
		mouseEnter(modifierMask, x, y);
		setFocus();

		if (supportsPainting()) {
			doDrawTile();
		} else {
			doDrawAllTiles();
		}
	}

	protected void mouseDragged(int modifierMask, int x, int y) {
	}

	@Override
	protected void mouseDraggedInternal(int modifierMask, int x, int y) {
		computeCursorPosition(x, y);
		mouseDragged(modifierMask, x, y);
		if (supportsRangeSelection() && conf.cursorMode == CursorMode.SelectRectangle) {
			computeRangeSelection(tileCursorX, tileCursorY, 1, (modifierMask & SWT.SHIFT) == SWT.SHIFT);
			doDrawTile();
		}
	}

	protected void leftMouseButtonReleased(int modifierMask, int x, int y) {
	}

	@Override
	protected void leftMouseButtonReleasedInternal(int modifierMask, int x, int y) {
		computeCursorPosition(x, y);
		leftMouseButtonReleased(modifierMask, x, y);
		if (supportsRangeSelection() && conf.cursorMode == CursorMode.SelectRectangle) {
			if (rangeSelectionStarted) {
				rangeSelectionStarted = false;
				computeRangeSelection(tileCursorX, tileCursorY, 2, (modifierMask & SWT.SHIFT) == SWT.SHIFT);
			}
		}
	}

	protected void leftMouseButtonPressed(int modifierMask, int x, int y) {
	}

	@Override
	protected void leftMouseButtonPressedInternal(int modifierMask, int x, int y) {
		computeCursorPosition(x, y);
		leftMouseButtonPressed(modifierMask, x, y);

		if (supportsRangeSelection() && conf.cursorMode == CursorMode.SelectRectangle) {
			computeRangeSelection(tileCursorX, tileCursorY, 0, false);
			rangeSelectionStarted = false;
			doDrawTile();
		}
	}

	private void computeRangeSelection(int tileCursorX, int tileCursorY, int mode, boolean enabledSquareSelection) {
		int x = tileCursorX < 0 ? 0 : tileCursorX;
		int y = tileCursorY < 0 ? 0 : tileCursorY;

		if (mode == 0) {
			selectedPixelRangeX = 0;
			selectedPixelRangeY = 0;
			selectedPixelRangeX2 = 0;
			selectedPixelRangeY2 = 0;
			System.out.println("reset");
		} else if (mode == 1) {
			if (!rangeSelectionStarted) {
				selectedPixelRangeX = x;
				selectedPixelRangeY = y;
				rangeSelectionStarted = true;
			} else {

				selectedPixelRangeX2 = enabledSquareSelection && y - selectedPixelRangeY > x - selectedPixelRangeX
						? selectedPixelRangeX + (selectedPixelRangeY2 - selectedPixelRangeY)
						: x;

				selectedPixelRangeY2 = enabledSquareSelection && x - selectedPixelRangeX > y - selectedPixelRangeY
						? selectedPixelRangeY + (selectedPixelRangeX2 - selectedPixelRangeX)
						: y;
			}

		} else if (mode == 2) {
			int x1 = selectedPixelRangeX;
			int x2 = selectedPixelRangeX2;
			int y1 = selectedPixelRangeY;
			int y2 = selectedPixelRangeY2;
			if (x1 > x2) {
				int v = x1;
				x1 = x2;
				x2 = v;
			}

			if (y1 > y2) {
				int v = y1;
				y1 = y2;
				y2 = v;
			}
			tileRepositoryService.setSelection(new Rectangle(x1, y1, x2 - x1 + 1, y2 - y1 + 1));
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
		} else if (redrawMode == RedrawMode.DrawTile) {// || (redrawMode == RedrawMode.DrawAllTiles &&
														// supportsPainting())) {
			paintTileService.paintTile(gc, tileRepositoryService.getSelectedTile(), conf, colorPaletteProvider);
		} else if (redrawMode == RedrawMode.DrawAllTiles) {
			// paintTileService.paintTile(gc, tileRepositoryService.getSelectedTile(), conf,
			// colorPaletteProvider);
			paintTileService.paintAllTiles(this, gc, conf, colorPaletteProvider);
		} else if (redrawMode == RedrawMode.DrawSelectedTiles) {
			paintTileService.paintSelectedTiles(this, gc, conf, colorPaletteProvider);
		} else if (redrawMode == RedrawMode.DrawIndexed) {
			paintTileService.paintTile(this, gc, animationIndex, conf, colorPaletteProvider);
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

		if (!supportsPainting()) {
			paintSelection(gc);
		}

		if (paintTileCursor) {
			paintTileCursor(gc, mouseIn, updateCursorLocation);
		}

		if (supportsRangeSelection() && conf.cursorMode == CursorMode.SelectRectangle) {
			paintRangeSelection(gc);
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

	private void paintRangeSelection(GC gc) {
		gc.setForeground(Constants.BRIGHT_ORANGE);
		gc.setLineWidth(2);
		gc.setLineStyle(SWT.LINE_DASH);

		int x1 = selectedPixelRangeX;
		int x2 = selectedPixelRangeX2;
		int y1 = selectedPixelRangeY;
		int y2 = selectedPixelRangeY2;

		if (!(x1 == 0 && x2 == 0 && y1 == 0 && y2 == 0)) {
			if (x1 > x2) {
				int v = x1;
				x1 = x2;
				x2 = v;
			}

			if (y1 > y2) {
				int v = y1;
				y1 = y2;
				y2 = v;
			}

			gc.drawRectangle(x1 * conf.getPixelSize(), y1 * conf.getPixelSize(),
					(x2 - x1) * conf.getPixelSize() + conf.getPixelSize(),
					(y2 - y1) * conf.getPixelSize() + conf.getPixelSize());
		}
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
		selectedTileIndexList.forEach(i -> {
			int y = i / conf.getColumns();
			int x = i % conf.getColumns();
			gc.fillRectangle(x * conf.scaledTileWidth, y * conf.scaledTileHeight, conf.scaledTileWidth,
					conf.scaledTileHeight);
		});
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
	public void doDrawSelectedTiles() {
		redrawMode = RedrawMode.DrawSelectedTiles;
		redraw();
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		int width = (conf.width * conf.currentPixelWidth * conf.tileColumns * conf.columns);
		int height = (conf.height * conf.currentPixelHeight * conf.tileRows * conf.rows);
		return new Point(width, height);
	}

	public void setCursorMode(CursorMode cursorMode) {
		conf.setCursorMode(cursorMode);
		if (cursorMode == CursorMode.Point) {
			tileRepositoryService
					.setSelection(new Rectangle(0, 0, conf.getWidth() * conf.getTileColumns() * conf.getColumns(),
							conf.getHeight() * conf.getRows() * conf.getTileRows()));
		}
		fireDoDrawAllTiles(this);
		doDrawAllTiles();
	}

	protected boolean supportsPainting() {
		return conf.supportsPainting;
	}

	protected boolean supportsSingleSelection() {
		return (conf.tileSelectionModes & TileSelectionModes.SINGLE) == TileSelectionModes.SINGLE;
	}

	protected boolean supportsMultiSelection() {
		return (conf.tileSelectionModes & TileSelectionModes.MULTI) == TileSelectionModes.MULTI;
	}

	protected boolean supportsRangeSelection() {
		return (conf.tileSelectionModes & TileSelectionModes.RANGE) == TileSelectionModes.RANGE;
	}

	public void addDrawListener(IDrawListener redrawListener) {
		drawListenerList.add(redrawListener);
	}

	public void removeDrawListener(IDrawListener redrawListener) {
		drawListenerList.remove(redrawListener);
	}

	protected void fireDoDrawTile(BaseImagingWidget source) {
		drawListenerList.forEach(l -> l.doDrawTile());
	}

	protected void fireDoDrawAllTiles(BaseImagingWidget source) {
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
		// tileSelected(tile);
		// tileIndexesSelected(tileRepositoryService.get);
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
	public void updateTiles(List<Integer> selectedTileIndexList, UpdateMode updateMode) {
		if (updateMode == UpdateMode.Single && supportsPainting()) {
			Tile tile = tileRepositoryService.getTile(selectedTileIndexList.get(0));
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
				// computeTileSelection(false, (modifierMask & SWT.CTRL) == SWT.CTRL);
				computeTileSelection(tileX, tileY, 1);
			}
			tile.addTileListener(this);
			doDrawTile();
		} else if (updateMode == UpdateMode.Selection && (supportsSingleSelection() || supportsMultiSelection())) {
			doDrawSelectedTiles();
		} else if (updateMode == UpdateMode.All) {
			doDrawAllTiles();
		} else if (updateMode == UpdateMode.Animation) {
			animationIndex = selectedTileIndexList.get(0);
			tileRepositoryService.setSelectedTileIndex(animationIndex);
			redrawMode = RedrawMode.DrawIndexed;
			redraw();
		}
	}

	@Override
	public void tileChanged() {
		conf.setMultiColorEnabled(tile.isMulticolor());
		doDrawTile();
	}

	@Override
	public void tilesChanged(List<Integer> selectedTileIndexList) {
		doDrawSelectedTiles();
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