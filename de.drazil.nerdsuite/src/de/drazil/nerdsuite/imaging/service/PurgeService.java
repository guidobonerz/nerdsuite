package de.drazil.nerdsuite.imaging.service;

import java.util.List;

import de.drazil.nerdsuite.model.TileLocation;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;
import de.drazil.nerdsuite.widget.Tile;

public class PurgeService extends AbstractImagingService {

	@Override
	public boolean needsConfirmation() {
		return true;
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
	public void each(int action, Tile tile, ImagingWidgetConfiguration configuration) {
		int[] content = tile.getActiveLayer().getContent();
		for (int i = 0; i < content.length; i++) {
			content[i] = 0;
		}
	}
}
