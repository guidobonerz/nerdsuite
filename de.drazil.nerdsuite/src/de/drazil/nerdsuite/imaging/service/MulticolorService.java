package de.drazil.nerdsuite.imaging.service;

import java.util.List;

import de.drazil.nerdsuite.enums.TileAction;
import de.drazil.nerdsuite.model.TileLocation;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;
import de.drazil.nerdsuite.widget.Tile;

public class MulticolorService extends AbstractImagingService {

	public static final int MC_ON = 1;
	public static final int MC_OFF = 0;

	@Override
	public boolean needsConfirmation() {
		return false;
	}

	@Override
	public boolean isReadyToRun(List<TileLocation> tileLocationList, ImagingWidgetConfiguration configuration) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void sendResponse(String message, Object data) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isProcessConfirmed(boolean confirmAnyProcess) {
		return confirmable.isConfirmed("Do you really want to purge this tile?");
	}

	@Override
	public boolean needsConversion() {
		return false;
	}

	@Override
	public void each(int action, Tile tile, ImagingWidgetConfiguration configuration, TileAction tileAction) {
		System.out.println(action);

		int[] content = tile.getActiveLayer().getContent();
		for (int i = 0; i < content.length; i += 2) {
			if (action == MC_ON) {
				if (content[i] > 0 && content[i + 1] > 0) {
					content[i] = content[i + 1] = 3;
				} else if (content[i] > 0 && content[i + 1] == 0) {
					content[i] = content[i + 1] = 2;
				} else if (content[i] == 0 && content[i + 1] > 0) {
					content[i] = content[i + 1] = 1;
				} else {
					content[i] = content[i + 1] = 0;
				}
			} else {
				if (content[i] + content[i + 1] == 6) {
					content[i] = content[i + 1] = 1;
				} else if (content[i] + content[i + 1] == 4) {
					content[i] = 1;
					content[i + 1] = 0;
				} else if (content[i] + content[i + 1] == 2) {
					content[i] = 0;
					content[i + 1] = 1;
				} else {
					content[i] = content[i + 1] = 0;
				}
			}
		}
	}
}
