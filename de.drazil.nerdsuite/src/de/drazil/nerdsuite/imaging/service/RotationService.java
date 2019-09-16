package de.drazil.nerdsuite.imaging.service;

import java.util.List;

import de.drazil.nerdsuite.model.TileLocation;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;
import de.drazil.nerdsuite.widget.Tile;

public class RotationService extends AbstractImagingService {
	public final static int CW = 1;
	public final static int CCW = 2;

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

	private boolean checkIfSquareBase() {
		int w = imagingWidgetConfiguration.currentWidth * imagingWidgetConfiguration.tileColumns;
		int h = imagingWidgetConfiguration.height * imagingWidgetConfiguration.tileRows;
		return w == h;
	}

	@Override
	public void each(int action, Tile tile, ImagingWidgetConfiguration configuration) {
		int[] content = tile.getActiveLayer().getContent();
		int[] targetContent = new int[content.length];
		for (int y = 0; y < configuration.tileHeight; y++) {
			for (int x = 0; x < configuration.tileWidth; x++) {
				int b = content[x + (y * configuration.tileWidth)];
				int o = 0;
				if (action == CCW) {
					o = (configuration.tileSize) - (configuration.tileWidth)
							- (configuration.tileWidth * x) + y;
				} else if (action == CW) {
					o = (configuration.tileWidth) - y - 1
							+ (x * configuration.tileWidth);
				}
				if (o >= 0 && o < (configuration.tileSize)) {
					targetContent[o] = b;
				}
			}
		}
		tile.getActiveLayer().setContent(targetContent);
	}

}
