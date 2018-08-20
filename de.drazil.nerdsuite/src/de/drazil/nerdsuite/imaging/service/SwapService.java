package de.drazil.nerdsuite.imaging.service;

import java.util.List;

import de.drazil.nerdsuite.model.TileLocation;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;

public class SwapService extends AbstractService {

	@Override
	public boolean needsConfirmation() {

		return false;
	}

	@Override
	public void runService(int action, List<TileLocation> tileLocationList, ImagingWidgetConfiguration configuration,
			int offset, byte bitplane[]) {
		int swapSourceOffset = conf.computeTileOffset(tileLocationList.get(0).x, tileLocationList.get(0).y, offset);
		int swapTargetOffset = conf.computeTileOffset(tileLocationList.get(1).x, tileLocationList.get(1).y, offset);

		for (int n = 0; n < conf.getTileSize(); n++) {
			byte buffer = bitplane[swapSourceOffset + n];
			bitplane[swapSourceOffset + n] = bitplane[swapTargetOffset + n];
			bitplane[swapTargetOffset + n] = buffer;
		}
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
	public boolean isProcessConfirmed(boolean confirmAnyProcess) {
		// TODO Auto-generated method stub
		return false;
	}

}
