package de.drazil.nerdsuite.imaging.service;

import java.util.List;

public interface ITileUpdateListener {
	enum UpdateMode {
		Single, Selection, All, Animation
	}

	public void updateTiles(List<Integer> selectedTileIndexList, UpdateMode updateMode);

	public void updateTile(int selectedTileIndex, UpdateMode updateMode);
}
