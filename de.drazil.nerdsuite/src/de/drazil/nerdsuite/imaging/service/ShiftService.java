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
			for (int x = 0; x < configuration.width; x++) {
				int b = content[x];
				for (int y = 0; y < configuration.height - 1; y++) {
					content[x + y * configuration.width] = content[x + (y + 1) * configuration.width];
				}
				content[x + (configuration.width * (configuration.height - 1))] = b;
			}
		} else if (action == DOWN) {
			for (int x = 0; x < configuration.width; x++) {
				int b = content[x + (configuration.width * (configuration.height - 1))];
				for (int y = configuration.height - 1; y > 0; y--) {
					content[x + y * configuration.width] = content[x + (y - 1) * configuration.width];
				}
				content[x] = b;
			}
		} else if (action == LEFT) {
			for (int y = 0; y < configuration.height; y++) {
				int b = content[y * configuration.width];
				for (int x = 0; x < configuration.width - 1; x++) {
					content[x + y * configuration.width] = content[(x + 1) + y * configuration.width];
				}
				content[(configuration.width + y * configuration.width) - 1] = b;
			}
		} else if (action == RIGHT) {
			for (int y = 0; y < configuration.height; y++) {
				int b = content[(configuration.width + y * configuration.width) - 1];
				for (int x = configuration.width - 1; x > 0; x--) {
					content[x + y * configuration.width] = content[(x - 1) + y * configuration.width];
				}
				content[y * configuration.width] = b;
			}
		}
	}
}
