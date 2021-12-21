package de.drazil.nerdsuite.imaging.service;

import org.eclipse.swt.graphics.Rectangle;

import de.drazil.nerdsuite.enums.TileAction;
import de.drazil.nerdsuite.widget.Tile;

public class MirrorService extends AbstractImagingService {
	public final static int UPPER_HALF = 1;
	public final static int LOWER_HALF = 2;
	public final static int LEFT_HALF = 3;
	public final static int RIGHT_HALF = 4;

	@Override
	public void each(int action, int tileIndex, Tile tile, TileRepositoryService repositoryService, TileAction tileAction) {
		int[] content = tile.getActiveLayer().getContent();
		int[] brush = tile.getActiveLayer().getBrush();
		Rectangle selection = service.getSelection();
		int tileWidth = conf.tileWidth;
		int loops = tile.isMulticolorEnabled() ? 2 : 1;
		mirror(action, content, tileWidth, selection, loops);
		if (brush != null && brush.length > 0) {
			mirror(action, brush, tileWidth, selection, loops);
		}

	}

	private void mirror(int action, int[] data, int tileWidth, Rectangle selection, int loops) {
		if (action == UPPER_HALF) {
			for (int y = selection.y, c = 0; y < selection.y + selection.height / 2; y++, c++) {
				for (int x = selection.x; x < selection.x + selection.width; x++) {
					data[x + ((selection.y + selection.height - c - 1) * tileWidth)] = data[x + (y * tileWidth)];
				}
			}
		} else if (action == LOWER_HALF) {
			for (int y = selection.y, c = 0; y < selection.y + selection.height / 2; y++, c++) {
				for (int x = selection.x; x < selection.x + selection.width; x++) {
					data[x + (y * tileWidth)] = data[x + ((selection.y + selection.height - c - 1) * tileWidth)];
				}
			}
		} else if (action == LEFT_HALF) {
			for (int y = selection.y; y < selection.y + selection.height; y++) {
				for (int x = selection.x, c = 0; x < selection.x + selection.width / 2; x++, c++) {
					data[selection.x + selection.width - 1 - c + (y * tileWidth)] = data[x + (y * tileWidth)];
				}
			}
		} else if (action == RIGHT_HALF) {
			for (int y = selection.y; y < selection.y + selection.height; y++) {
				for (int x = selection.x, c = 0; x < selection.x + selection.width / 2; x++, c++) {
					data[x + (y * tileWidth)] = data[selection.x + selection.width - 1 - c + (y * tileWidth)];
				}
			}
		}
	}

}
