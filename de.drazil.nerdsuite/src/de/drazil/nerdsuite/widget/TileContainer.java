package de.drazil.nerdsuite.widget;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.drazil.nerdsuite.model.GraphicFormat;
import de.drazil.nerdsuite.model.GraphicFormatVariant;
import de.drazil.nerdsuite.model.ProjectMetaData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TileContainer {
	@JsonProperty(value = "metadata")
	private ProjectMetaData metadata = null;
	@JsonProperty(value = "tiles")
	private List<Tile> tileList = null;
	@JsonProperty(value = "tileIndexOrder")
	private List<Integer> tileIndexOrderList = null;
	@JsonProperty(value = "selectedTiles")
	private List<Integer> selectedTileIndexList = null;
	@JsonProperty(value = "referenceRepositoryLocation")
	private String referenceRepositoryLocation = null;

	@JsonIgnore
	public void setInitialSize(int tileCount) {
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
		String name = String.format("%s%d", "tile_", (getTileList().size() + 1));
		// String key = String.format("%s_%s", metadata.getPlatform(),
		// metadata.getType());
		// GraphicFormat format = GraphicFormatFactory.getFormatById(key);
		// GraphicFormatVariant variant = GraphicFormatFactory.getFormatVariantById(key,
		// metadata.getVariant());
		// return addTile(name, format.getHeight() * variant.getTileRows() *
		// format.getWidth() * variant.getTileColumns(), 0, metadata.getBlankValue());
		int blankValue = metadata.getBlankValue() == null ? 0 : metadata.getBlankValue();
		return addTile(name, metadata.getTileSize(), blankValue);
	}

	@JsonIgnore
	private Tile addTile(String name, int tileSize, int defaultBrush) {
		initList();
		Tile tile = new Tile(name, tileSize);
		tile.addLayer(name, tileSize, defaultBrush);
		getTileList().add(tile);
		getTileIndexOrderList().add(getTileList().indexOf(tile));
		// fireTileAdded();
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
			// fireTileRemoved();
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
		// fireTileReordered();
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
		// fireTileRedraw(getSelectedTileIndexList(), ImagePainterFactory.READ, false);

	}

	@JsonIgnore
	public void setSelectedTileIndexList(List<Integer> tileIndexList) {
		selectedTileIndexList = tileIndexList;
		// fireTileRedraw(tileIndexList, ImagePainterFactory.READ, false);
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

}
