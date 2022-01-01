package de.drazil.nerdsuite.imaging.service;

import org.eclipse.swt.graphics.Rectangle;

import de.drazil.nerdsuite.enums.TileAction;
import de.drazil.nerdsuite.widget.Tile;

public class PurgeService extends AbstractImagingService {

	@Override
	public boolean needsConfirmation() {
		return true;
	}

	@Override
	public boolean isProcessConfirmed(boolean confirmAnyProcess) {
		return confirmable.isConfirmed("Do you really want to purge this tile?");
	}

	@Override
	public void each(int action, int tileIndex, Tile tile, TileRepositoryService repositoryService,
			TileAction tileAction) {
		int[] content = tile.getActiveLayer().getContent();
		int[] brush = tile.getActiveLayer().getBrush();
		int tileWidth = conf.getTileWidth();
		Rectangle r = service.getSelection();
		Integer blankValue = repositoryService.getMetadata().getBlankValue();
		for (int x = r.x; x < r.x + r.width; x++) {
			for (int y = r.y; y < r.y + r.height; y++) {
				content[x + y * tileWidth] = 0;
				if (brush != null) {
					brush[x + y * tileWidth] = blankValue;
				}
			}
		}
	}
}
