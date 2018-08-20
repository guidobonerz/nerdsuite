package de.drazil.nerdsuite.imaging.service;

import java.util.List;

import de.drazil.nerdsuite.model.TileLocation;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;

public class PurgeService extends AbstractService {

	@Override
	public boolean needsConfirmation() {
		return true;
	}

	@Override
	public void each(int action, TileLocation tileLocation, ImagingWidgetConfiguration configuration, int offset, byte[] bitplane) {
		for (int n = 0; n < conf.getTileSize(); n++) {
			bitplane[offset + n] = 0;
		}
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

}
