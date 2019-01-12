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
	public void execute(int action) {
		int swapSourceOffset = imagingWidgetConfiguration.computeTileOffset(tileSelectionList.get(0).x,
				tileSelectionList.get(0).y, navigationOffset);
		int swapTargetOffset = imagingWidgetConfiguration.computeTileOffset(tileSelectionList.get(1).x,
				tileSelectionList.get(1).y, navigationOffset);

		for (int n = 0; n < imagingWidgetConfiguration.getTileSize(); n++) {
			byte buffer = bitplane[swapSourceOffset + n];
			bitplane[swapSourceOffset + n] = bitplane[swapTargetOffset + n];
			bitplane[swapTargetOffset + n] = buffer;
		}
	}
}
