package de.drazil.nerdsuite.widget;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.enums.RedrawMode;
import de.drazil.nerdsuite.model.SelectionRange;

public class RepositoryWidget extends BaseImagingWidget {

	private boolean tileSelectionStarted = false;
	private SelectionRange tileSelectionRange = null;
	private List<Integer> selectedTileIndexList = null;

	public RepositoryWidget(Composite parent, int style) {
		super(parent, style);

		tileSelectionRange = new SelectionRange();
		selectedTileIndexList = new ArrayList<>();

	}

	@Override
	protected void leftMouseButtonClicked(int modifierMask, int x, int y) {
		if (supportsSingleSelection() || supportsMultiSelection()) {
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

			doDrawAllTiles();
		}
	}

	@Override
	protected void mouseDragged(int modifierMask, int x, int y) {
		if (supportsMultiSelection()) {
			// computeTileSelection(false, (modifierMask & SWT.CTRL) == SWT.CTRL);
			computeTileSelection(tileX, tileY, 1);
			doDrawAllTiles();
		}
	}

	@Override
	protected void leftMouseButtonReleased(int modifierMask, int x, int y) {
		if (supportsMultiSelection() && selectedTileIndexList.size() > 1) {
			tileSelectionStarted = false;
			tileSelectionRange.reset();
			tileRepositoryService.setSelectedTileIndexList(selectedTileIndexList);
		}
	}

	@Override
	protected void leftMouseButtonPressed(int modifierMask, int x, int y) {
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
	protected void mouseEnter(int modifierMask, int x, int y) {
		doDrawAllTiles();
	}

	@Override
	protected void mouseExit(int modifierMask, int x, int y) {
		doDrawAllTiles();
	}

	private int computeTileIndex(int x, int y) {
		return (x + (y * conf.columns));
	}

	private void computeTileSelection(int tileX, int tileY, int mode) {
		if (mode == 0) {
			tileSelectionStarted = false;
			tileSelectionRange.setFrom(tileX);
			tileSelectionRange.setTo(tileY);
		} else if (mode == 1) {
			int index = computeTileIndex(tileX, tileY);
			if (!tileSelectionStarted) {
				tileSelectionRange.setFrom(index);
				tileSelectionStarted = true;
			}
			tileSelectionRange.setTo(index);
			selectedTileIndexList.clear();

			int from = tileSelectionRange.getFrom();
			int to = tileSelectionRange.getTo();
			if (from > to) {
				int d = from;
				from = to;
				to = d;
			}

			for (int i = from; i <= to; i++) {
				selectedTileIndexList.add(i);
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
			doDrawAllTiles();
		}
	}

	public void paintControl(PaintEvent e) {
		paintControl(e.gc, redrawMode, conf.isPixelGridEnabled(), conf.isSeparatorEnabled(), conf.isTileGridEnabled(),
				conf.isTileSubGridEnabled(), true, conf.isTileCursorEnabled(), true);
	}

	protected void paintControl(GC gc, RedrawMode redrawMode, boolean paintPixelGrid, boolean paintSeparator,
			boolean paintTileGrid, boolean paintTileSubGrid, boolean paintSelection, boolean paintTileCursor,
			boolean paintTelevisionMode) {

		if (redrawMode == RedrawMode.DrawAllTiles) {
			for (int i = 0; i < tileRepositoryService.getSize(); i++) {
				paintTile(this, gc, tileRepositoryService.getTileIndex(i), conf, colorPaletteProvider);
			}
		} else if (redrawMode == RedrawMode.DrawSelectedTiles) {
			List<Integer> list = tileRepositoryService.getSelectedTileIndexList();
			for (int i = 0; i < list.size(); i++) {
				paintTile(this, gc, tileRepositoryService.getTileIndex(i), conf, colorPaletteProvider);
			}
		} else if (redrawMode == RedrawMode.DrawIndexed) {
			paintTile(this, gc, animationIndex, conf, colorPaletteProvider);
		}

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

		if (!supportsPainting()) {
			paintSelection(gc);
		}
		/*
		 * if (paintTileCursor) { paintTileCursor(gc, mouseIn, updateCursorLocation); }
		 */
		/*
		 * if (supportsRangeSelection() && conf.cursorMode ==
		 * CursorMode.SelectRectangle) { paintRangeSelection(gc); }
		 */
		/*
		 * if (paintTelevisionMode && supportsSingleSelection()) {
		 * paintTelevisionRaster(gc); }
		 */
		/*
		 * if (supportsDrawCursor()) { paintPixelCursor(gc); }
		 */
		redrawMode = RedrawMode.DrawNothing;

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
	public void updateTiles(List<Integer> selectedTileIndexList, UpdateMode updateMode) {
		if (updateMode == UpdateMode.Selection && (supportsSingleSelection() || supportsMultiSelection())) {
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
	public void updateTile(int selectedTileIndex, UpdateMode updateMode) {
		// TODO Auto-generated method stub

	}
}
