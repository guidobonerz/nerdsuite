package de.drazil.nerdsuite.widget;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.enums.PencilMode;
import de.drazil.nerdsuite.enums.RedrawMode;
import de.drazil.nerdsuite.enums.TileSelectionModes;
import de.drazil.nerdsuite.imaging.service.IServiceCallback;
import de.drazil.nerdsuite.imaging.service.ITileBulkModificationListener;
import de.drazil.nerdsuite.imaging.service.ITileManagementListener;
import de.drazil.nerdsuite.imaging.service.ITileUpdateListener;
import de.drazil.nerdsuite.imaging.service.ImagePainterFactory;
import de.drazil.nerdsuite.imaging.service.ServiceFactory;
import de.drazil.nerdsuite.imaging.service.TileRepositoryService;
import de.drazil.nerdsuite.mouse.IMeasuringListener;
import de.drazil.nerdsuite.mouse.MeasuringController;
import lombok.Getter;

public abstract class BaseImagingWidget extends BaseWidget
		implements IDrawListener, PaintListener, IServiceCallback, ITileUpdateListener, ITileManagementListener,
		ITileListener, ITileBulkModificationListener, IMeasuringListener, IColorSelectionListener {

	@Getter
	protected ImagingWidgetConfiguration conf = null;
	private boolean keyPressed = false;
	private int currentKeyCodePressed = 0;
	private char currentCharacterPressed = 0;
	private boolean altS = false;

	protected int selectedTileIndexX = 0;
	protected int selectedTileIndexY = 0;
	protected int selectedTileIndex = 0;

	protected int tileGap = 0;
	protected int oldCursorX = -1;
	protected int oldCursorY = -1;
	protected int cursorX = 0;
	protected int cursorY = 0;
	protected int oldTileX = -1;
	protected int oldTileY = -1;
	protected int tileX = 0;
	protected int tileY = 0;
	protected int oldTileCursorX = -1;
	protected int oldTileCursorY = -1;
	protected int tileCursorX = 0;
	protected int tileCursorY = 0;
	protected int temporaryIndex;
	protected int action;

	protected boolean cursorChanged = false;
	protected boolean tileCursorChanged = false;
	protected boolean tileChanged = false;

	protected boolean updateCursorLocation = false;

	protected RedrawMode redrawMode = RedrawMode.DrawAllTiles;

	protected boolean mouseIn = false;

	private List<IDrawListener> drawListenerList = null;
	protected TileRepositoryService tileRepositoryService;

	protected Tile tile = null;
	protected Image image = null;
	protected MeasuringController mc;

	protected IColorPaletteProvider colorPaletteProvider;

	public BaseImagingWidget(Composite parent, int style) {
		super(parent, style);
		conf = new ImagingWidgetConfiguration();
		mc = new MeasuringController();
		mc.addMeasuringListener(this);
		tileGap = getTileGap();
	}

	protected abstract int getTileGap();

	protected int getCalculatedColumns() {
		return ((getParent().getBounds().width - 30) / conf.getScaledTileWidth());
	}

	public void init(String owner, IColorPaletteProvider colorPaletteProvider, final boolean autowrap) {
		conf.setServiceOwner(owner);

		this.colorPaletteProvider = colorPaletteProvider;

		drawListenerList = new ArrayList<>();
		tileRepositoryService = ServiceFactory.getService(conf.getServiceOwnerId(), TileRepositoryService.class);
		addPaintListener(this);
		getParent().getDisplay().getActiveShell().addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event event) {

				if (autowrap) {
					int c = (int) getCalculatedColumns();
					conf.setColumns(c == 0 ? 1 : c);
					conf.setRows(tileRepositoryService.getSize() / conf.getColumns()
							+ (tileRepositoryService.getSize() % conf.getColumns() == 0 ? 0 : 1));
					doRedraw(RedrawMode.DrawAllTiles, ImagePainterFactory.READ);
				}

			}
		});
	}

	public void setTriggerMillis(long... triggerMillis) {
		mc.setTriggerMillis(triggerMillis);
	}

	@Override
	public void onTriggerTimeReached(long triggerTime) {

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
	}

	protected void mouseEnter(int modifierMask, int x, int y) {
	}

	@Override
	protected void mouseEnterInternal(int modifierMask, int x, int y) {
		mouseIn = true;
		mouseEnter(modifierMask, x, y);
		// setFocus();
	}

	protected void mouseDragged(int modifierMask, int x, int y) {
	}

	@Override
	protected void mouseDraggedInternal(int modifierMask, int x, int y) {
		computeCursorPosition(x, y);
		mouseDragged(modifierMask, x, y);
	}

	protected void leftMouseButtonReleased(int modifierMask, int x, int y) {
	}

	@Override
	protected void leftMouseButtonReleasedInternal(int modifierMask, int x, int y) {
		computeCursorPosition(x, y);
		leftMouseButtonReleased(modifierMask, x, y);
	}

	protected void leftMouseButtonPressed(int modifierMask, int x, int y) {
	}

	@Override
	protected void leftMouseButtonPressedInternal(int modifierMask, int x, int y) {
		computeCursorPosition(x, y);
		leftMouseButtonPressed(modifierMask, x, y);
	}

	protected void mouseScrolled(int modifierMask, int x, int y, int count) {
	}

	@Override
	protected void mouseScrolledInternal(int modifierMask, int x, int y, int count) {
		computeCursorPosition(x, y);
		mouseScrolled(modifierMask, x, y, count);
	}

	protected void computeCursorPosition(int x, int y) {
		cursorX = x / conf.currentPixelWidth;
		cursorY = y / conf.currentPixelHeight;
		if (oldCursorX != cursorX || oldCursorY != cursorY) {
			oldCursorX = cursorX;
			oldCursorY = cursorY;
			cursorChanged = true;
		} else {
			cursorChanged = false;
		}
		tileX = x / (conf.getScaledTileWidth() + tileGap);
		tileY = y / (conf.getScaledTileHeight() + tileGap);

		if (oldTileX != tileX || oldTileY != tileY) {
			oldTileX = tileX;
			oldTileY = tileY;
			tileChanged = true;
		} else {
			tileChanged = false;
		}

		tileCursorX = (cursorX - (tileX * conf.width));
		tileCursorY = (cursorY - (tileY * conf.height));
		if (oldTileCursorX != tileCursorX || oldTileCursorY != tileCursorY) {
			oldTileCursorX = tileCursorX;
			oldTileCursorY = tileCursorY;
			tileCursorChanged = true;
		} else {
			tileCursorChanged = false;
		}
	}

	private boolean checkKeyPressed(int modifierKey, char charCode) {
		return (modifierMask & modifierKey) == modifierKey && currentCharacterPressed == charCode && keyPressed;
	}

	public void paintControl(PaintEvent e) {
		paintControl(e.gc, redrawMode, conf.isPixelGridEnabled(), conf.isSeparatorEnabled(), conf.isTileGridEnabled(),
				conf.isTileSubGridEnabled(), true, conf.isTileCursorEnabled(), true);
	}

	protected abstract void paintControl(GC gc, RedrawMode redrawMode, boolean paintPixelGrid, boolean paintSeparator,
			boolean paintTileGrid, boolean paintTileSubGrid, boolean paintSelection, boolean paintTileCursor,
			boolean paintTelevisionMode);

	protected void paintTelevisionRaster(GC gc) {
		int height = conf.height * conf.tileRows * conf.rows * conf.currentPixelHeight;
		int length = conf.width * conf.tileColumns * conf.columns * conf.currentPixelWidth;
		for (int y = 0; y < height; y += 2) {
			gc.setAlpha(60);
			gc.setForeground(Constants.BLACK);
			gc.drawLine(0, y, length, y);
		}
	}

	public void recalc() {
		int pixmul = conf.pixelConfig.pixmul;
		conf.currentPixelWidth = conf.pixelSize * pixmul;
		conf.currentWidth = conf.width / pixmul;
		// doRedraw(RedrawMode.DrawAllTiles, null, ImagePainterFactory.READ);
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		int width = (conf.width * conf.currentPixelWidth * conf.tileColumns * conf.columns);
		int height = (conf.height * conf.currentPixelHeight * conf.tileRows * conf.rows);
		return new Point(width, height);
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

	protected void fireDoRedraw(RedrawMode redrawMode, PencilMode pencilMode, int update) {
		drawListenerList.forEach(l -> l.doRedraw(redrawMode, update));
	}

	@Override
	public void doRedraw(RedrawMode redrawMode, int action) {
		this.action = action;
		this.redrawMode = redrawMode;
		redrawCalculatedArea();
	}

	public abstract void redrawCalculatedArea();

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
	public abstract void redrawTiles(List<Integer> selectedTileIndexList, RedrawMode redrawMode, int action);

	@Override
	public void tileChanged() {
		conf.setMultiColorEnabled(tile.isMulticolor());
		doRedraw(RedrawMode.DrawSelectedTile, ImagePainterFactory.UPDATE);
	}

	@Override
	public void tilesChanged(List<Integer> selectedTileIndexList) {
		doRedraw(RedrawMode.DrawSelectedTiles, ImagePainterFactory.UPDATE);
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
	public void layerContentChanged(int layer) {
		doRedraw(RedrawMode.DrawSelectedTile, ImagePainterFactory.UPDATE);
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