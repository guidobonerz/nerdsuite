package de.drazil.nerdsuite.imaging.service;

import java.util.List;

import de.drazil.nerdsuite.model.TileLocation;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;
import de.drazil.nerdsuite.widget.Tile;

public class FlipService extends AbstractImagingService {

	public final static int HORIZONTAL = 1;
	public final static int VERTICAL = 2;

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
		if (action == HORIZONTAL) {
			for (int y = 0; y < configuration.height; y++) {
				for (int x = 0; x < configuration.width / 2; x++) {
					int a = content[x + (y * configuration.width)];
					int b = content[configuration.width - 1 - x + (y * configuration.width)];
					content[x + (y * configuration.width)] = b;
					content[configuration.width - 1 - x + (y * configuration.width)] = a;
				}
			}
		} else if (action == VERTICAL) {
			for (int y = 0; y < configuration.height / 2; y++) {
				for (int x = 0; x < configuration.width; x++) {
					int a = content[x + (y * configuration.width)];
					int b = content[x + ((configuration.height - y - 1) * configuration.width)];
					content[x + (y * configuration.width)] = b;
					content[x + ((configuration.height - y - 1) * configuration.width)] = a;
				}
			}
		}
	}

}
