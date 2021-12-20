package de.drazil.nerdsuite.widget;

import static java.lang.Math.abs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.enums.RedrawMode;
import de.drazil.nerdsuite.imaging.service.ImagePainterFactory;
import de.drazil.nerdsuite.model.Image2;
import de.drazil.nerdsuite.model.SelectionRange;

public class RepositoryWidget extends BaseImagingWidget {

	private boolean tileDragActive = false;
	private boolean tileSelectionStarted = false;
	private SelectionRange tileSelectionRange = null;
	private List<Integer> selectedTileIndexList = null;
	private boolean drawAll = true;
	private int start;
	private int end;
	private Map<String, Image2> imageCache;
	private int maxColumns;
	private int maxRows;

	public RepositoryWidget(Composite parent, int style, String owner, IColorPaletteProvider colorPaletteProvider,
			boolean autowrap) {
		super(parent, style, owner, colorPaletteProvider, autowrap);
		tileSelectionRange = new SelectionRange();
		selectedTileIndexList = new ArrayList<>();
		setTriggerMillis(1000);
		setBackground(Constants.BLACK);
		imageCache = new HashMap<String, Image2>();
	}

	@Override
	protected void leftMouseButtonPressedDelayed(int modifierMask, int x, int y) {
		tileDragActive = true;
		computeTileSelection(tileX, tileY, 0);
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				doRedraw(RedrawMode.DrawAllTiles, ImagePainterFactory.READ);
			}
		});
	}

	@Override
	protected void leftMouseButtonClicked(int modifierMask, int x, int y) {
		// mc.stop();
		if (!tileDragActive) {
			selectedTileIndexX = tileX;
			selectedTileIndexY = tileY;
			selectedTileIndex = computeTileIndex(tileX, tileY);
			computeTileSelection(tileX, tileY, 1);
			if (selectedTileIndex < tileRepositoryService.getSize()) {
				tileRepositoryService.setSelectedTileIndex(selectedTileIndex);
			} else {
				System.out.println("tile selection outside range...");
			}
			doRedraw(RedrawMode.DrawAllTiles, ImagePainterFactory.READ);
		} else {
			selectedTileIndexX = tileX;
			selectedTileIndexY = tileY;
			selectedTileIndex = computeTileIndex(tileX, tileY);
		}
	}

	@Override
	protected void mouseDragged(int modifierMask, int x, int y) {
		if (tileSelectionStarted) {
			// mc.stop();
		}
		computeTileSelection(tileX, tileY, 1);
		doRedraw(RedrawMode.DrawAllTiles, ImagePainterFactory.READ);
	}

	@Override
	protected void leftMouseButtonReleased(int modifierMask, int x, int y) {
		// mc.stop();
		if (tileDragActive) {
			tileDragActive = false;
			tileRepositoryService.moveTile(tileSelectionRange.getFrom(), tileSelectionRange.getTo());
		}
		if (selectedTileIndexList.size() > 1) {
			tileSelectionStarted = false;
			tileRepositoryService.setSelectedTileIndexList(selectedTileIndexList);
		}
		doRedraw(RedrawMode.DrawAllTiles, ImagePainterFactory.READ);
	}

	@Override
	protected void leftMouseButtonPressed(int modifierMask, int x, int y) {
		// mc.start(-1);
		computeTileSelection(tileX, tileY, 0);
	}

	@Override
	protected void mouseMove(int modifierMask, int x, int y) {
		if (tileChanged) {
			doRedraw(RedrawMode.DrawAllTiles, ImagePainterFactory.READ);
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
		return (x + (y * maxColumns));
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
			// resetSelectionList();
			doRedraw(RedrawMode.DrawAllTiles, ImagePainterFactory.READ);
		}
	}

	public void paintControl(PaintEvent e) {
		paintControl(e.gc, redrawMode, conf.pixelGridEnabled, conf.separatorEnabled, conf.tileGridEnabled,
				conf.tileSubGridEnabled, true, conf.tileCursorEnabled, true);
	}

	protected void paintControl(GC gc, RedrawMode redrawMode, boolean paintPixelGrid, boolean paintSeparator,
			boolean paintTileGrid, boolean paintTileSubGrid, boolean paintSelection, boolean paintTileCursor,
			boolean paintTelevisionMode) {
		Rectangle r = getParent().getBounds();

		maxColumns = r.width / (conf.repositoryScaledTileWith + conf.tileGap);
		maxRows = r.height / (conf.repositoryScaledTileHeight + conf.tileGap);

		paintTiles(gc, action, maxColumns, maxRows);

		if (paintTileGrid) {
			paintTileGrid(gc, maxColumns, maxRows);
		}

		if (tileDragActive) {
			paintDragMarker(gc);
		} else {

			paintTileMarker(gc);
			paintSelection(gc);
		}

		action = ImagePainterFactory.NONE;
		drawAll = true;
		redrawMode = RedrawMode.DrawNothing;
	}

	private void paintDragMarker(GC gc) {
		int from = tileSelectionRange.getFrom();
		int to = tileSelectionRange.getTo();
		int xfrom = from % maxColumns;
		int yfrom = from / maxColumns;
		int xto = to % maxColumns;
		int yto = to / maxColumns;
		gc.setLineWidth(1);
		gc.setBackground(Constants.SELECTION_TILE_MARKER_COLOR);
		gc.setAlpha(150);
		gc.fillRectangle(xfrom * conf.tileWidthPixel, yfrom * conf.tileHeightPixel, conf.tileWidthPixel,
				conf.tileHeightPixel);
		gc.setAlpha(255);
		gc.setForeground(Constants.BRIGHT_ORANGE);
		gc.setLineWidth(4);
		if (abs(to - from) > 0)
			gc.drawLine(xto * conf.tileWidthPixel, yto * conf.tileHeightPixel, xto * conf.tileWidthPixel,
					yto * conf.tileHeightPixel + conf.tileHeightPixel);
	}

	private void paintSelection(GC gc) {
		gc.setBackground(Constants.SELECTION_TILE_MARKER_COLOR);
		gc.setAlpha(150);
		selectedTileIndexList.forEach(i -> {
			int y = i / maxColumns;
			int x = i % maxColumns;
			gc.fillRectangle(x * (conf.repositoryScaledTileWith + conf.tileGap),
					y * (conf.repositoryScaledTileHeight + conf.tileGap), conf.repositoryScaledTileWith,
					conf.repositoryScaledTileHeight);
			if (i == temporaryIndex) {
				gc.setLineWidth(3);
				gc.setForeground(Constants.TEMPORARY_SELECTION_TILE_MARKER_COLOR);
				gc.drawRectangle(x * (conf.repositoryScaledTileWith + conf.tileGap),
						y * (conf.repositoryScaledTileHeight + conf.tileGap), conf.repositoryScaledTileWith,
						conf.repositoryScaledTileHeight);
			}
		});
	}

	private void paintTileMarker(GC gc) {
		if (mouseIn && computeTileIndex(tileX, tileY) < tileRepositoryService.getSize()) {
			gc.setLineWidth(3);
			gc.setBackground(Constants.BRIGHT_ORANGE);
			gc.setAlpha(150);
			gc.fillRectangle(tileX * (conf.repositoryScaledTileWith + conf.tileGap),
					tileY * (conf.repositoryScaledTileHeight + conf.tileGap), conf.repositoryScaledTileWith,
					conf.repositoryScaledTileHeight);
		}
	}

	private void paintTileGrid(GC gc, int maxColumns, int maxRows) {
		gc.setLineWidth(1);
		gc.setLineStyle(SWT.LINE_SOLID);
		gc.setForeground(Constants.TILE_GRID_COLOR);
		for (int x = 0; x <= maxColumns; x++) {

			gc.drawLine(x * (conf.repositoryScaledTileWith + conf.tileGap) - 1, 0,
					x * (conf.repositoryScaledTileWith + conf.tileGap) - 1, getParent().getBounds().height);
		}
		for (int y = 0; y <= maxRows; y++) {
			gc.drawLine(0, y * (conf.repositoryScaledTileHeight + conf.tileGap) - 1, getParent().getBounds().width,
					y * (conf.repositoryScaledTileHeight + conf.tileGap) - 1);

		}
	}

	@Override
	public void redrawTiles(List<Integer> selectedTileIndexList, RedrawMode redrawMode, int action) {
		if (redrawMode == RedrawMode.DrawTemporarySelectedTile) {
			temporaryIndex = selectedTileIndexList.get(0);
		}
		doRedraw(redrawMode, action);
	}

	private void paintTiles(GC gc, int action, int maxX, int maxY) {
		int max = maxX * maxY;
		for (int i = 0; i < max; i++) {
			paintTile(gc, i, action, maxX);
		}
	}

	private void paintTile(GC gc, int index, int action, int columns) {

		int y = (index / columns) * (conf.repositoryScaledTileHeight + conf.tileGap);
		int x = (index % columns) * (conf.repositoryScaledTileWith + conf.tileGap);
		if (index < tileRepositoryService.getSize()) {
			Tile tile = tileRepositoryService.getTile(index);
			Layer layer = tile.getActiveLayer();
			String name = String.format(ImagePainterFactory.IMAGE_ID, tile.getId(), layer.getId(), 0);
			imagePainterFactory.drawScaledImage(gc, tile, name, x, y, true);
		} else {
			/*
			 * gc.setForeground(Constants.TILE_GRID_COLOR);
			 * gc.setFont(Constants.GoogleMaterials); gc.drawLine(x, y, x +
			 * conf.repositoryScaledTileHeight + conf.tileGap, y +
			 * conf.repositoryScaledTileHeight + conf.tileGap);
			 */
		}
	}

	@Override
	public void activeLayerChanged(int layer) {
		doRedraw(RedrawMode.DrawAllTiles, ImagePainterFactory.READ);
	}

	@Override
	public void colorSelected(int colorNo, int colorIndex) {
		tileRepositoryService.getSelectedTile().setActiveLayerColorIndex(colorNo, colorIndex, true);
	}

	@Override
	public void tileReordered() {
		doRedraw(RedrawMode.DrawAllTiles, ImagePainterFactory.READ);
	}

	@Override
	public void redrawCalculatedArea() {

		if (redrawMode == RedrawMode.DrawSelectedTiles || redrawMode == RedrawMode.DrawSelectedTile
				|| redrawMode == RedrawMode.DrawPixel) {
			drawAll = false;

			start = tileRepositoryService.getSelectedTileIndexList().get(0);
			end = tileRepositoryService.getSelectedTileIndexList()
					.get(tileRepositoryService.getSelectedTileIndexList().size() - 1);

			int iys = start / maxColumns;
			int ys = iys * (conf.repositoryScaledTileHeight + conf.tileGap);

			int iye = end / maxColumns;
			int ye = iye * (conf.repositoryScaledTileHeight + conf.tileGap);

			start = computeTileIndex(0, iys);
			end = computeTileIndex(maxColumns, iye);
			if (end > tileRepositoryService.getSize()) {
				end = tileRepositoryService.getSize();
			}
			int height = (1 + iye - iys) * (conf.repositoryScaledTileHeight + conf.tileGap);
			redraw(0, ys, (conf.repositoryScaledTileWith + conf.tileGap) * maxColumns, height, false);
		} else {
			drawAll = true;
			redraw();
		}
	}

}
