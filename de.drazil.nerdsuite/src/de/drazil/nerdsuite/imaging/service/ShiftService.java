package de.drazil.nerdsuite.imaging.service;

import java.util.List;

import de.drazil.nerdsuite.model.TileLocation;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration2;

public class ShiftService extends AbstractImagingService {
	public final static int UP = 1;
	public final static int DOWN = 2;
	public final static int LEFT = 4;
	public final static int RIGHT = 8;

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
			byte[] bitplane, byte workArray[], int width, int height) {
		if (action == UP) {
			for (int x = 0; x < width; x++) {
				byte b = workArray[x];
				for (int y = 0; y < height - 1; y++) {
					workArray[x + y * width] = workArray[x + (y + 1) * width];
				}
				workArray[x + (width * (height - 1))] = b;
			}
		} else if (action == DOWN) {
			for (int x = 0; x < width; x++) {
				byte b = workArray[x + (width * (height - 1))];
				for (int y = height - 1; y > 0; y--) {
					workArray[x + y * width] = workArray[x + (y - 1) * width];
				}
				workArray[x] = b;
			}
		} else if (action == LEFT) {
			for (int y = 0; y < height; y++) {
				byte b = workArray[y * width];
				for (int x = 0; x < width - 1; x++) {
					workArray[x + y * width] = workArray[(x + 1) + y * width];
				}
				workArray[(width + y * width) - 1] = b;
			}
		} else if (action == RIGHT) {
			for (int y = 0; y < height; y++) {
				byte b = workArray[(width + y * width) - 1];
				for (int x = width - 1; x > 0; x--) {
					workArray[x + y * width] = workArray[(x - 1) + y * width];
				}
				workArray[y * width] = b;
			}
		}
		return workArray;
	}

}
