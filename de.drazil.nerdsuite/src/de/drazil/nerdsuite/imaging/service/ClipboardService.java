package de.drazil.nerdsuite.imaging.service;

import java.util.List;

import de.drazil.nerdsuite.model.TileLocation;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;

public class ClipboardService extends AbstractService implements IImagingService {

	public final static int OFF = 0;
	public final static int CUT = 1;
	public final static int COPY = 2;
	public final static int PASTE = 4;
	private int currentAction = OFF;
	private int cutCopyOffset = 0;
	private int pasteOffset = 0;

	@Override
	public boolean needsConfirmation() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void runService(int action, List<TileLocation> tileLocationList, byte[] bitplane) {
		int offset = conf.computeTileOffset(tileLocationList.get(0).x, tileLocationList.get(0).y, navigationOffset);
		if (action == CUT || action == COPY) {
			currentAction = action;
			cutCopyOffset = offset;
		}
		if (action == PASTE) {// && currentAction != OFF) {
			pasteOffset = offset;
			for (int i = 0; i < conf.getTileSize(); i++) {
				bitplane[pasteOffset + i] = bitplane[cutCopyOffset + i];
				if (currentAction == CUT) {
					bitplane[cutCopyOffset + i] = 0;
				}
			}
			currentAction = OFF;
			callback.afterRunService();
		}
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
