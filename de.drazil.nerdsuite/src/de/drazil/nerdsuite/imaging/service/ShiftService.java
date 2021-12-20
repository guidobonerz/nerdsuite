package de.drazil.nerdsuite.imaging.service;

import org.eclipse.swt.graphics.Rectangle;

import de.drazil.nerdsuite.enums.TileAction;
import de.drazil.nerdsuite.widget.Tile;

public class ShiftService extends AbstractImagingService {
	public final static int UP = 1;
	public final static int DOWN = 2;
	public final static int LEFT = 3;
	public final static int RIGHT = 4;

	@Override
	public void each(int action, int tileIndex, Tile tile, TileRepositoryService repositoryService, TileAction tileAction) {

		int[] content = tile.getActiveLayer().getContent();
		int[] brush = tile.getActiveLayer().getBrush();
		Rectangle selection = service.getSelection();
		int tileWidth = conf.getTileWidth();
		int loops = tile.isMulticolorEnabled() ? 2 : 1;
		shift(action, content, tileWidth, selection, loops);
		if (brush != null && brush.length > 0) {
			shift(action, brush, tileWidth, selection, loops);
		}
	}

	private void shift(int action, int[] data, int tileWidth, Rectangle selection, int loops) {
		if (action == UP) {
			for (int x = selection.x; x < selection.x + selection.width; x++) {
				int b = data[x + selection.y * tileWidth];
				for (int y = selection.y; y < selection.y + selection.height - 1; y++) {
					data[x + y * tileWidth] = data[x + (y + 1) * tileWidth];
				}
				data[x + (conf.getTileWidth() * (selection.y + selection.height - 1))] = b;
			}
		} else if (action == DOWN) {
			for (int x = selection.x; x < selection.x + selection.width; x++) {
				int b = data[x + (tileWidth * (selection.y + selection.height - 1))];
				for (int y = selection.y + selection.height - 1; y > selection.y; y--) {
					data[x + y * tileWidth] = data[x + (y - 1) * tileWidth];
				}
				data[x + selection.y * tileWidth] = b;
			}
		} else if (action == LEFT) {
			for (int l = 0; l < loops; l++) {
				for (int y = selection.y; y < selection.y + selection.height; y++) {
					int b = data[selection.x + y * tileWidth];
					for (int x = selection.x; x < selection.x + selection.width - 1; x++) {
						data[x + y * tileWidth] = data[(x + 1) + y * tileWidth];
					}
					data[(selection.x + selection.width + y * conf.getTileWidth()) - 1] = b;
				}
			}
		} else if (action == RIGHT) {
			for (int l = 0; l < loops; l++) {
				for (int y = selection.y; y < selection.y + selection.height; y++) {
					int b = data[(selection.x + selection.width + y * tileWidth) - 1];
					for (int x = selection.x + selection.width - 1; x > selection.x; x--) {
						data[x + y * tileWidth] = data[(x - 1) + y * tileWidth];
					}
					data[selection.x + y * tileWidth] = b;
				}
			}
		}
	}
}
