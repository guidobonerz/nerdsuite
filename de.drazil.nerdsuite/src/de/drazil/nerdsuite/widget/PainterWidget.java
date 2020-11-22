package de.drazil.nerdsuite.widget;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.enums.CursorMode;
import de.drazil.nerdsuite.enums.PencilMode;
import de.drazil.nerdsuite.enums.RedrawMode;
import de.drazil.nerdsuite.imaging.service.ImagePainterFactory;
import de.drazil.nerdsuite.model.Image2;
import de.drazil.nerdsuite.model.ProjectMetaData;

public class PainterWidget extends BaseImagingWidget {

	private boolean rangeSelectionStarted = false;
	private int selectedPixelRangeX = 0;
	private int selectedPixelRangeY = 0;
	private int selectedPixelRangeX2 = 0;
	private int selectedPixelRangeY2 = 0;
	private Point startPos;

	private int oldScrollStep = 0;
	private int scrollStep = 0;
	private ScrolledComposite parent;

	public PainterWidget(Composite parent, int style, String owner, IColorPaletteProvider colorPaletteProvider, boolean autowrap) {
		super(parent, style, owner, colorPaletteProvider, autowrap);

		this.parent = (ScrolledComposite) parent;
		this.parent.getHorizontalBar().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				scrollWorkArea(e.x, e.y);
			}
		});
		this.parent.getVerticalBar().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				scrollWorkArea(e.x, e.y);
			}
		});

	}

	@Override
	protected void leftMouseButtonClicked(int modifierMask, int x, int y) {
		if (conf.cursorMode == CursorMode.Point) {
			setPixel(tile, cursorX, cursorY, conf);
			doRedraw(RedrawMode.DrawPixel, ImagePainterFactory.PIXEL);
		}
	}

	@Override
	protected void mouseDragged(int modifierMask, int x, int y) {
		if (conf.cursorMode == CursorMode.SelectRectangle) {
			computeRangeSelection(tileCursorX, tileCursorY, 1, (modifierMask & SWT.SHIFT) == SWT.SHIFT);
			doRedraw(RedrawMode.DrawSelectedTile, ImagePainterFactory.UPDATE);
		} else if (conf.cursorMode == CursorMode.Move || (conf.cursorMode == CursorMode.Point && (this.modifierMask & (SWT.SHIFT + SWT.CTRL)) == SWT.SHIFT + SWT.CTRL)) {
			int xoff = x - startPos.x;
			int yoff = y - startPos.y;
			scrollWorkArea(xoff, yoff);
		} else if (conf.cursorMode == CursorMode.Point && cursorChanged) {
			setPixel(tile, cursorX, cursorY, conf);
			doRedraw(RedrawMode.DrawPixel, ImagePainterFactory.PIXEL);
		}
	}

	private void scrollWorkArea(int xoff, int yoff) {
		int xo = parent.getHorizontalBar().getSelection() - xoff;
		int yo = parent.getVerticalBar().getSelection() - yoff;
		parent.setOrigin(xo, yo);
		tileRepositoryService.getSelectedTile().setOrigin(new Point(xo, yo));
	}

	@Override
	protected void leftMouseButtonPressed(int modifierMask, int x, int y) {
		takePosition = true;
		if (conf.cursorMode == CursorMode.SelectRectangle) {
			computeRangeSelection(tileCursorX, tileCursorY, 0, false);
			doRedraw(RedrawMode.DrawSelectedTile, ImagePainterFactory.UPDATE);
		} else if (conf.cursorMode == CursorMode.Move || (conf.cursorMode == CursorMode.Point && (this.modifierMask & (SWT.SHIFT + SWT.CTRL)) == SWT.SHIFT + SWT.CTRL)) {
			startPos = new Point(x, y);
		}
	}

	@Override
	protected void leftMouseButtonReleased(int modifierMask, int x, int y) {

		if (conf.cursorMode == CursorMode.SelectRectangle) {
			if (rangeSelectionStarted) {
				rangeSelectionStarted = false;
				computeRangeSelection(tileCursorX, tileCursorY, 2, (modifierMask & SWT.SHIFT) == SWT.SHIFT);
			}
		} else if (conf.cursorMode == CursorMode.Point) {
			fireDoRedraw(RedrawMode.DrawSelectedTile, null, ImagePainterFactory.UPDATE);
		}
	}

	@Override
	protected void mouseEnter(int modifierMask, int x, int y) {
		doRedraw(RedrawMode.DrawSelectedTile, ImagePainterFactory.READ);
	}

	@Override
	protected void mouseExit(int modifierMask, int x, int y) {
		doRedraw(RedrawMode.DrawSelectedTile, ImagePainterFactory.READ);
	}

	@Override
	protected void mouseMove(int modifierMask, int x, int y) {
		if (conf.cursorMode == CursorMode.Point && cursorChanged) {
			doRedraw(RedrawMode.DrawSelectedTile, ImagePainterFactory.READ);
		}
	}

	@Override
	protected void mouseScrolled(int modifierMask, int x, int y, int count) {
		oldScrollStep = scrollStep;
		scrollStep += count;

		if (Math.abs(scrollStep) % 3 == 0) {
			boolean direction = oldScrollStep < scrollStep;
			int step = direction ? 2 : -2;
			if (conf.pixelWidth + step >= 8 && conf.pixelWidth + step <= 32) {
				conf.pixelWidth += step;
				conf.pixelHeight = conf.pixelWidth;
				recalc();
				conf.computeDimensions(tileRepositoryService.getSize());
				imagePainterFactory.resetCache();
				// imagePainterFactory.drawSelectedTile(tileRepositoryService,
				// colorPaletteProvider, conf);
				doRedraw(RedrawMode.DrawSelectedTile, ImagePainterFactory.READ);
				((ScrolledComposite) getParent()).setMinSize(conf.fullWidthPixel, conf.fullHeightPixel);
			}
		}
	}

	private int computeCursorIndex(int x, int y) {
		return (x + (y * conf.tileWidth));
	}

	private void computeRangeSelection(int tileCursorX, int tileCursorY, int mode, boolean enabledSquareSelection) {
		int x = tileCursorX < 0 ? 0 : tileCursorX;
		int y = tileCursorY < 0 ? 0 : tileCursorY;

		if (mode == 0) {
			selectedPixelRangeX = 0;
			selectedPixelRangeY = 0;
			selectedPixelRangeX2 = 0;
			selectedPixelRangeY2 = 0;
		} else if (mode == 1) {
			if (!rangeSelectionStarted) {
				selectedPixelRangeX = x;
				selectedPixelRangeY = y;
				rangeSelectionStarted = true;
			} else {
				selectedPixelRangeX2 = enabledSquareSelection && y - selectedPixelRangeY > x - selectedPixelRangeX ? selectedPixelRangeX + (selectedPixelRangeY2 - selectedPixelRangeY) : x;
				selectedPixelRangeY2 = enabledSquareSelection && x - selectedPixelRangeX > y - selectedPixelRangeY ? selectedPixelRangeY + (selectedPixelRangeX2 - selectedPixelRangeX) : y;
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

	protected void paintControl(GC gc, RedrawMode redrawMode, boolean paintPixelGrid, boolean paintSeparator, boolean paintTileGrid, boolean paintTileSubGrid, boolean paintSelection,
			boolean paintTileCursor, boolean paintTelevisionMode) {
		gc.drawImage(imagePainterFactory.createOrUpdateBaseImage("REPOSITORY", Constants.BLACK).getImage(), 0, 0);
		if (redrawMode == RedrawMode.DrawPixel) {
			// paintPixel(gc, cursorX, cursorY, action);
		} else if (redrawMode == RedrawMode.DrawTemporarySelectedTile) {
			// paintTile(gc, temporaryIndex, conf, colorPaletteProvider, action);
		} else {
			gc.drawImage(
					imagePainterFactory.createOrUpdateTile(tileRepositoryService.getSelectedTile(), tileRepositoryService.getActiveLayerFromSelectedTile().getSelectedColorIndex(), false).getImage(),
					0, 0);
		}

		if (paintPixelGrid) {

			gc.drawImage(imagePainterFactory.getGridLayer().getImage(), 0, 0);
			if (paintSeparator) {
				// paintSeparator(gc);
			}
			if (paintTileSubGrid) {
				// paintTileSubGrid(gc);
			}

		}

		if (conf.cursorMode == CursorMode.SelectRectangle) {
			paintRangeSelection(gc);
		} else if (conf.cursorMode == CursorMode.Point) {
			paintPixelCursor(gc);
		} else {
		}

		action = ImagePainterFactory.NONE;
		redrawMode = RedrawMode.DrawNothing;
	}

	/*
	 * private void paintTileSubGrid(GC gc) {
	 * gc.setForeground(Constants.TILE_SUB_GRID_COLOR); for (int y = height; y <
	 * tileHeight; y += height) { gc.drawLine(0, y * pixelHeight,
	 * conf.scaledTileWidth, y * pixelHeight); }
	 * gc.setForeground(Constants.TILE_SUB_GRID_COLOR); for (int x = width; x <
	 * tileWidth; x += width) { gc.drawLine(x * pixelWidth, 0, x * pixelWidth,
	 * conf.scaledTileHeight); } }
	 * 
	 * private void paintSeparator(GC gc) {
	 * gc.setForeground(Constants.BYTE_SEPARATOR_COLOR); int bc =
	 * conf.pixelConfig.bitCount; int step = (8 * bc); for (int x = step; x <
	 * (conf.scaledTileWidth) / bc; x += step) { gc.drawLine(x * pixelWidth, 0, x *
	 * pixelWidth, conf.scaledTileHeight); } }
	 */
	private void paintPixelCursor(GC gc) {
		if (computeCursorIndex(cursorX, cursorY) < conf.tileSize) {
			if (tileRepositoryService.hasReference()) {
				Tile tile = tileRepositoryService.getSelectedTile();
				int brushIndex = tileRepositoryReferenceService.getSelectedTileIndex(true);
				Tile refTile = tileRepositoryReferenceService.getTile(brushIndex, true);
				ImagePainterFactory ipf = ImagePainterFactory.getImageFactory(tileRepositoryReferenceService.getMetadata().getId());
				gc.drawImage(ipf.createOrUpdateTile(refTile, tile.getColorIndex(1), false).getImage(), cursorX * conf.pixelPaintWidth, cursorY * conf.pixelPaintHeight);
			} else {
				// gc.setForeground(colo);
				// gc.drawRectangle((cursorX * conf.pixelPaintWidth) - 1, (cursorY *
				// conf.pixelPaintHeight) - 1, conf.pixelPaintWidth + 1, conf.pixelPaintHeight +
				// 1);
			}

			gc.setForeground(Constants.BRIGHT_ORANGE);
			gc.drawRectangle((cursorX * conf.pixelPaintWidth) - 1, (cursorY * conf.pixelPaintHeight) - 1, conf.pixelPaintWidth + 1, conf.pixelPaintHeight + 1);
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

			gc.drawRectangle(x1 * conf.pixelPaintWidth, y1 * conf.pixelPaintHeight, (x2 - x1) * conf.pixelPaintWidth + conf.pixelPaintWidth, (y2 - y1) * conf.pixelPaintHeight + conf.pixelPaintHeight);
		}
	}

	public void setCursorMode(CursorMode cursorMode) {
		conf.cursorMode = cursorMode;
		if (cursorMode == CursorMode.Point) {
			setCursor(new Cursor(getShell().getDisplay(), SWT.CURSOR_ARROW));
			tileRepositoryService.setSelection(new Rectangle(0, 0, conf.tileWidth, conf.tileHeight));
		} else if (cursorMode == CursorMode.Move) {
			setCursor(new Cursor(getShell().getDisplay(), SWT.CURSOR_SIZEALL));
		}
		doRedraw(RedrawMode.DrawSelectedTile, ImagePainterFactory.READ);
	}

	@Override
	public void redrawTiles(List<Integer> selectedTileIndexList, RedrawMode redrawMode, int action) {
		if (redrawMode == RedrawMode.DrawSelectedTile || redrawMode == RedrawMode.DrawSelectedTiles) {
			// Tile tile = tileRepositoryService.getTile(selectedTileIndexList.get(0));
			// if (this.tile != null) {
			// this.tile.removeTileListener(this);
			// }
			// this.tile = tile;
			// tile.addTileListener(this);
		} else if (redrawMode == RedrawMode.DrawTemporarySelectedTile) {
			temporaryIndex = selectedTileIndexList.get(0);
		}
		doRedraw(redrawMode, action);
		parent.setOrigin(tileRepositoryService.getSelectedTile().getOrigin());

	}

	public void setPixel(Tile tile, int x, int y, ImagingWidgetConfiguration conf) {
		Layer layer = tileRepositoryService.getSelectedTile().getActiveLayer();

		switch (conf.paintMode) {
		case Single: {
			setPixel(layer, x, y, conf);
			break;
		}
		case VerticalMirror: {
			setPixel(layer, x, y, conf);
			int centerX = conf.tileWidth / 2;
			int diff = centerX - x - 1;
			setPixel(layer, centerX + diff, y, conf);
			break;
		}
		case HorizontalMirror: {
			setPixel(layer, x, y, conf);
			int centerY = conf.tileHeight / 2;
			int diff = centerY - y - 1;
			setPixel(layer, x, centerY + diff, conf);
			break;
		}
		case Kaleidoscope: {
			setPixel(layer, x, y, conf);
			int centerX = conf.tileWidth / 2;
			int diffX = centerX - x - 1;
			setPixel(layer, centerX + diffX, y, conf);
			int centerY = conf.tileHeight / 2;
			int diffY = centerY - y - 1;
			setPixel(layer, x, centerY + diffY, conf);
			setPixel(layer, centerX + diffX, centerY + diffY, conf);
			break;
		}
		}
	}

	private void setPixel(Layer layer, int x, int y, ImagingWidgetConfiguration conf) {
		if (x >= 0 && y >= 0 && x < conf.tileWidth && y < conf.tileHeight) {
			int colorIndex = (conf.pencilMode == PencilMode.Draw) ? layer.getSelectedColorIndex() : 0;
			int colorId = tileRepositoryService.getSelectedTile().getActiveLayer().getColorPalette().get(colorIndex);
			int offset = y * conf.tileWidth + x;

			layer.getContent()[offset] = colorId;

			if (tileRepositoryReferenceService != null) {
				int brush[] = layer.getBrush();
				if (brush == null) {
					layer.resetBrush(tileRepositoryService.getMetadata().getBlankValue());
				}
				int i = tileRepositoryReferenceService.getSelectedTileIndex();
				layer.getBrush()[offset] = i;

			}
		}
	}

	private void paintPixel(GC gc, int x, int y, boolean isDirty) {

		Tile tile = tileRepositoryService.getSelectedTile();
		Layer layer = tile.getActiveLayer();
		String name = String.format("%s_%s", tile.getName(), layer.getName());
		Image2 imageInternal = imagePainterFactory.createLayer();
		GC gcLayer = new GC(imageInternal.getImage());
		int offset = conf.tileWidth * y + x;

		if (tileRepositoryService.hasReference()) {
			int brushIndex = tileRepositoryReferenceService.getSelectedTileIndex(true);
			Tile refTile = tileRepositoryReferenceService.getTile(brushIndex, true);
			ImagePainterFactory ipf = ImagePainterFactory.getImageFactory(tileRepositoryReferenceService.getMetadata().getId());
			gcLayer.drawImage(ipf.createOrUpdateTile(refTile, layer.getContent()[offset]).getImage(), cursorX * conf.pixelPaintWidth, cursorY * conf.pixelPaintHeight);
		} else {
			gcLayer.setBackground(colorPaletteProvider.getColorByIndex(layer.getContent()[offset]));
			gcLayer.fillRectangle(x * conf.pixelPaintWidth, y * conf.pixelPaintHeight, conf.pixelPaintWidth, conf.pixelPaintHeight);
		}
		gcLayer.dispose();

	}

	@Override
	public void activeLayerChanged(int layer) {
		doRedraw(RedrawMode.DrawSelectedTile, ImagePainterFactory.UPDATE);
	}

	@Override
	public void colorSelected(int colorNo, int colorIndex) {
		tileRepositoryService.getSelectedTile().setActiveLayerColorIndex(colorNo, colorIndex, true);
	}

	@Override
	public void redrawCalculatedArea() {
		redraw(0, 0, conf.fullWidthPixel, conf.fullHeightPixel, false);
		// redraw(cursorX * 8, cursorY * 8, 8, 8, false);
		// System.out.println("redraw calculated");
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point(conf.fullWidthPixel, conf.fullHeightPixel);
	}

	@Override
	protected String getViewerConfigName() {
		return ProjectMetaData.PAINTER_CONFIG;
	}
}
