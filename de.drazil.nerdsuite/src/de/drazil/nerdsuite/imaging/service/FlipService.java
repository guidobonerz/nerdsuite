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
		Rectangle r = service.getSelection();
		int tileWidth = conf.tileWidth;
		if (action == HORIZONTAL) {
			for (int y = r.y; y < r.y + r.height; y++) {
				for (int x = r.x, c = 0; x < r.x + r.width / 2; x++, c++) {
					int a = content[x + (y * tileWidth)];
					int b = content[r.x + r.width - 1 - c + (y * tileWidth)];
					content[x + (y * tileWidth)] = b;
					content[r.x + r.width - 1 - c + (y * tileWidth)] = a;
				}
			}
		} else if (action == VERTICAL) {
			for (int y = r.y, c = 0; y < r.y + r.height / 2; y++, c++) {
				for (int x = r.x; x < r.x + r.width; x++) {
					int a = content[x + (y * tileWidth)];
					int b = content[x + ((r.y + r.height - c - 1) * tileWidth)];
					content[x + (y * tileWidth)] = b;
					content[x + ((r.y + r.height - c - 1) * tileWidth)] = a;
				}
			}
		}
	}

}
