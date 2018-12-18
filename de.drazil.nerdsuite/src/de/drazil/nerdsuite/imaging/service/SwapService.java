package de.drazil.nerdsuite.imaging.service;

import java.util.List;

import de.drazil.nerdsuite.model.TileLocation;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;

public class SwapService extends AbstractImagingService {

	@Override
	public boolean needsConfirmation() {

		return false;
	}

	@Override
	public boolean isReadyToRun(List<TileLocation> tileLocationList, ImagingWidgetConfiguration configuration) {
		return tileLocationList.size() == 2;
	}

	@Override
	public void sendResponse(String message, Object data) {
		// showNotification(ImagingService.Swap, null, "Please select only two
		// tiles to swap.", null);

	}

	@Override
	public boolean needsConversion() {
		return false;
	}

	@Override
	public boolean isProcessConfirmed(boolean confirmAnyProcess) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void runService(int action, List<TileLocation> tileLocationList, byte bitplane[]) {
		int swapSourceOffset = conf.computeTileOffset(tileLocationList.get(0).x, tileLocationList.get(0).y,
				navigationOffset);
		int swapTargetOffset = conf.computeTileOffset(tileLocationList.get(1).x, tileLocationList.get(1).y,
				navigationOffset);

		for (int n = 0; n < conf.getTileSize(); n++) {
			byte buffer = bitplane[swapSourceOffset + n];
			bitplane[swapSourceOffset + n] = bitplane[swapTargetOffset + n];
			bitplane[swapTargetOffset + n] = buffer;
		}
	}

}
