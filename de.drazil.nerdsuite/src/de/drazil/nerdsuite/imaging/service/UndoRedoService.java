package de.drazil.nerdsuite.imaging.service;

import java.util.ArrayList;
import java.util.List;

public class UndoRedoService extends AbstractImagingService {

	public final static int UNDO = 1;
	public final static int REDO = 2;

	List<byte[]> undoRedoList = null;

	public UndoRedoService() {
		undoRedoList = new ArrayList<>();
	}

}
