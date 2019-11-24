package de.drazil.nerdsuite.imaging.service;

public class ClipboardService extends AbstractImagingService implements IImagingService {

	public final static int OFF = 0;
	public final static int CUT = 1;
	public final static int COPY = 2;
	public final static int PASTE = 4;
	private int currentAction = OFF;
	private int cutCopyOffset = 0;
	private int pasteOffset = 0;

	@Override
	public void execute(int action, IConfirmable confirmable) {
		int offset = 0;
		if (action == CUT || action == COPY) {
			currentAction = action;
			cutCopyOffset = offset;
		}
		if (action == PASTE) {// && currentAction != OFF) {
			pasteOffset = offset;
			for (int i = 0; i < imagingWidgetConfiguration.getTileSize(); i++) {
				// bitplane[pasteOffset + i] = bitplane[cutCopyOffset + i];
				if (currentAction == CUT) {
					// bitplane[cutCopyOffset + i] = 0;
				}
			}
			currentAction = OFF;
			serviceCallback.afterRunService();
		}
	}

}
