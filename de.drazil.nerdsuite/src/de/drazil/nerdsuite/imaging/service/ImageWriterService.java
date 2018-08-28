package de.drazil.nerdsuite.imaging.service;

import java.util.List;

import de.drazil.nerdsuite.model.TileLocation;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;

public class ImageWriterService extends AbstractService {

	public final static int WRITE_PNG = 1;
	public final static int WRITE_GIF = 2;
	public final static int WRITE_ANIMATED_GIF = 4;
	public final static int ENABLE_TELEVISION_MODE = 128;

	private boolean isTelevisionModeEnabled = false;

	public ImageWriterService() {
		// TODO Auto-generated constructor stub
	}

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
	public void runService(int action, List<TileLocation> tileLocationList, byte[] bitplane) {
		// TODO Auto-generated method stub
		super.runService(action, tileLocationList, bitplane);
	}

	@Override
	public void setValue(int action, Object data) {
		if (action == ENABLE_TELEVISION_MODE) {
			isTelevisionModeEnabled = (boolean) data;
		}
	}

}
