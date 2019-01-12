package de.drazil.nerdsuite.imaging.service;

import java.util.List;

import de.drazil.nerdsuite.model.TileLocation;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration2;

public class InvertService extends AbstractImagingService {

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
	public boolean isReadyToRun(List<TileLocation> tileLocationList, ImagingWidgetConfiguration2 configuration) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isProcessConfirmed(boolean confirmAnyProcess) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public byte[] each(int action, TileLocation tileLocation, ImagingWidgetConfiguration2 configuration, int offset,
			byte[] bitplane, byte[] workArray, int width, int height) {
		int mask = imagingWidgetConfiguration.pixelConfig.mask;
		for (int i = 0; i < workArray.length; i++) {
			workArray[i] = (byte) ((workArray[i] ^ 0xff) & mask);
		}
		return workArray;
	}

}
