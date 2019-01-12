package de.drazil.nerdsuite.imaging.service;

import java.util.List;

import de.drazil.nerdsuite.model.TileLocation;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration2;

public class RotationService extends AbstractImagingService {
	public final static int CW = 1;
	public final static int CCW = 2;

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

	private boolean checkIfSquareBase() {
		int w = imagingWidgetConfiguration.currentWidth * imagingWidgetConfiguration.tileColumns;
		int h = imagingWidgetConfiguration.height * imagingWidgetConfiguration.tileRows;
		return w == h;
	}

	@Override
	public byte[] each(int action, TileLocation tileLocation, ImagingWidgetConfiguration2 configuration, int offset,
			byte[] bitplane, byte[] workArray, int width, int height) {
		byte targetWorkArray[] = createWorkArray();
		for (int y = 0; y < imagingWidgetConfiguration.height * imagingWidgetConfiguration.tileRows; y++) {
			for (int x = 0; x < imagingWidgetConfiguration.width * imagingWidgetConfiguration.tileColumns; x++) {
				byte b = workArray[x + (y * imagingWidgetConfiguration.width * imagingWidgetConfiguration.tileColumns)];
				int o = 0;
				if (action == CCW) {
					o = (imagingWidgetConfiguration.width * imagingWidgetConfiguration.height
							* imagingWidgetConfiguration.tileRows * imagingWidgetConfiguration.tileColumns)
							- (imagingWidgetConfiguration.width * imagingWidgetConfiguration.tileColumns)
							- (imagingWidgetConfiguration.width * imagingWidgetConfiguration.tileColumns * x) + y;
				} else if (action == CW) {
					o = (imagingWidgetConfiguration.width * imagingWidgetConfiguration.tileColumns) - y - 1
							+ (x * imagingWidgetConfiguration.width * imagingWidgetConfiguration.tileColumns);
				}
				if (o >= 0 && o < (width * height)) {
					targetWorkArray[o] = b;
				}
			}
		}
		return targetWorkArray;
	}

}
