package de.drazil.nerdsuite.imaging.service;

import java.util.ArrayList;
import java.util.List;

import de.drazil.nerdsuite.widget.Tile;

public class TileService extends AbstractImagingService {

	private List<Tile> tileList = null;
	private List<Integer> tileIndexOrderList = null;
	private List<ITileServiceListener> tileServiceListener = null;

	public TileService() {
		tileList = new ArrayList<Tile>();
		tileIndexOrderList = new ArrayList<Integer>();
		tileServiceListener = new ArrayList<ITileServiceListener>();
	}

	public void addTile(String name) {
		Tile tile = new Tile(name);
		tileList.add(tile);
		tileIndexOrderList.add(tileList.indexOf(tile));
		fireTileAdded();
	}

	public void removeTile(int index) {
		int tileIndex = tileIndexOrderList.get(index);
		tileList.remove(tileIndex);
		tileIndexOrderList.remove(index);
		fireTileRemoved();
	}

	public void addTileListener(ITileServiceListener listener) {
		tileServiceListener.add(listener);
	}

	public void removeTileListener(ITileServiceListener listener) {
		tileServiceListener.remove(listener);
	}

	private void fireTileAdded() {
		tileServiceListener.forEach(listener -> listener.tileAdded());
	}

	private void fireTileRemoved() {
		tileServiceListener.forEach(listener -> listener.tileRemoved());
	}

	private void fireTileReordered() {
		tileServiceListener.forEach(listener -> listener.tileReordered());
	}
}
