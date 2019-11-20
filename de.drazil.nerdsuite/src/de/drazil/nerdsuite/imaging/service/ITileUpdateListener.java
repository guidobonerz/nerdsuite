package de.drazil.nerdsuite.imaging.service;

import java.util.List;

public interface ITileUpdateListener {
	enum UpdateMode {
		Single, Selection, All
	}

	public void updateTiles(List<Integer> selectedTileIndexList, UpdateMode updateMode);
}
