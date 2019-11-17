package de.drazil.nerdsuite.imaging.service;

import java.util.List;

import org.eclipse.swt.graphics.Rectangle;

import de.drazil.nerdsuite.enums.TileAction;
import de.drazil.nerdsuite.model.TileLocation;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;
import de.drazil.nerdsuite.widget.Tile;

public class MirrorService extends AbstractImagingService {
	public final static int UPPER_HALF = 1;
	public final static int LOWER_HALF = 2;
	public final static int LEFT_HALF = 3;
	public final static int RIGHT_HALF = 4;

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
		if (action == UPPER_HALF) {
			for (int y = r.y, c = 0; y < r.y + r.height / 2; y++, c++) {
				for (int x = r.x; x < r.x + r.width; x++) {
					content[x + ((r.y + r.height - c - 1) * configuration.tileWidth)] = content[x
							+ (y * configuration.tileWidth)];
				}
			}
		} else if (action == LOWER_HALF) {
			for (int y = r.y, c = 0; y < r.y + r.height / 2; y++, c++) {
				for (int x = r.x; x < r.x + r.width; x++) {
					content[x + (y * configuration.tileWidth)] = content[x
							+ ((r.y + r.height - c - 1) * configuration.tileWidth)];
				}
			}
		} else if (action == LEFT_HALF) {
			for (int y = r.y; y < r.y + r.height; y++) {
				for (int x = r.x, c = 0; x < r.x + r.width / 2; x++, c++) {
					content[r.x + r.width - 1 - c + (y * configuration.tileWidth)] = content[x
							+ (y * configuration.tileWidth)];
				}
			}
		} else if (action == RIGHT_HALF) {
			for (int y = r.y; y < r.y + r.height; y++) {
				for (int x = r.x, c = 0; x < r.x + r.width / 2; x++, c++) {
					content[x + (y * configuration.tileWidth)] = content[r.x + r.width - 1 - c
							+ (y * configuration.tileWidth)];
				}
			}
		}
	}

}
