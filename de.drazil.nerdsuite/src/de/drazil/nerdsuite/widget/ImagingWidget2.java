package de.drazil.nerdsuite.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import de.drazil.nerdsuite.constants.GridStyle;
import de.drazil.nerdsuite.constants.PencilMode;
import de.drazil.nerdsuite.imaging.service.IImagingService;
import de.drazil.nerdsuite.imaging.service.ITileManagementListener;
import de.drazil.nerdsuite.imaging.service.ITileSelectionListener;
import de.drazil.nerdsuite.imaging.service.PaintTileService;
import de.drazil.nerdsuite.imaging.service.ServiceFactory;
import de.drazil.nerdsuite.log.Console;
import de.drazil.nerdsuite.model.TileLocation;

public class ImagingWidget2 extends BaseImagingWidget
		implements IDrawListener, PaintListener, IImagingCallback, ITileSelectionListener, ITileManagementListener {

	private final static int DRAW_NOTHING = 0;
	private final static int DRAW_ALL_TILES = 1;
	private final static int DRAW_TILE = 2;
	private final static int DRAW_PIXEL = 4;

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

	private int selectedColorIndex;
	private int selectedTileOffset = 0;
	private int monoColorDefaultIndex;
	private int navigationOffset = 0;
	private int colorCount;

	private int paintControlMode = DRAW_NOTHING;

	private boolean mouseIn = false;

	private IColorProvider colorProvider;
	private ScrollBar hBar = null;
	private ScrollBar vBar = null;
	private List<IDrawListener> drawListenerList = null;
	private List<TileLocation> tileSelectionList = null;
	private List<TileLocation> selectionRangeBuffer = null;

	private Map<String, IImagingService> serviceCacheMap = null;

	private Tile tile = null;

	public ImagingWidget2(Composite parent, int style) {
		this(parent, style, null);
	}

	public ImagingWidget2(Composite parent, int style, ImagingWidgetConfiguration configuration) {
		super(parent, style, configuration);

		serviceCacheMap = new HashMap<>();

		selectionRangeBuffer = new ArrayList<>();
		tileSelectionList = new ArrayList<>();
		drawListenerList = new ArrayList<IDrawListener>();

		hBar = getHorizontalBar();
		vBar = getVerticalBar();

		setBackground(Constants.BLACK);

		addPaintListener(this);
		parent.getDisplay().getActiveShell().addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event event) {
				doDrawAllTiles();
			}
		});

	}

	public void setTile(Tile tile) {
		this.tile = tile;
		redraw();
	}

	@Override
	public void rightMouseButtonClicked(int modifierMask, int x, int y) {
		if (supportsPainting()) {
			conf.pencilMode = conf.pencilMode == PencilMode.Draw ? PencilMode.Erase : PencilMode.Draw;
			Console.println("PencilMode:" + conf.pencilMode);
		}
	}

	@Override
	public void leftMouseButtonClicked(int modifierMask, int x, int y) {
		computeCursorPosition(x, y);
		if (supportsSingleSelection() || supportsMultiSelection()) {
			selectedTileIndexX = tileX;
			selectedTileIndexY = tileY;
			selectedTileIndex = (tileY * conf.columns) + tileX;
			// fireSetSelectedTile(ImagingWidget.this, tile);
			// computeSelection(false, false);
			doDrawAllTiles();
		} else if (supportsPainting()) {
			ServiceFactory.getService(PaintTileService.class).setPixel(tile, cursorX, cursorY, conf);
			doDrawTile();
			// fireDoDrawTile(ImagingWidget.this);
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
		if (supportsPainting()) {
			if (oldCursorX != cursorX || oldCursorY != cursorY) {
				oldCursorX = cursorX;
				oldCursorY = cursorY;
				ServiceFactory.getService(PaintTileService.class).setPixel(tile, cursorX, cursorY, conf);
				doDrawTile();
				// fireDoDrawTile(ImagingWidget.this);
			}
		} else if (supportsMultiSelection()) {
			// computeSelection(false, false);
			doDrawAllTiles();
		}
	}

	@Override
	public void leftMouseButtonPressed(int modifierMask, int x, int y) {
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

	protected void computeCursorPosition(int x, int y) {
		System.out.println(conf.widgetName);
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

	private boolean checkKeyPressed(int modifierKey, char charCode) {
		return (modifierMask & modifierKey) == modifierKey && currentCharacterPressed == charCode && keyPressed;
	}

	private boolean checkPaintControlMode(int mode) {
		return (paintControlMode & mode) == mode;
	}

	public void paintControl(PaintEvent e) {
		paintControl(e.gc, paintControlMode, conf.isPixelGridEnabled(), conf.isSeparatorEnabled(),
				conf.isTileGridEnabled(), conf.isTileSubGridEnabled(), true, conf.isTileCursorEnabled(), false);
	}

	private void paintControl(GC gc, int paintControlMode, boolean paintPixelGrid, boolean paintSeparator,
			boolean paintTileGrid, boolean paintTileSubGrid, boolean paintSelection, boolean paintTileCursor,
			boolean paintTelevisionMode) {

		if (checkPaintControlMode(DRAW_TILE)) {
			ServiceFactory.getService(this, PaintTileService.class).paintTile(gc, tile, conf);
		}
		if (checkPaintControlMode(DRAW_ALL_TILES)) {
			ServiceFactory.getService(this, PaintTileService.class).paintAllTiles(gc, conf);
		}

		if (paintPixelGrid) {
			paintPixelGrid(gc);
		}
		/*
		 * if (paintSeparator) { paintSeparator(gc); } if (paintTileGrid) {
		 * paintTileGrid(gc); }
		 * 
		 * if (paintTileSubGrid) { paintTileSubGrid(gc); }
		 * 
		 * paintSelection(gc);
		 * 
		 * if (paintTileCursor) { paintTileCursor(gc, mouseIn, updateCursorLocation); }
		 * 
		 * if (paintTelevisionMode) { paintTelevisionRaster(gc); }
		 */
		/*
		 * if (supportsDrawCursor()) { paintPixelCursor(gc); }
		 */
		paintControlMode = DRAW_NOTHING;
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
			gc.fillRectangle(tilelocation.x * conf.width * conf.pixelSize * conf.tileColumns,
					tilelocation.y * conf.height * conf.pixelSize * conf.tileRows,
					conf.width * conf.pixelSize * conf.tileColumns, conf.height * conf.pixelSize * conf.tileRows);
		}
	}

	private void paintTileCursor(GC gc, boolean mouseIn, boolean updateCursorLocation) {

		if (mouseIn) {
			gc.setAlpha(40);
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

	private void paintPixelGrid(GC gc) {
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
		for (int x = step; x < (conf.width * conf.tileColumns) / bc; x += step) {
			gc.drawLine(x * conf.currentPixelWidth, 0, x * conf.currentPixelWidth,
					conf.height * conf.tileRows * conf.pixelSize);
		}
	}

	private void paintTileSubGrid(GC gc) {
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

	private void paintTileGrid(GC gc) {
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

	public void setColorProvider(IColorProvider colorProvider) {
		this.colorProvider = colorProvider;
		conf.setPixelConfig(colorProvider.getPixelConfig());
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

	public void setSelectedColor(int index) {
		selectedColorIndex = index;
		monoColorDefaultIndex = index;
	}

	@Override
	public void doDrawPixel(BaseImagingWidget source, int x, int y, PencilMode pencilMode) {
		conf.pencilMode = pencilMode;
		cursorX = x + (selectedTileIndexX * conf.width * conf.tileColumns);
		cursorY = y + (selectedTileIndexY * conf.height * conf.tileRows);
		doDrawPixel();
	}

	protected void doDrawPixel() {
		paintControlMode = DRAW_PIXEL;
		int inset = conf.isPixelGridEnabled() ? 1 : 0;

		switch (conf.paintMode) {
		case Single: {
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
		paintControlMode = DRAW_TILE;
		redraw(selectedTileIndexX * conf.width * conf.pixelSize * conf.tileColumns,
				selectedTileIndexY * conf.height * conf.pixelSize * conf.tileRows,
				conf.width * conf.pixelSize * conf.tileColumns, conf.height * conf.pixelSize * conf.tileRows, true);
	}

	@Override
	public void doDrawAllTiles() {
		paintControlMode = DRAW_ALL_TILES;
		// setNotification(selectedTileOffset, conf.getTileSize());
		redraw();

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
		for (IDrawListener listener : drawListenerList) {
			listener.doDrawTile();
		}
	}

	private void fireDoDrawAllTiles(BaseImagingWidget source) {
		for (IDrawListener listener : drawListenerList) {
			listener.doDrawAllTiles();
		}
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
	public void tileAdded() {
		// TODO Auto-generated method stub

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
	public void tileSelected(Tile tile) {
		setTile(tile);
	}
}