package de.drazil.nerdsuite.imaging.service;

import java.util.List;

import de.drazil.nerdsuite.enums.RedrawMode;

public interface ITileUpdateListener {
	public void redrawTiles(List<Integer> selectedTileIndexList, RedrawMode redrawMode, int update);
}
