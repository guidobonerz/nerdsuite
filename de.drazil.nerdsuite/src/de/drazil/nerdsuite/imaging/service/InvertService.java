package de.drazil.nerdsuite.imaging.service;

import org.eclipse.swt.graphics.Rectangle;

import de.drazil.nerdsuite.enums.TileAction;
import de.drazil.nerdsuite.widget.Tile;

public class InvertService extends AbstractImagingService {
	@Override
	public void each(int action, int tileIndex, Tile tile, TileRepositoryService repositoryService, TileAction tileAction) {
		int[] content = repositoryService.getActiveLayerFromSelectedTile().getContent();
		int tileWidth = conf.getTileWidth();
		Rectangle r = service.getSelection();
		for (int x = r.x; x < r.x + r.width; x++) {
			for (int y = r.y; y < r.y + r.height; y++) {
				int v = content[x + y * tileWidth];
				if (tile.isMulticolorEnabled()) {
					v = (v ^ 0xff) & 0b11;
				} else {
					v = (v ^ 0xff) & 0b01;
				}
				content[x + y * tileWidth] = v;
			}
		}
	}
}
