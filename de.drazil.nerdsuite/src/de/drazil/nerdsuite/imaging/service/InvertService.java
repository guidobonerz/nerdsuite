package de.drazil.nerdsuite.imaging.service;

import org.eclipse.swt.graphics.Rectangle;

import de.drazil.nerdsuite.enums.TileAction;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;
import de.drazil.nerdsuite.widget.Tile;

public class InvertService extends AbstractImagingService {
	@Override
	public void each(int action, int tileIndex, Tile tile, TileRepositoryService repositoryService, ImagingWidgetConfiguration configuration, TileAction tileAction) {
		int[] content = repositoryService.getActiveLayer().getContent();
		Rectangle r = service.getSelection();
		for (int x = r.x; x < r.x + r.width; x++) {
			for (int y = r.y; y < r.y + r.height; y++) {
				int v = content[x + y * configuration.tileWidth];
				if (configuration.isMultiColorEnabled()) {
					v = (v ^ 0xff) & 0b11;
				} else {
					v = (v ^ 0xff) & 0b01;
				}
				content[x + y * configuration.tileWidth] = v;
			}
		}
	}
}
