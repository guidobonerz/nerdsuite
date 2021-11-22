package de.drazil.nerdsuite.imaging.service;

import java.util.List;

public interface ITileBulkModificationListener {
	public void tilesChanged(List<Integer> selectedTileIndexList);
}
