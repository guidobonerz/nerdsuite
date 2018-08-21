package de.drazil.nerdsuite.imaging.service;

import java.util.List;

import de.drazil.nerdsuite.model.TileLocation;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;

public class RotationService extends AbstractService {
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
	public byte[] each(int action, TileLocation tileLocation, ImagingWidgetConfiguration configuration, int offset,
			byte[] bitplane, byte[] workArray, int width, int height) {
		byte targetWorkArray[] = createWorkArray();
		for (int y = 0; y < conf.height * conf.tileRows; y++) {
			for (int x = 0; x < conf.width * conf.tileColumns; x++) {
				byte b = workArray[x + (y * conf.width * conf.tileColumns)];
				int o = 0;
				if (action == CCW) {
					o = (conf.width * conf.height * conf.tileRows * conf.tileColumns) - (conf.width * conf.tileColumns)
							- (conf.width * conf.tileColumns * x) + y;
				} else if (action == CW) {
					o = (conf.width * conf.tileColumns) - y - 1 + (x * conf.width * conf.tileColumns);
				}
				if (o >= 0 && o < (width * height)) {
					targetWorkArray[o] = b;
				}
			}
		}
		return targetWorkArray;
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

	private boolean checkIfSquareBase() {
		int w = conf.currentWidth * conf.tileColumns;
		int h = conf.height * conf.tileRows;
		return w == h;
	}

}
