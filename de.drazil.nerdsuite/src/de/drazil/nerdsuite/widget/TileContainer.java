package de.drazil.nerdsuite.widget;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.drazil.nerdsuite.enums.RedrawMode;
import de.drazil.nerdsuite.imaging.service.ITileManagementListener;
import de.drazil.nerdsuite.imaging.service.ITileUpdateListener;
import de.drazil.nerdsuite.imaging.service.ImagePainterFactory;
import de.drazil.nerdsuite.model.GraphicMetadata;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TileContainer {
	@JsonProperty(value = "metadata")
	private GraphicMetadata metadata = null;
	@JsonProperty(value = "tiles")
	private List<Tile> tileList = null;
	@JsonProperty(value = "tileIndexOrder")
	private List<Integer> tileIndexOrderList = null;
	@JsonProperty(value = "selectedTiles")
	private List<Integer> selectedTileIndexList = null;
	@JsonIgnore
	private List<ITileManagementListener> tileServiceManagementListener = null;
	@JsonIgnore
	private List<ITileUpdateListener> tileUpdateListener = null;
	@JsonIgnore
	private int tileSize;

	public TileContainer() {
		tileServiceManagementListener = new ArrayList<>();
		tileUpdateListener = new ArrayList<>();
	}

	public void setMetadata(GraphicMetadata metadata) {
		this.metadata = metadata;
		this.tileSize = metadata.getWidth() * metadata.getHeight() * metadata.getColumns() * metadata.getRows();
	}

	@JsonIgnore
	public void addInitialTiles(int tileCount) {
		initList();
		for (int i = 0; i < tileCount; i++) {
			addTile();
		}
		setSelectedTileIndex(0);
	}

	private void initList() {
		if (tileList == null) {
			tileList = new ArrayList<Tile>();
		}
		if (tileIndexOrderList == null) {
			tileIndexOrderList = new ArrayList<Integer>();
		}

		if (selectedTileIndexList == null) {
			selectedTileIndexList = new ArrayList<Integer>();
		}
	}

	@JsonIgnore
	public Tile addTile() {
		initList();
		String id = String.format("T%03X", (getTileList().size() + 1));
		Integer blankValue = metadata.getBlankValue();
		return addTile(id, id, tileSize, blankValue);
	}

	@JsonIgnore
	private Tile addTile(String id, String name, int tileSize, Integer defaultBrush) {
		initList();
		Tile tile = new Tile(id, name, tileSize);
		String layerId = String.format("L%03X", (getTileList().size() + 1), (tile.getSize() + 1));
		tile.addLayer(layerId, layerId, tileSize, defaultBrush);
		getTileList().add(tile);
		getTileIndexOrderList().add(getTileList().indexOf(tile));
		fireTileAdded();
		return tile;
	}

	@JsonIgnore
	public void removeLast() {
		if (getTileIndexOrderList().size() > 0) {
			List<Integer> l = new ArrayList<Integer>();
			l.add(getTileIndexOrderList().size() - 1);
			removeTile(l);
		}
	}

	@JsonIgnore
	public void removeSelected() {
		removeTile(getSelectedTileIndexList());
	}

	@JsonIgnore
	public void removeTile(List<Integer> tileIndexList) {
		if (getTileIndexOrderList().size() > 0) {
			for (int i = 0; i < tileIndexList.size(); i++) {
				int tileIndex = getTileIndexOrderList().get(i);
				getTileList().remove(tileIndex);
				getTileIndexOrderList().remove(i);
			}
			fireTileRemoved();
		}
	}

	@JsonIgnore
	public void moveTile(int from, int to) {
		int v = getTileIndexOrderList().get(from);
		if (to < from) {
			getTileIndexOrderList().remove(from);
			getTileIndexOrderList().add(to, v);
		} else {
			getTileIndexOrderList().add(to, v);
			getTileIndexOrderList().remove(from);
		}
		fireTileReordered();
	}

	@JsonIgnore
	public int getTileIndex(int index) {
		return getTileIndexOrderList().get(index);
	}

	@JsonIgnore
	public void setSelectedTileIndex(int index) {
		initList();
		getSelectedTileIndexList().clear();
		getSelectedTileIndexList().add(index);
		fireTileRedraw(getSelectedTileIndexList(), ImagePainterFactory.READ, false);

	}

	@JsonIgnore
	public void setSelectedTileIndexList(List<Integer> tileIndexList) {
		selectedTileIndexList = tileIndexList;
		fireTileRedraw(tileIndexList, ImagePainterFactory.READ, false);
	}

	@JsonIgnore
	public List<Integer> getSelectedTileIndexList() {
		return selectedTileIndexList;
	}

	@JsonIgnore
	public Tile getSelectedTile() {
		return getTile(getSelectedTileIndex(false));
	}

	@JsonIgnore
	public int getSelectedTileIndex(boolean natural) {
		int index = 1;
		if (natural) {
			index = tileList.indexOf(getSelectedTile());
		} else {
			index = getSelectedTileIndexList().get(0);
		}
		return index;
	}

	@JsonIgnore
	public void resetActiveLayer(int index) {
		getTile(index).getActiveLayer().reset(0, 0);
	}

	@JsonIgnore
	public Tile getTile(int index) {
		return getTile(index, false);
	}

	@JsonIgnore
	public Tile getTile(int index, boolean naturalOrder) {
		return getTileList().get(naturalOrder ? index : getTileIndexOrderList().get(index));
	}

	@JsonIgnore
	public int getSize() {
		return getTileList().size();
	}

	@JsonIgnore
	public void setDirty(boolean dirty) {
		getSelectedTile().setDirty(dirty);
		fireTileRedraw(getSelectedTileIndexList(), -1, false);
	}

	@JsonIgnore
	private void fireTileAdded() {
		tileServiceManagementListener.forEach(listener -> listener.tileAdded(getSelectedTile()));
	}

	@JsonIgnore
	public void addTileManagementListener(ITileManagementListener listener) {
		tileServiceManagementListener.add(listener);
	}

	@JsonIgnore
	public void removeTileManagementListener(ITileManagementListener listener) {
		tileServiceManagementListener.remove(listener);
	}

	@JsonIgnore
	private void fireTileRemoved() {
		tileServiceManagementListener.forEach(listener -> listener.tileRemoved());
	}

	@JsonIgnore
	private void fireTileReordered() {
		tileServiceManagementListener.forEach(listener -> listener.tileReordered());
	}

	@JsonIgnore
	public void addTileManagementListener(ITileManagementListener... listeners) {
		for (ITileManagementListener listener : listeners) {
			addTileManagementListener(listener);
		}
	}

	@JsonIgnore
	public void addTileUpdateListener(ITileUpdateListener... listeners) {
		for (ITileUpdateListener listener : listeners) {
			addTileUpdateListener(listener);
		}
	}

	@JsonIgnore
	public void addTileUpdateListener(ITileUpdateListener listener) {
		tileUpdateListener.add(listener);
	}

	@JsonIgnore
	public void removeTileUpdateListener(ITileUpdateListener listener) {
		tileUpdateListener.remove(listener);
	}

	@JsonIgnore
	private void fireTileRedraw(List<Integer> selectedTileIndexList, int action, boolean temporary) {
		if (selectedTileIndexList != null) {
			if (selectedTileIndexList.size() == 1) {
				tileUpdateListener.forEach(listener -> listener.redrawTiles(selectedTileIndexList,
						temporary ? RedrawMode.DrawTemporarySelectedTile : RedrawMode.DrawSelectedTile, action));
			} else {
				tileUpdateListener.forEach(
						listener -> listener.redrawTiles(selectedTileIndexList, RedrawMode.DrawSelectedTiles, action));
			}
		}
	}

	@JsonIgnore
	public void redrawTileViewer(List<Integer> selectedTileIndexList, int action, boolean temporary) {
		fireTileRedraw(selectedTileIndexList, action, temporary);
	}

}
