package de.drazil.nerdsuite.imaging.service;

import org.eclipse.swt.graphics.Rectangle;

import de.drazil.nerdsuite.enums.TileAction;
import de.drazil.nerdsuite.widget.Tile;

public class SwapService extends AbstractImagingService {
	@Override
	public void each(int action, int tileIndex, Tile tile, TileRepositoryService repositoryService, TileAction tileAction) {
		int[] content = tile.getActiveLayer().getContent();
		int tileWidth = conf.getTileWidth();
		Rectangle r = service.getSelection();
		
	}
}
