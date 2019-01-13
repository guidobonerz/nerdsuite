package de.drazil.nerdsuite.imaging.service;

import java.util.ArrayList;
import java.util.List;

import de.drazil.nerdsuite.widget.Tile;

public class TileService extends AbstractImagingService {

	private List<Tile> tileList = null;
	private List<Integer> tileIndexOrderList = null;
	private List<ITileManagementListener> tileServiceManagementListener = null;
	private List<ITileSelectionListener> tileServiceSelectionListener = null;

	public TileService() {
		tileList = new ArrayList<Tile>();
		tileIndexOrderList = new ArrayList<Integer>();
		tileServiceManagementListener = new ArrayList<ITileManagementListener>();
		tileServiceSelectionListener = new ArrayList<ITileSelectionListener>();
	}

	public void addTile(String name, int size) {
		Tile tile = new Tile(name, size);
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

	public void setSelectedTile(int index) {
		fireTileSelected(getTile(index));
	}

	public Tile getTile(int index) {
		return tileList.get(tileIndexOrderList.get(index));
	}

	public void addTileManagementListener(ITileManagementListener listener) {
		tileServiceManagementListener.add(listener);
	}

	public void removeTileManagementListener(ITileManagementListener listener) {
		tileServiceManagementListener.remove(listener);
	}

	public void addTileSelectionListener(ITileSelectionListener listener) {
		tileServiceSelectionListener.add(listener);
	}

	public void removeTileSelectionListener(ITileSelectionListener listener) {
		tileServiceSelectionListener.remove(listener);
	}

	private void fireTileAdded() {
		tileServiceManagementListener.forEach(listener -> listener.tileAdded());
	}

	private void fireTileRemoved() {
		tileServiceManagementListener.forEach(listener -> listener.tileRemoved());
	}

	private void fireTileReordered() {
		tileServiceManagementListener.forEach(listener -> listener.tileReordered());
	}

	public void fireTileSelected(Tile tile) {
		tileServiceSelectionListener.forEach(listener -> listener.tileSelected(tile));
	}
}
