package de.drazil.nerdsuite.imaging.service;

import java.util.List;

import de.drazil.nerdsuite.model.TileLocation;
import de.drazil.nerdsuite.widget.Tile;

public interface ITileSelectionListener {

	public void tileSelected(Tile tile);

	public void tilesSelected(List<TileLocation> tileLocationList);
}
