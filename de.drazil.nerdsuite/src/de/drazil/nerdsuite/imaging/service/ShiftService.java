package de.drazil.nerdsuite.imaging.service;

import org.eclipse.swt.graphics.Rectangle;

import de.drazil.nerdsuite.enums.TileAction;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;
import de.drazil.nerdsuite.widget.Tile;

public class ShiftService extends AbstractImagingService {
	public final static int UP = 1;
	public final static int DOWN = 2;
	public final static int LEFT = 3;
	public final static int RIGHT = 4;

	@Override
	public void each(int action, int tileIndex, Tile tile, TileRepositoryService repositoryService, ImagingWidgetConfiguration configuration, TileAction tileAction) {

		int[] content = repositoryService.getActiveLayerFromSelectedTile().getContent();
		Rectangle r = service.getSelection();

		if (action == UP) {
			for (int x = r.x; x < r.x + r.width; x++) {
				int b = content[x + r.y * configuration.tileWidth];
				for (int y = r.y; y < r.y + r.height - 1; y++) {
					content[x + y * configuration.tileWidth] = content[x + (y + 1) * configuration.tileWidth];
				}
				content[x + (configuration.tileWidth * (r.y + r.height - 1))] = b;
			}
		} else if (action == DOWN) {
			for (int x = r.x; x < r.x + r.width; x++) {
				int b = content[x + (configuration.tileWidth * (r.y + r.height - 1))];
				for (int y = r.y + r.height - 1; y > r.y; y--) {
					content[x + y * configuration.tileWidth] = content[x + (y - 1) * configuration.tileWidth];
				}
				content[x + r.y * configuration.tileWidth] = b;
			}
		} else if (action == LEFT) {
			for (int y = r.y; y < r.y + r.height; y++) {
				int b = content[r.x + y * configuration.tileWidth];
				for (int x = r.x; x < r.x + r.width - 1; x++) {
					content[x + y * configuration.tileWidth] = content[(x + 1) + y * configuration.tileWidth];
				}
				content[(r.x + r.width + y * configuration.tileWidth) - 1] = b;
			}
		} else if (action == RIGHT) {
			for (int y = r.y; y < r.y + r.height; y++) {
				int b = content[(r.x + r.width + y * configuration.tileWidth) - 1];
				for (int x = r.x + r.width - 1; x > r.x; x--) {
					content[x + y * configuration.tileWidth] = content[(x - 1) + y * configuration.tileWidth];
				}
				content[r.x + y * configuration.tileWidth] = b;
			}
		}
	}
}
