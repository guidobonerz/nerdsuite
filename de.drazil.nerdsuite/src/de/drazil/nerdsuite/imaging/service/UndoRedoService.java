package de.drazil.nerdsuite.imaging.service;

import java.util.ArrayList;
import java.util.List;

import de.drazil.nerdsuite.model.TileLocation;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;

public class UndoRedoService extends AbstractImagingService {

	public final static int UNDO = 1;
	public final static int REDO = 2;

	List<byte[]> undoRedoList = null;

	public UndoRedoService() {
		undoRedoList = new ArrayList<>();
	}

	@Override
	public void execute(int action, IConfirmable confirmable) {
		switch (action) {
		case UNDO: {
			break;
		}
		case REDO: {
			break;
		}
		}
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

}
