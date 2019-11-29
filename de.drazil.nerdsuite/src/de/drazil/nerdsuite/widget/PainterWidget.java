package de.drazil.nerdsuite.widget;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.enums.CursorMode;
import de.drazil.nerdsuite.enums.PencilMode;
import de.drazil.nerdsuite.enums.RedrawMode;

public class PainterWidget extends BaseImagingWidget {

	private boolean rangeSelectionStarted = false;
	private int selectedPixelRangeX = 0;
	private int selectedPixelRangeY = 0;
	private int selectedPixelRangeX2 = 0;
	private int selectedPixelRangeY2 = 0;
	private boolean forceUpdate = false;

	public PainterWidget(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected void leftMouseButtonClicked(int modifierMask, int x, int y) {
		if (conf.cursorMode == CursorMode.Point) {
			setPixel(tile, cursorX, cursorY, conf);
			forceUpdate = true;
			doDrawPixel();
			//fireDoDrawTile(this);
		}
	}

	@Override
	protected void mouseDragged(int modifierMask, int x, int y) {

		if (conf.cursorMode == CursorMode.Point) {
			if (oldCursorX != cursorX || oldCursorY != cursorY) {
				oldCursorX = cursorX;
				oldCursorY = cursorY;
				setPixel(tile, cursorX, cursorY, conf);
				forceUpdate = true;
				doDrawPixel();
				//fireDoDrawTile(this);
			}
		} else if (conf.cursorMode == CursorMode.SelectRectangle) {
			computeRangeSelection(tileCursorX, tileCursorY, 1, (modifierMask & SWT.SHIFT) == SWT.SHIFT);
			doDrawTile();
		}
	}

	@Override
	protected void leftMouseButtonPressed(int modifierMask, int x, int y) {
		if (conf.cursorMode == CursorMode.SelectRectangle) {
			computeRangeSelection(tileCursorX, tileCursorY, 0, false);
			// rangeSelectionStarted = true;
			doDrawTile();
		}
	}

	@Override
	protected void leftMouseButtonReleased(int modifierMask, int x, int y) {
		if (conf.cursorMode == CursorMode.SelectRectangle) {
			if (rangeSelectionStarted) {
				rangeSelectionStarted = false;
				computeRangeSelection(tileCursorX, tileCursorY, 2, (modifierMask & SWT.SHIFT) == SWT.SHIFT);
			}
		}
	}

	@Override
	protected void mouseEnter(int modifierMask, int x, int y) {
		doDrawTile();
	}

	@Override
	protected void mouseExit(int modifierMask, int x, int y) {
		doDrawTile();
	}

	@Override
	protected void mouseMove(int modifierMask, int x, int y) {
		int i = computeCursorIndex(cursorX, cursorY);
		System.out.println("index:" + i);
		doDrawTile();
	}

	private int computeCursorIndex(int x, int y) {
		return (x + (y * conf.width * conf.tileColumns));
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

	protected void paintControl(GC gc, RedrawMode redrawMode, boolean paintPixelGrid, boolean paintSeparator,
			boolean paintTileGrid, boolean paintTileSubGrid, boolean paintSelection, boolean paintTileCursor,
			boolean paintTelevisionMode) {

		if (redrawMode == RedrawMode.DrawPixel) {
			System.out.println("draw Pixel");
			paintPixel(gc, tileRepositoryService.getSelectedTile(), cursorX, cursorY, conf, colorPaletteProvider);
		} else if (redrawMode == RedrawMode.DrawSelectedTile) {
			paintTile(gc, tileRepositoryService.getSelectedTile(), conf, colorPaletteProvider, false);
		} else if (redrawMode == RedrawMode.DrawTemporarySelectedTile) {
			paintTile(gc, tileRepositoryService.getTile(temporaryIndex), conf, colorPaletteProvider, false);
		}

		if (paintPixelGrid) {
			paintPixelGrid(gc);
		}

		if (paintSeparator) {
			paintSeparator(gc);
		}

		if (paintTileSubGrid) {
			paintTileSubGrid(gc);
		}

		paintPixelCursor(gc);

		if (conf.cursorMode == CursorMode.SelectRectangle) {
			paintRangeSelection(gc);
		}

		forceUpdate = false;
		redrawMode = RedrawMode.DrawNothing;
	}

	private void paintSeparator(GC gc) {
		gc.setForeground(Constants.BYTE_SEPARATOR_COLOR);
		int bc = conf.pixelConfig.bitCount;
		int step = (8 * bc);
		for (int x = step; x < (conf.scaledTileWidth) / bc; x += step) {
			gc.drawLine(x * conf.currentPixelWidth, 0, x * conf.currentPixelWidth, conf.scaledTileHeight);
		}
	}

	protected void paintPixelCursor(GC gc) {
		if (computeCursorIndex(cursorX, cursorY) < conf.width * conf.height * conf.tileColumns * conf.tileRows) {
			gc.setBackground(Constants.BRIGHT_ORANGE);
			gc.fillRectangle(cursorX * conf.pixelSize, cursorY * conf.pixelSize, conf.pixelSize, conf.pixelSize);
		}
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

	public void setCursorMode(CursorMode cursorMode) {
		conf.setCursorMode(cursorMode);
		if (cursorMode == CursorMode.Point) {
			tileRepositoryService
					.setSelection(new Rectangle(0, 0, conf.getWidth() * conf.getTileColumns() * conf.getColumns(),
							conf.getHeight() * conf.getRows() * conf.getTileRows()));
		}
		fireDoDrawAllTiles(this);
		doDrawTile();
	}

	@Override
	public void redrawTiles(List<Integer> selectedTileIndexList, RedrawMode redrawMode, boolean forceUpdate) {
		this.forceUpdate = forceUpdate;
		this.redrawMode = redrawMode;
		if (redrawMode == RedrawMode.DrawSelectedTile) {
			Tile tile = tileRepositoryService.getTile(selectedTileIndexList.get(0));
			if (this.tile != null) {
				this.tile.removeTileListener(this);
			}
			this.tile = tile;
			tile.addTileListener(this);
			doDrawTile();
		} else if (redrawMode == RedrawMode.DrawTemporarySelectedTile) {
			temporaryIndex = selectedTileIndexList.get(0);
			redraw();
		}
	}

	public void setPixel(Tile tile, int x, int y, ImagingWidgetConfiguration conf) {
		Layer layer = tile.getActiveLayer();

		switch (conf.paintMode) {
		case Single: {
			setPixel(layer, x, y, conf);
			break;
		}
		case VerticalMirror: {
			setPixel(layer, x, y, conf);
			int centerX = ((conf.width * conf.tileColumns) / 2);
			int diff = centerX - x - 1;
			setPixel(layer, centerX + diff, y, conf);
			break;
		}
		case HorizontalMirror: {
			setPixel(layer, x, y, conf);
			int centerY = ((conf.height * conf.tileRows) / 2);
			int diff = centerY - y - 1;
			setPixel(layer, x, centerY + diff, conf);
			break;
		}
		case Kaleidoscope: {
			setPixel(layer, x, y, conf);
			int centerX = ((conf.width * conf.tileColumns) / 2);
			int diffX = centerX - x - 1;
			setPixel(layer, centerX + diffX, y, conf);
			int centerY = ((conf.height * conf.tileRows) / 2);
			int diffY = centerY - y - 1;
			setPixel(layer, x, centerY + diffY, conf);
			setPixel(layer, centerX + diffX, centerY + diffY, conf);
			break;
		}
		}
	}

	private void setPixel(Layer layer, int x, int y, ImagingWidgetConfiguration conf) {
		if (x >= 0 && y >= 0 && x < conf.tileWidth && y < conf.tileHeight) {
			layer.getContent()[y * conf.tileWidth + x] = (conf.pencilMode == PencilMode.Draw)
					? layer.getSelectedColorIndex()
					: 0;
		}
	}

	private void paintPixel(GC gc, Tile tile, int x, int y, ImagingWidgetConfiguration conf,
			IColorPaletteProvider colorPaletteProvider) {
		gc.drawImage(imagePainterFactory.getImage(tile, x, y, true, conf, colorPaletteProvider, forceUpdate), 0, 0);
	}

	public void paintTile(GC gc, Tile tile, ImagingWidgetConfiguration conf, IColorPaletteProvider colorPaletteProvider,
			boolean forceUpdate) {
		gc.drawImage(imagePainterFactory.getImage(tile, 0, 0, false, conf, colorPaletteProvider, forceUpdate), 0, 0);
		System.out.println("paint tile");
	}

	@Override
	public void activeLayerChanged(int layer) {
		doDrawTile();
	}

	@Override
	public void colorSelected(int colorNo, int colorIndex) {
		tileRepositoryService.getSelectedTile().setActiveLayerColorIndex(colorNo, colorIndex, true);

	}
}
