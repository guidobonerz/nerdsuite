package de.drazil.nerdsuite.widget;

import static java.lang.Math.abs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.enums.RedrawMode;
import de.drazil.nerdsuite.model.SelectionRange;

public class RepositoryWidget extends BaseImagingWidget {

	private boolean tileDragActive = false;
	private boolean tileSelectionStarted = false;
	private SelectionRange tileSelectionRange = null;
	private List<Integer> selectedTileIndexList = null;
	// private int tileMarkerIndex;

	public RepositoryWidget(Composite parent, int style) {
		super(parent, style);
		tileSelectionRange = new SelectionRange();
		selectedTileIndexList = new ArrayList<>();
		setTriggerMillis(1000);
	}

	@Override
	protected void leftMouseButtonClicked(int modifierMask, int x, int y) {
		mc.stop();
		if (!tileDragActive) {
			selectedTileIndexX = tileX;
			selectedTileIndexY = tileY;
			selectedTileIndex = computeTileIndex(tileX, tileY);
			// computeTileSelection(false, (modifierMask & SWT.CTRL) == SWT.CTRL);
			computeTileSelection(tileX, tileY, 1);
			if (selectedTileIndex < tileRepositoryService.getSize()) {
				tileRepositoryService.setSelectedTileIndex(selectedTileIndex);
			} else {
				System.out.println("tile selection outside range...");
			}
			// fireSetSelectedTile(ImagingWidget.this, tile);
			// doDrawAllTiles();
			doRedraw(RedrawMode.DrawAllTiles, null, false);
		} else {
			selectedTileIndexX = tileX;
			selectedTileIndexY = tileY;
			selectedTileIndex = computeTileIndex(tileX, tileY);
		}
	}

	@Override
	protected void mouseDragged(int modifierMask, int x, int y) {
		if (tileSelectionStarted) {
			mc.stop();
		}
		computeTileSelection(tileX, tileY, 1);
		// doDrawAllTiles();
		doRedraw(RedrawMode.DrawAllTiles, null, false);
	}

	@Override
	protected void leftMouseButtonReleased(int modifierMask, int x, int y) {
		mc.stop();
		if (tileDragActive) {
			tileDragActive = false;
			tileRepositoryService.moveTile(tileSelectionRange.getFrom(), tileSelectionRange.getTo());
		}
		if (selectedTileIndexList.size() > 1) {
			tileSelectionStarted = false;
			// tileSelectionRange.reset();
			tileRepositoryService.setSelectedTileIndexList(selectedTileIndexList);
		}
	}

	@Override
	protected void leftMouseButtonPressed(int modifierMask, int x, int y) {
		mc.start();
		if (supportsSingleSelection()) {
			resetSelectionList();
		}
		if (supportsMultiSelection() || supportsSingleSelection()) {
			computeTileSelection(tileX, tileY, 0);
			// System.out.printf("tile x:%2d tile y:%2d\n", tileX, tileY);
		}
	}

	@Override
	protected void mouseMove(int modifierMask, int x, int y) {
		if (oldTileX != tileX || oldTileY != tileY) {
			// tileMarkerIndex = computeTileIndex(tileX, tileY);
			oldTileX = tileX;
			oldTileY = tileY;
			doRedraw(RedrawMode.DrawAllTiles, null, false);
		}
	}

	@Override
	protected void mouseEnter(int modifierMask, int x, int y) {
		// doDrawAllTiles();
	}

	@Override
	protected void mouseExit(int modifierMask, int x, int y) {
		// doDrawAllTiles();
	}

	private int computeTileIndex(int x, int y) {
		return (x + (y * conf.columns));
	}

	private void computeTileSelection(int tileX, int tileY, int mode) {
		int index = computeTileIndex(tileX, tileY);
		if (mode == 0) {
			tileSelectionStarted = false;
			tileSelectionRange.setFrom(index);
			tileSelectionRange.setTo(index);
		} else if (mode == 1) {
			if (!tileSelectionStarted) {
				tileSelectionRange.setFrom(index);
				tileSelectionStarted = true;
			}
			tileSelectionRange.setTo(index);

			int from = tileSelectionRange.getFrom();
			int to = tileSelectionRange.getTo();
			if (from > to) {
				int d = from;
				from = to;
				to = d;
			}
			if (!tileDragActive) {
				selectedTileIndexList.clear();
				for (int i = from; i <= to; i++) {
					if (i < tileRepositoryService.getSize()) {
						selectedTileIndexList.add(i);
					}
				}
			}
		}
	}

	private void resetSelectionList() {
		selectedTileIndexList = new ArrayList<>();
	}

	public void selectAll() {
		if (supportsMultiSelection()) {
			resetSelectionList();
			// computeSelection(true, false);
			// doDrawAllTiles();
			doRedraw(RedrawMode.DrawAllTiles, null, false);
		}
	}

	public void paintControl(PaintEvent e) {
		paintControl(e.gc, redrawMode, conf.isPixelGridEnabled(), conf.isSeparatorEnabled(), conf.isTileGridEnabled(),
				conf.isTileSubGridEnabled(), true, conf.isTileCursorEnabled(), true);
	}

