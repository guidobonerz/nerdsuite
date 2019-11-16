package de.drazil.nerdsuite.imaging.service;

import java.util.List;

import org.eclipse.swt.graphics.Rectangle;

import de.drazil.nerdsuite.enums.TileAction;
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
	public void each(int action, Tile tile, ImagingWidgetConfiguration configuration, TileAction tileAction) {

		int[] content = tile.getActiveLayer().getContent();

		Rectangle r = tile.getSelection();

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
