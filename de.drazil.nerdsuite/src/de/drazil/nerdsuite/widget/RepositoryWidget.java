package de.drazil.nerdsuite.widget;

import static java.lang.Math.abs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.GC;
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

	public RepositoryWidget(Composite parent, int style, String owner, IColorPaletteProvider colorPaletteProvider, boolean autowrap) {
		super(parent, style, owner, colorPaletteProvider, autowrap);
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
			mc.stop();
		}
		computeTileSelection(tileX, tileY, 1);
		doRedraw(RedrawMode.DrawAllTiles, ImagePainterFactory.READ);
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
			tileRepositoryService.setSelectedTileIndexList(selectedTileIndexList);
		}
		doRedraw(RedrawMode.DrawAllTiles, ImagePainterFactory.READ);
	}

	@Override
	protected void leftMouseButtonPressed(int modifierMask, int x, int y) {
		mc.start();
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
			// resetSelectionList();
			doRedraw(RedrawMode.DrawAllTiles, ImagePainterFactory.READ);
		}
	}

	public void paintControl(PaintEvent e) {
		paintControl(e.gc, redrawMode, conf.pixelGridEnabled, conf.separatorEnabled, conf.tileGridEnabled, conf.tileSubGridEnabled, true, conf.tileCursorEnabled, true);
	}

	protected void paintControl(GC gc, RedrawMode redrawMode, boolean paintPixelGrid, boolean paintSeparator, boolean paintTileGrid, boolean paintTileSubGrid, boolean paintSelection,
			boolean paintTileCursor, boolean paintTelevisionMode) {
		/*
		 * for (int i = (drawAll ? 0 : start); i < (drawAll ?
		 * tileRepositoryService.getSize() : end); i++) { int index = drawAll ? i :
		 * tileRepositoryService.getTileIndex(i); paintTile(gc, i, action); }
		 */
		/*
		 * if (paintTileGrid) { paintTileGrid(gc); }
		 * 
		 * if (tileDragActive) { paintDragMarker(gc); } else { paintSelection(gc);
		 * paintTileMarker(gc); }
		 */
		action = ImagePainterFactory.NONE;
		drawAll = true;
		redrawMode = RedrawMode.DrawNothing;
	}

	private void paintDragMarker(GC gc) {
		int from = tileSelectionRange.getFrom();
		int to = tileSelectionRange.getTo();
		int xfrom = from % conf.columns;
		int yfrom = from / conf.columns;
		int xto = to % conf.columns;
		int yto = to / conf.columns;
		gc.setLineWidth(1);
		gc.setBackground(Constants.SELECTION_TILE_MARKER_COLOR);
		gc.setAlpha(150);
		gc.fillRectangle(xfrom * conf.tileWidthPixel, yfrom * conf.tileHeightPixel, conf.tileWidthPixel, conf.tileHeightPixel);
		gc.setAlpha(255);
		gc.setForeground(Constants.BRIGHT_ORANGE);
		gc.setLineWidth(4);
		if (abs(to - from) > 0)
			gc.drawLine(xto * conf.tileWidthPixel, yto * conf.tileHeightPixel, xto * conf.tileWidthPixel, yto * conf.tileHeightPixel + conf.tileHeightPixel);
	}

	private void paintSelection(GC gc) {
		gc.setBackground(Constants.SELECTION_TILE_MARKER_COLOR);
		gc.setAlpha(150);
		selectedTileIndexList.forEach(i -> {
			int y = i / conf.columns;
			int x = i % conf.columns;
			gc.fillRectangle(x * conf.tileWidthPixel, y * conf.tileHeightPixel, conf.tileWidthPixel, conf.tileHeightPixel);
			if (i == temporaryIndex) {
				gc.setLineWidth(3);
				gc.setForeground(Constants.TEMPORARY_SELECTION_TILE_MARKER_COLOR);
				gc.drawRectangle(x * conf.tileWidthPixel, y * conf.tileHeightPixel, conf.tileWidthPixel, conf.tileHeightPixel);
			}
		});
	}

	private void paintTileMarker(GC gc) {
		if (mouseIn && computeTileIndex(tileX, tileY) < tileRepositoryService.getSize()) {
			gc.setLineWidth(3);
			gc.setBackground(Constants.BRIGHT_ORANGE);
			gc.fillRectangle(tileX * conf.tileWidthPixel, tileY * conf.tileHeightPixel, conf.tileWidthPixel, conf.tileHeightPixel);
		}
	}

	private void paintTileGrid(GC gc) {
		gc.setLineWidth(1);
		gc.setLineStyle(SWT.LINE_SOLID);
		gc.setForeground(Constants.TILE_GRID_COLOR);
		for (int x = 0; x < conf.columns; x++) {
			for (int y = 0; y < conf.rows; y++) {
				gc.drawRectangle(x * conf.tileWidthPixel, y * conf.tileHeightPixel, conf.tileWidthPixel, conf.tileHeightPixel);
			}
		}
	}

	@Override
	public void redrawTiles(List<Integer> selectedTileIndexList, RedrawMode redrawMode, int action) {
		if (redrawMode == RedrawMode.DrawTemporarySelectedTile) {
			temporaryIndex = selectedTileIndexList.get(0);
		}
		doRedraw(redrawMode, action);
	}

	public void paintTile(Composite parent, GC gc, int index, ImagingWidgetConfiguration conf, IColorPaletteProvider colorPaletteProvider, int update) {
		/*
		 * Image image = tileRepositoryService.getImagePainterFactory().getImage(
		 * tileRepositoryService, tileRepositoryService.getTileIndex(index), 0, 0,
		 * update, conf, colorPaletteProvider, tileRepositoryService.getMetadata()); int
		 * imageWidth = image.getBounds().width; int imageHeight =
		 * image.getBounds().height; int columns = conf.getColumns(); int y = (index /
		 * columns) * imageHeight; int x = (index % columns) * imageWidth;
		 * gc.drawImage(image, x, y);
		 */

	}

	private void paintTile(GC gc, int index, int action) {
		Tile tile = tileRepositoryService.getSelectedTile();
		Layer layer = tile.getActiveLayer();
		String name = String.format("%s_%s", tile.getName(), layer.getName());
		Image2 imageInternal = imagePainterFactory.createLayer();
		GC gcLayer = new GC(imageInternal.getImage());
		int x = 0;
		int y = 0;
		for (int i = 0; i < conf.getTileSize(); i++) {
			if (i % conf.tileWidth == 0 && i > 0) {
				x = 0;
				y++;
			}
			gcLayer.setBackground(colorPaletteProvider.getColorByIndex(layer.getContent()[i]));
			gcLayer.fillRectangle(x * conf.pixelPaintWidth, y * conf.pixelPaintHeight, conf.pixelPaintWidth, conf.pixelPaintHeight);
			x++;
		}
		gcLayer.dispose();
		gc.drawImage(imagePainterFactory.createOrUpdateBaseImage(name, Constants.BLACK).getImage(), 0, 0);

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
	public void onTriggerTimeReached(long triggerTime) {
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
	public void tileReordered() {
		doRedraw(RedrawMode.DrawAllTiles, ImagePainterFactory.READ);
	}

	@Override
	public void redrawCalculatedArea() {
		if (redrawMode == RedrawMode.DrawSelectedTiles || redrawMode == RedrawMode.DrawSelectedTile || redrawMode == RedrawMode.DrawPixel) {
			drawAll = false;

			start = tileRepositoryService.getSelectedTileIndexList().get(0);
			end = tileRepositoryService.getSelectedTileIndexList().get(tileRepositoryService.getSelectedTileIndexList().size() - 1);

			int iys = start / conf.columns;
			int ys = iys * conf.tileHeightPixel;

			int iye = end / conf.columns;
			int ye = iye * conf.tileHeightPixel;

			start = computeTileIndex(0, iys);
			end = computeTileIndex(conf.columns, iye);
			if (end > tileRepositoryService.getSize()) {
				end = tileRepositoryService.getSize();
			}
			int height = (1 + iye - iys) * conf.tileHeightPixel;
			redraw(0, ys, conf.tileWidthPixel * conf.columns, height, false);
		} else {
			drawAll = true;
			redraw();
		}
	}

}