	protected void paintControl(GC gc, RedrawMode redrawMode, boolean paintPixelGrid, boolean paintSeparator,
			boolean paintTileGrid, boolean paintTileSubGrid, boolean paintSelection, boolean paintTileCursor,
			boolean paintTelevisionMode) {

		if (redrawMode == RedrawMode.DrawSelectedTile) {
			paintTile(this, gc, tileRepositoryService.getSelectedTileIndex(), conf, colorPaletteProvider, forceUpdate);
		} else {
			// if (redrawMode == RedrawMode.DrawAllTiles) {
			for (int i = 0; i < tileRepositoryService.getSize(); i++) {
				paintTile(this, gc, tileRepositoryService.getTileIndex(i), conf, colorPaletteProvider, forceUpdate);
			}
			// }
		}
		/*
		 * else if (redrawMode == RedrawMode.DrawSelectedTile) { paintTile(this, gc,
		 * temporaryIndex, conf, colorPaletteProvider, forceUpdate); }
		 */
		if (paintPixelGrid) {
			paintPixelGrid(gc);
		}
		/*
		 * if (paintSeparator) { paintSeparator(gc); }
		 */
		if (paintTileGrid) {
			paintTileGrid(gc);
		}

		if (paintTileSubGrid) {
			paintTileSubGrid(gc);
		}

		if (tileDragActive) {
			paintDragMarker(gc);
		} else {
			paintSelection(gc);
			paintTileMarker(gc);
		}

		forceUpdate = false;
		redrawMode = RedrawMode.DrawNothing;

	}

	private void paintDragMarker(GC gc) {
		int from = tileSelectionRange.getFrom();
		int to = tileSelectionRange.getTo();
		int xfrom = from % conf.getColumns();
		int yfrom = from / conf.getColumns();
		int xto = to % conf.getColumns();
		int yto = to / conf.getColumns();
		gc.setLineWidth(1);
		gc.setBackground(Constants.SELECTION_TILE_MARKER_COLOR);
		gc.setAlpha(150);
		gc.fillRectangle(xfrom * conf.scaledTileWidth, yfrom * conf.scaledTileHeight, conf.scaledTileWidth,
				conf.scaledTileHeight);
		gc.setAlpha(255);
		gc.setForeground(Constants.BRIGHT_ORANGE);
		gc.setLineWidth(4);
		if (abs(to - from) > 0)
			gc.drawLine(xto * conf.scaledTileWidth, yto * conf.scaledTileHeight, xto * conf.scaledTileWidth,
					yto * conf.scaledTileHeight + conf.scaledTileHeight);
	}

	private void paintSelection(GC gc) {
		gc.setBackground(Constants.SELECTION_TILE_MARKER_COLOR);
		gc.setAlpha(150);
		selectedTileIndexList.forEach(i -> {
			int y = i / conf.getColumns();
			int x = i % conf.getColumns();
			gc.fillRectangle(x * conf.scaledTileWidth, y * conf.scaledTileHeight, conf.scaledTileWidth,
					conf.scaledTileHeight);
			if (i == temporaryIndex) {
				gc.setLineWidth(3);
				gc.setForeground(Constants.TEMPORARY_SELECTION_TILE_MARKER_COLOR);
				gc.drawRectangle(x * conf.scaledTileWidth, y * conf.scaledTileHeight, conf.scaledTileWidth,
						conf.scaledTileHeight);
			}
		});
	}

	private void paintTileMarker(GC gc) {
		if (mouseIn && computeTileIndex(tileX, tileY) < tileRepositoryService.getSize()) {
			gc.setLineWidth(3);
			gc.setBackground(Constants.BRIGHT_ORANGE);
			gc.fillRectangle(tileX * conf.scaledTileWidth, tileY * conf.scaledTileHeight, conf.scaledTileWidth,
					conf.scaledTileHeight);
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

	@Override
	public void redrawTiles(List<Integer> selectedTileIndexList, RedrawMode redrawMode, boolean forceUpdate) {
		this.forceUpdate = forceUpdate;
		System.out.println("forceUpdate:" + forceUpdate);
		this.redrawMode = redrawMode;
		if (redrawMode == RedrawMode.DrawTemporarySelectedTile) {
			temporaryIndex = selectedTileIndexList.get(0);
		}
		// doDrawAllTiles();
		doRedraw(redrawMode, null, forceUpdate);
	}

	public void paintTile(Composite parent, GC gc, int index, ImagingWidgetConfiguration conf,
			IColorPaletteProvider colorPaletteProvider, boolean forceUpdate) {
		// int parentWidth = parent.getBounds().width;
		Image image = tileRepositoryService.getImagePainterFactory().getImage(tileRepositoryService.getTile(index), 0,
				0, false, conf, colorPaletteProvider, forceUpdate);
		int imageWidth = image.getBounds().width;
		int imageHeight = image.getBounds().height;
		int columns = conf.getColumns();// (int) (parentWidth / imageWidth);
		// conf.setColumns(columns);
		int y = (index / columns) * imageHeight;
		int x = (index % columns) * imageWidth;
		gc.drawImage(image, x, y);
	}

	@Override
	public void activeLayerChanged(int layer) {
		// doDrawAllTiles();
		doRedraw(RedrawMode.DrawAllTiles, null, false);
	}

	@Override
	public void colorSelected(int colorNo, int colorIndex) {
		tileRepositoryService.getSelectedTile().setActiveLayerColorIndex(colorNo, colorIndex, true);
	}

	@Override
	public void onTriggerTimeReached(long triggerTime) {
		tileDragActive = true;
		computeTileSelection(tileX, tileY, 0);
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				// doDrawAllTiles();
				doRedraw(RedrawMode.DrawAllTiles, null, false);
			}
		});
	}

	@Override
	public void tileReordered() {
		// doDrawAllTiles();
		doRedraw(RedrawMode.DrawAllTiles, null, false);
	}

}
