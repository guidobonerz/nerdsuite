package de.drazil.nerdsuite.imaging.service;

import java.util.List;

import de.drazil.nerdsuite.model.TileLocation;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;
import de.drazil.nerdsuite.widget.Tile;

public class ShiftService extends AbstractImagingService {
	public final static int UP = 1;
	public final static int DOWN = 2;
	public final static int LEFT = 3;
	public final static int RIGHT = 4;

	@Override
	public boolean needsConfirmation() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void sendResponse(String message, Object data) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isReadyToRun(List<TileLocation> tileLocationList, ImagingWidgetConfiguration configuration) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isProcessConfirmed(boolean confirmAnyProcess) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void each(int action, Tile tile, ImagingWidgetConfiguration configuration) {

		int[] content = tile.getActiveLayer().getContent();

		if (action == UP) {
			for (int x = 0; x < configuration.tileWidth; x++) {
				int b = content[x];
				for (int y = 0; y < configuration.tileHeight - 1; y++) {
					content[x + y * configuration.tileWidth] = content[x + (y + 1) * configuration.tileWidth];
				}
				content[x + (configuration.tileWidth * (configuration.tileHeight - 1))] = b;
			}
		} else if (action == DOWN) {
			for (int x = 0; x < configuration.tileWidth; x++) {
				int b = content[x + (configuration.tileWidth * (configuration.tileHeight - 1))];
				for (int y = configuration.tileHeight - 1; y > 0; y--) {
					content[x + y * configuration.tileWidth] = content[x + (y - 1) * configuration.tileWidth];
				}
				content[x] = b;
			}
		} else if (action == LEFT) {
			for (int y = 0; y < configuration.tileHeight; y++) {
				int b = content[y * configuration.tileWidth];
				for (int x = 0; x < configuration.tileWidth - 1; x++) {
					content[x + y * configuration.tileWidth] = content[(x + 1) + y * configuration.tileWidth];
				}
				content[(configuration.tileWidth + y * configuration.tileWidth) - 1] = b;
			}
		} else if (action == RIGHT) {
			for (int y = 0; y < configuration.tileHeight; y++) {
				int b = content[(configuration.tileWidth + y * configuration.tileWidth) - 1];
				for (int x = configuration.tileWidth - 1; x > 0; x--) {
					content[x + y * configuration.tileWidth] = content[(x - 1) + y * configuration.tileWidth];
				}
				content[y * configuration.tileWidth] = b;
			}
		}
	}
}
