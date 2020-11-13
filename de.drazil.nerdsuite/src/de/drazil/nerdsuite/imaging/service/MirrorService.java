package de.drazil.nerdsuite.imaging.service;

import org.eclipse.swt.graphics.Rectangle;

import de.drazil.nerdsuite.enums.TileAction;
import de.drazil.nerdsuite.model.ProjectMetaData;
import de.drazil.nerdsuite.widget.Tile;

public class MirrorService extends AbstractImagingService {
	public final static int UPPER_HALF = 1;
	public final static int LOWER_HALF = 2;
	public final static int LEFT_HALF = 3;
	public final static int RIGHT_HALF = 4;

	@Override
	public void each(int action, int tileIndex, Tile tile, TileRepositoryService repositoryService, TileAction tileAction, ProjectMetaData metadata) {
		int[] content = repositoryService.getActiveLayerFromSelectedTile().getContent();
		Rectangle r = service.getSelection();
		int tileWidth = metadata.getTileWidth();
		if (action == UPPER_HALF) {
			for (int y = r.y, c = 0; y < r.y + r.height / 2; y++, c++) {
				for (int x = r.x; x < r.x + r.width; x++) {
					content[x + ((r.y + r.height - c - 1) * tileWidth)] = content[x + (y * tileWidth)];
				}
			}
		} else if (action == LOWER_HALF) {
			for (int y = r.y, c = 0; y < r.y + r.height / 2; y++, c++) {
				for (int x = r.x; x < r.x + r.width; x++) {
					content[x + (y * tileWidth)] = content[x + ((r.y + r.height - c - 1) * tileWidth)];
				}
			}
		} else if (action == LEFT_HALF) {
			for (int y = r.y; y < r.y + r.height; y++) {
				for (int x = r.x, c = 0; x < r.x + r.width / 2; x++, c++) {
					content[r.x + r.width - 1 - c + (y * tileWidth)] = content[x + (y * tileWidth)];
				}
			}
		} else if (action == RIGHT_HALF) {
			for (int y = r.y; y < r.y + r.height; y++) {
				for (int x = r.x, c = 0; x < r.x + r.width / 2; x++, c++) {
					content[x + (y * tileWidth)] = content[r.x + r.width - 1 - c + (y * tileWidth)];
				}
			}
		}
	}

}
