package de.drazil.nerdsuite.imaging.service;

import org.eclipse.swt.graphics.Rectangle;

import de.drazil.nerdsuite.enums.TileAction;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;
import de.drazil.nerdsuite.widget.Tile;

public class FlipService extends AbstractImagingService {

	public final static int HORIZONTAL = 1;
	public final static int VERTICAL = 2;

	@Override
	public void each(int action, Tile tile, ImagingWidgetConfiguration configuration, TileAction tileAction) {
		int[] content = tile.getActiveLayer().getContent();
		Rectangle r = service.getSelection();
		if (action == HORIZONTAL) {
			for (int y = r.y; y < r.y + r.height; y++) {
				for (int x = r.x, c = 0; x < r.x + r.width / 2; x++, c++) {
					int a = content[x + (y * configuration.tileWidth)];
					int b = content[r.x + r.width - 1 - c + (y * configuration.tileWidth)];
					content[x + (y * configuration.tileWidth)] = b;
					content[r.x + r.width - 1 - c + (y * configuration.tileWidth)] = a;
				}
			}
		} else if (action == VERTICAL) {
			for (int y = r.y, c = 0; y < r.y + r.height / 2; y++, c++) {
				for (int x = r.x; x < r.x + r.width; x++) {
					int a = content[x + (y * configuration.tileWidth)];
					int b = content[x + ((r.y + r.height - c - 1) * configuration.tileWidth)];
					content[x + (y * configuration.tileWidth)] = b;
					content[x + ((r.y + r.height - c - 1) * configuration.tileWidth)] = a;
				}
			}
		}
	}

}
