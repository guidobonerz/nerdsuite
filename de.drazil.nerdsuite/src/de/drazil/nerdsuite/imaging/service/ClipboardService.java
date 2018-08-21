package de.drazil.nerdsuite.imaging.service;

import java.util.List;

import de.drazil.nerdsuite.model.TileLocation;
import de.drazil.nerdsuite.widget.IImagingCallback;
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
	public void runService(int action, List<TileLocation> tileLocationList, ImagingWidgetConfiguration configuration,
			IImagingCallback callback, int offset, byte[] bitplane) {
		int ofs = conf.computeTileOffset(tileLocationList.get(0).x, tileLocationList.get(0).y, offset);
		if (action == CUT || action == COPY) {
			currentAction = action;
			cutCopyOffset = ofs;
		}
		if (action == PASTE && currentAction != OFF) {
			pasteOffset = ofs;
			for (int i = 0; i < conf.getTileSize(); i++) {
				bitplane[pasteOffset + i] = bitplane[cutCopyOffset + i];
				if (currentAction == CUT) {
					bitplane[cutCopyOffset + i] = 0;
				}
			}
			currentAction = OFF;
			callback.update(0);
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
