package de.drazil.nerdsuite.imaging.service;

import java.util.List;

import de.drazil.nerdsuite.model.TileLocation;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;

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
		return false;
	}

	@Override
	public boolean needsConversion() {
		return false;
	}

	@Override
	public byte[] each(int action, TileLocation tileLocation, ImagingWidgetConfiguration configuration, int offset,
			byte[] bitplane, byte workArray[], int width, int height) {
		for (int n = 0; n < conf.getTileSize(); n++) {
			bitplane[offset + n] = 0;
		}
		return null;
	}

}
