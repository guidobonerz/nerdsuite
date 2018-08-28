package de.drazil.nerdsuite.imaging.service;

import java.util.List;

import de.drazil.nerdsuite.model.TileLocation;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;

public class FlipService extends AbstractService {

	public final static int HORIZONTAL = 1;
	public final static int VERTICAL = 2;

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
	public byte[] each(int action, TileLocation tileLocation, ImagingWidgetConfiguration configuration, int offset,
			byte[] bitplane, byte[] workArray, int width, int height) {
		if (action == HORIZONTAL) {
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width / 2; x++) {
					byte a = workArray[x + (y * width)];
					byte b = workArray[width - 1 - x + (y * width)];
					workArray[x + (y * width)] = b;
					workArray[width - 1 - x + (y * width)] = a;
				}
			}
		} else if (action == VERTICAL) {
			for (int y = 0; y < height / 2; y++) {
				for (int x = 0; x < width; x++) {
					byte a = workArray[x + (y * width)];
					byte b = workArray[x + ((height - y - 1) * width)];
					workArray[x + (y * width)] = b;
					workArray[x + ((height - y - 1) * width)] = a;
				}
			}
		}
		return workArray;
	}

}
