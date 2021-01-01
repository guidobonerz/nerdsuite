package de.drazil.nerdsuite.imaging.service;

import org.eclipse.swt.graphics.Rectangle;

import de.drazil.nerdsuite.enums.TileAction;
import de.drazil.nerdsuite.widget.Tile;

public class RotationService extends AbstractImagingService {
	public final static int CW = 1;
	public final static int CCW = 2;

	@Override
	public boolean needsConfirmation() {
		return checkIfSquareBase();
	}

	@Override
	public boolean isProcessConfirmed(boolean confirmAnyProcess) {
		return confirmable.isConfirmed("Tile does not have a square base\nDo you really want to rotate this tile?\n\nTo prevent data loss click No");
	}

	private boolean checkIfSquareBase() {
		Rectangle r = service.getSelection();
		return service.getSelectedTile().isMulticolorEnabled() ? r.width / 2 != r.height : r.width != r.height;
	}

	@Override
	public void each(int action, int tileIndex, Tile tile, TileRepositoryService repositoryService, TileAction tileAction) {
		Rectangle selection = service.getSelection();
		int[] content = repositoryService.getActiveLayerFromSelectedTile().getContent();
		int[] brush = repositoryService.getActiveLayerFromSelectedTile().getBrush();
		int width = selection.width / (tile.isMulticolorEnabled() ? 2 : 1);
		int[] sourceContentSelection = new int[width * selection.height];
		int[] sourceBrushSelection = new int[width * selection.height];
		int[] targetContentSelection = new int[width * selection.height];
		int[] targetBrushSelection = new int[width * selection.height];
		int tileWidth = conf.getTileWidth();
		boolean multicolor = tile.isMulticolorEnabled();
		rotate(action, content, sourceContentSelection, targetContentSelection, tileWidth, selection, multicolor, width);
		if (brush != null && brush.length > 0) {
			rotate(action, brush, sourceBrushSelection, targetBrushSelection, tileWidth, selection, multicolor, width);
		}

	}

	private void rotate(int action, int[] data, int[] source, int[] target, int tileWidth, Rectangle selection, boolean multicolor, int width) {
		for (int x = selection.x, cx = 0; x < selection.x + selection.width; x += (multicolor ? 2 : 1), cx++) {
			for (int y = selection.y, cy = 0; y < selection.y + selection.height; y++, cy++) {
				source[cx + cy * width] = data[x + y * tileWidth];
			}
		}

		for (int y = 0; y < selection.height; y++) {
			for (int x = 0; x < width; x++) {
				int b = source[x + (y * width)];
				int o = 0;
				if (action == CCW) {
					o = (target.length) - (width) - (width * x) + y;
				} else if (action == CW) {
					o = (width) - y - 1 + (x * width);
				}
				if (o >= 0 && o < (target.length)) {
					target[o] = b;
				}
			}
		}

		for (int x = selection.x, cx = 0; x < selection.x + selection.width; x += (multicolor ? 2 : 1), cx++) {
			for (int y = selection.y, cy = 0; y < selection.y + selection.height; y++, cy++) {
				data[x + y * tileWidth] = target[cx + cy * width];
				if (multicolor) {
					data[x + 1 + y * tileWidth] = target[cx + cy * width];
				}
			}
		}
	}

}
