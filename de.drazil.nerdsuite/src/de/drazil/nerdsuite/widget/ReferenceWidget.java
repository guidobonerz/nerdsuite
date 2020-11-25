package de.drazil.nerdsuite.widget;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.enums.RedrawMode;
import de.drazil.nerdsuite.imaging.service.ImagePainterFactory;
import de.drazil.nerdsuite.model.ProjectMetaData;
import de.drazil.nerdsuite.model.SelectionRange;

public class ReferenceWidget extends BaseImagingWidget {

	private boolean tileSelectionStarted = false;
	private SelectionRange tileSelectionRange = null;
	private List<Integer> selectedTileIndexList = null;
	private int start;
	private int end;

	public ReferenceWidget(Composite parent, int style, String owner, IColorPaletteProvider colorPaletteProvider, boolean autowrap) {
		super(parent, style, owner, colorPaletteProvider, autowrap);
		tileSelectionRange = new SelectionRange();
		selectedTileIndexList = new ArrayList<>();
		// setBackground(Constants.DARK_GREY);
	}

	@Override
	protected void leftMouseButtonClicked(int modifierMask, int x, int y) {
		selectedTileIndexX = tileX;
		selectedTileIndexY = tileY;
		selectedTileIndex = computeTileIndex(tileX, tileY);
		System.out.printf("select index:%02x\n", selectedTileIndex);
		computeTileSelection(tileX, tileY, 1);
		if (selectedTileIndex < tileRepositoryService.getSize()) {
			tileRepositoryService.setSelectedTileIndex(selectedTileIndex);
		} else {
			System.out.println("tile selection outside range...");
		}
		doRedraw(RedrawMode.DrawAllTiles, ImagePainterFactory.READ);
	}

	@Override
	protected void leftMouseButtonReleased(int modifierMask, int x, int y) {
		if (selectedTileIndexList.size() > 1) {
			tileSelectionStarted = false;
			tileRepositoryService.setSelectedTileIndexList(selectedTileIndexList);
		}
		doRedraw(RedrawMode.DrawAllTiles, ImagePainterFactory.READ);
	}

	@Override
	protected void leftMouseButtonPressed(int modifierMask, int x, int y) {
		computeTileSelection(tileX, tileY, 0);
	}

	@Override
	protected void mouseEnter(int modifierMask, int x, int y) {
		mouseMove(modifierMask, x, y);
	}

	@Override
	protected void mouseExit(int modifierMask, int x, int y) {
		mouseMove(modifierMask, x, y);
	}

	@Override
	protected void mouseMove(int modifierMask, int x, int y) {
		if (tileChanged || !mouseIn) {
			doRedraw(RedrawMode.DrawAllTiles, ImagePainterFactory.READ);
		}
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

			selectedTileIndexList.clear();
			for (int i = from; i <= to; i++) {
				if (i < tileRepositoryService.getSize()) {
					selectedTileIndexList.add(i);
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
		paintTileMap(gc);
		paintSelection(gc);
		paintTileMarker(gc);
		gc.setBackground(Constants.DARK_GREY);
		gc.fillRectangle(0, 302, 301, 20);
		gc.setForeground(Constants.WHITE);
		int index = tileRepositoryService.getTileList().indexOf(tileRepositoryService.getSelectedTile());
		int index2 = tileRepositoryService.getSelectedTileIndex();
		gc.drawString(String.format("Natural %03X / %03d Ordered %03X / %03d", index, index, index2, index2), 0, 305);
		action = ImagePainterFactory.NONE;

		redrawMode = RedrawMode.DrawNothing;
	}

	private void paintSelection(GC gc) {
		gc.setBackground(Constants.SELECTION_TILE_MARKER_COLOR);
		gc.setAlpha(150);
		selectedTileIndexList.forEach(i -> {
			int y = i / conf.columns;
			int x = i % conf.columns;
			gc.fillRectangle(x * (conf.tileWidthPixel + conf.tileGap), y * (conf.tileHeightPixel + conf.tileGap), conf.tileWidthPixel, conf.tileHeightPixel);
			if (i == temporaryIndex) {
				gc.setLineWidth(3);
				gc.setForeground(Constants.TEMPORARY_SELECTION_TILE_MARKER_COLOR);
				gc.drawRectangle(x * (conf.tileWidthPixel + conf.tileGap), y * (conf.tileHeightPixel + conf.tileGap), conf.tileWidthPixel, conf.tileHeightPixel);
			}
		});
	}

	private void paintTileMarker(GC gc) {
		if (mouseIn && computeTileIndex(tileX, tileY) < tileRepositoryService.getSize()) {
			gc.setLineWidth(3);
			gc.setBackground(Constants.BRIGHT_ORANGE);
			gc.fillRectangle(tileX * (conf.tileWidthPixel + conf.tileGap), tileY * (conf.tileHeightPixel + conf.tileGap), conf.tileWidthPixel, conf.tileHeightPixel);
		}
	}

	@Override
	public void redrawTiles(List<Integer> selectedTileIndexList, RedrawMode redrawMode, int action) {
		if (redrawMode == RedrawMode.DrawTemporarySelectedTile) {
			temporaryIndex = selectedTileIndexList.get(0);
		}
		doRedraw(redrawMode, action);
	}

	private void paintTileMap(GC gc) {
		gc.drawImage(imagePainterFactory.createOrUpdateBaseImage("REPOSITORY", Constants.BLACK, 301, 301).getImage(), 0, 0);
		gc.drawImage(imagePainterFactory.createOrUpdateTileMap(1, false).getImage(), 0, 0);
	}

	@Override
	public void activeLayerChanged(int layer) {

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
		if (redrawMode == RedrawMode.DrawSelectedTiles || redrawMode == RedrawMode.DrawSelectedTile || redrawMode == RedrawMode.DrawPixel) {
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
			redraw();
		}
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point(conf.fullWidthPixel, conf.fullHeightPixel + 20);
	}

	@Override
	protected String getViewerConfigName() {
		return ProjectMetaData.REFERENCE_REPOSITORY_CONFIG;
	}
}
