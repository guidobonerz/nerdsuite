package de.drazil.nerdsuite.imaging.service;

import de.drazil.nerdsuite.widget.Tile;

public interface ITileManagementListener {
	public void tileAdded(Tile tile);

	public void tileRemoved();

	public void tileReordered();

}
