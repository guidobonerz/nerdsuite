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

		int[] content = repositoryService.getActiveLayerFromSelectedTile().getContent();
		Rectangle r = service.getSelection();
		int tileWidth = conf.getTileWidth();
		if (action == UP) {
			for (int x = r.x; x < r.x + r.width; x++) {
				int b = content[x + r.y * tileWidth];
				for (int y = r.y; y < r.y + r.height - 1; y++) {
					content[x + y * tileWidth] = content[x + (y + 1) * tileWidth];
				}
				content[x + (conf.getTileWidth() * (r.y + r.height - 1))] = b;
			}
		} else if (action == DOWN) {
			for (int x = r.x; x < r.x + r.width; x++) {
				int b = content[x + (tileWidth * (r.y + r.height - 1))];
				for (int y = r.y + r.height - 1; y > r.y; y--) {
					content[x + y * tileWidth] = content[x + (y - 1) * tileWidth];
				}
				content[x + r.y * tileWidth] = b;
			}
		} else if (action == LEFT) {
			for (int y = r.y; y < r.y + r.height; y++) {
				int b = content[r.x + y * tileWidth];
				for (int x = r.x; x < r.x + r.width - 1; x++) {
					content[x + y * tileWidth] = content[(x + 1) + y * tileWidth];
				}
				content[(r.x + r.width + y * conf.getTileWidth()) - 1] = b;
			}
		} else if (action == RIGHT) {
			for (int y = r.y; y < r.y + r.height; y++) {
				int b = content[(r.x + r.width + y * tileWidth) - 1];
				for (int x = r.x + r.width - 1; x > r.x; x--) {
					content[x + y * tileWidth] = content[(x - 1) + y * tileWidth];
				}
				content[r.x + y * tileWidth] = b;
			}
		}
	}
}
