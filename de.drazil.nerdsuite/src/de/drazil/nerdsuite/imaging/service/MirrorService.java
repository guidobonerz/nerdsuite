package de.drazil.nerdsuite.imaging.service;

import java.util.List;

import de.drazil.nerdsuite.model.TileLocation;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;

public class MirrorService extends AbstractService {
	public final static int UPPER_HALF = 1;
	public final static int LOWER_HALF = 2;
	public final static int LEFT_HALF = 4;
	public final static int RIGHT_HALF = 8;

	@Override
	public boolean needsConfirmation() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public byte[] each(int action, TileLocation tileLocation, ImagingWidgetConfiguration configuration, int offset,
			byte[] bitplane, byte[] workArray, int width, int height) {

		if (action == UPPER_HALF) {
			for (int y = 0; y < height / 2; y++) {
				for (int x = 0; x < width; x++) {
					workArray[x + ((height - y - 1) * width)] = workArray[x + (y * width)];
				}
			}
		} else if (action == LOWER_HALF) {
			for (int y = 0; y < height / 2; y++) {
				for (int x = 0; x < width; x++) {
					workArray[x + (y * width)] = workArray[x + ((height - y - 1) * width)];
				}
			}
		} else if (action == LEFT_HALF) {
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width / 2; x++) {
					workArray[width - 1 - x + (y * width)] = workArray[x + (y * width)];
				}
			}
		} else if (action == RIGHT_HALF) {
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width / 2; x++) {
					workArray[x + (y * width)] = workArray[width - 1 - x + (y * width)];
				}
			}
		}
		return workArray;
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

}