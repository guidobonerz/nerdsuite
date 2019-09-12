package de.drazil.nerdsuite.imaging.service;

import java.util.ArrayList;
import java.util.List;

import de.drazil.nerdsuite.widget.Tile;

public class TileRepositoryService extends AbstractImagingService {

	private List<Tile> tileList = null;
	private List<Integer> tileIndexOrderList = null;
	private List<ITileManagementListener> tileServiceManagementListener = null;
	private List<ITileSelectionListener> tileServiceSelectionListener = null;
	private ImagePainterFactory imagePainterFactory;
	private int selectedTileIndex = 0;

	public TileRepositoryService() {
		tileList = new ArrayList<>();
		tileIndexOrderList = new ArrayList<>();
		tileServiceManagementListener = new ArrayList<>();
		tileServiceSelectionListener = new ArrayList<>();
		imagePainterFactory = new ImagePainterFactory();
	}

	public void addTile(String name, int size) {
		System.out.println("Add Tile");
		Tile tile = new Tile(name, size);
		tileList.add(tile);
		tileIndexOrderList.add(tileList.indexOf(tile));
		fireTileAdded();
	}

	public void removeLast() {
		if (tileIndexOrderList.size() > 0) {
			removeTile(tileIndexOrderList.size() - 1);
		}

	}

	public void removeSelected() {
		removeTile(selectedTileIndex);
	}

	public void removeTile(int index) {
		if (tileIndexOrderList.size() > 0) {
			System.out.println("Remove Tile");
			int tileIndex = tileIndexOrderList.get(index);
			tileList.remove(tileIndex);
			tileIndexOrderList.remove(index);
			fireTileRemoved();
		}
	}

	public void setSelectedTile(int index) {
		selectedTileIndex = index;
		fireTileSelected(getTile(index));
	}

	public Tile getSelectedTile() {
		return getTile(selectedTileIndex);
	}

	public Tile getTile(int index) {
		return tileList.get(tileIndexOrderList.get(index));
	}

	public int getSize() {
		return tileList.size();
	}

	public ImagePainterFactory getImagePainterFactory() {
		return imagePainterFactory;
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
