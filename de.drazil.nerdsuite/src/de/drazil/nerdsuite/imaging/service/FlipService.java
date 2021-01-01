package de.drazil.nerdsuite.imaging.service;

import org.eclipse.swt.graphics.Rectangle;

import de.drazil.nerdsuite.enums.TileAction;
import de.drazil.nerdsuite.widget.Tile;

public class FlipService extends AbstractImagingService {

	public final static int HORIZONTAL = 1;
	public final static int VERTICAL = 2;

	@Override
	public void each(int action, int tileIndex, Tile tile, TileRepositoryService repositoryService, TileAction tileAction) {
		int[] content = repositoryService.getActiveLayerFromSelectedTile().getContent();
		int[] brush = repositoryService.getActiveLayerFromSelectedTile().getBrush();
		Rectangle selection = service.getSelection();
		int tileWidth = conf.tileWidth;
		int loops = tile.isMulticolorEnabled() ? 2 : 1;
		flip(action, content, tileWidth, selection, loops);
		if (brush != null && brush.length > 0) {
			flip(action, brush, tileWidth, selection, loops);
		}

	}

	private void flip(int action, int[] data, int tileWidth, Rectangle selection, int loops) {
		if (action == HORIZONTAL) {
			for (int y = selection.y; y < selection.y + selection.height; y++) {
				for (int x = selection.x, c = 0; x < selection.x + selection.width / 2; x++, c++) {
					int a = data[x + (y * tileWidth)];
					int b = data[selection.x + selection.width - 1 - c + (y * tileWidth)];
					data[x + (y * tileWidth)] = b;
					data[selection.x + selection.width - 1 - c + (y * tileWidth)] = a;
				}
			}
		} else if (action == VERTICAL) {
			for (int y = selection.y, c = 0; y < selection.y + selection.height / 2; y++, c++) {
				for (int x = selection.x; x < selection.x + selection.width; x++) {
					int a = data[x + (y * tileWidth)];
					int b = data[x + ((selection.y + selection.height - c - 1) * tileWidth)];
					data[x + (y * tileWidth)] = b;
					data[x + ((selection.y + selection.height - c - 1) * tileWidth)] = a;
				}
			}
		}
	}
}
