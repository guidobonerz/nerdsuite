package de.drazil.nerdsuite.imaging.service;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.swt.graphics.Rectangle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.drazil.nerdsuite.enums.RedrawMode;
import de.drazil.nerdsuite.model.CustomSize;
import de.drazil.nerdsuite.widget.Tile;
import lombok.Getter;
import lombok.Setter;

public class TileRepositoryService implements IService {
	@Getter
	@Setter
	private String platform;
	@Getter
	@Setter
	private String type;
	@Getter
	@Setter
	private String variant;
	@Getter
	@Setter
	private String referenceRepositoryName;
	@JsonIgnore
	@Setter
	private String owner = null;
	@JsonProperty(value = "tiles")
	private List<Tile> tileList = null;
	@JsonProperty(value = "tileIndexOrder")
	private List<Integer> tileIndexOrderList = null;
	@JsonIgnore
	private List<ITileManagementListener> tileServiceManagementListener = null;
	@JsonIgnore
	private List<ITileUpdateListener> tileUpdateListener = null;
	@JsonProperty(value = "selectedTiles")
	private List<Integer> selectedTileIndexList = null;
	@JsonProperty(value = "customFormat")
	private CustomSize customSize;
	@Getter
	@Setter
	@JsonIgnore
	private Rectangle selection;

	public TileRepositoryService() {
		tileList = new ArrayList<>();
		tileIndexOrderList = new ArrayList<>();
		tileServiceManagementListener = new ArrayList<>();
		tileUpdateListener = new ArrayList<>();
		selectedTileIndexList = new ArrayList<Integer>();
	}

	public Tile addTile(int size) {
		return addTile("tile_" + (tileList.size() + 1), size);
	}

	public Tile addTile(String name, int size) {
		System.out.println("Add Tile");
		Tile tile = new Tile(name, size);
		tileList.add(tile);
		tileIndexOrderList.add(tileList.indexOf(tile));
		setSelectedTileIndex(tileIndexOrderList.get(getSize() - 1));
		fireTileAdded();
		return tile;
	}

	public void setCustomSize(CustomSize customSize) {
		this.customSize = customSize;
	}

	public CustomSize getCustomSize() {
		return customSize;
	}

	public void removeLast() {
		if (tileIndexOrderList.size() > 0) {
			List<Integer> l = new ArrayList<Integer>();
			l.add(tileIndexOrderList.size() - 1);
			removeTile(l);
		}
	}

	public void removeSelected() {
		removeTile(selectedTileIndexList);
	}

	public void removeTile(List<Integer> tileIndexList) {
		if (tileIndexOrderList.size() > 0) {
			System.out.println("Remove Tile");
			for (int i = 0; i < tileIndexList.size(); i++) {
				int tileIndex = tileIndexOrderList.get(i);
				tileList.remove(tileIndex);
				tileIndexOrderList.remove(i);
			}
			fireTileRemoved();
		}
	}

	public void moveTile(int from, int to) {
		int v = tileIndexOrderList.get(from);
		if (to < from) {
			tileIndexOrderList.remove(from);
			tileIndexOrderList.add(to, v);
		} else {
			tileIndexOrderList.add(to, v);
			tileIndexOrderList.remove(from);
		}
		fireTileReordered();
	}

	public int getTileIndex(int index) {
		return tileIndexOrderList.get(index);
	}

	@JsonIgnore
	public void setSelectedTileIndex(int index) {
		selectedTileIndexList.clear();
		selectedTileIndexList.add(index);
		setSelectedTileIndexList(selectedTileIndexList);
	}

	public void setSelectedTileIndexList(List<Integer> tileIndexList) {
		this.selectedTileIndexList = tileIndexList;
		fireTileRedraw(tileIndexList, false, false);
	}

	public List<Integer> getSelectedTileIndexList() {
		return selectedTileIndexList;
	}

	@JsonIgnore
	public Tile getSelectedTile() {
		int index = selectedTileIndexList.get(0);
		return getTile(index);
	}

	@JsonIgnore
	public int getSelectedTileIndex() {
		return tileList.indexOf(getSelectedTile());
	}

	public Tile getTile(int index) {
		return tileList.get(tileIndexOrderList.get(index));
	}

	@JsonIgnore
	public int getSize() {
		return tileList.size();
	}

	public void addTileManagementListener(ITileManagementListener... listeners) {
		for (ITileManagementListener listener : listeners) {
			addTileManagementListener(listener);
		}
	}

	public void addTileManagementListener(ITileManagementListener listener) {
		tileServiceManagementListener.add(listener);
	}

	public void removeTileManagementListener(ITileManagementListener listener) {
		tileServiceManagementListener.remove(listener);
	}

	public void addTileSelectionListener(ITileUpdateListener... listeners) {
		for (ITileUpdateListener listener : listeners) {
			addTileUpdateListener(listener);
		}
	}

	public void addTileUpdateListener(ITileUpdateListener listener) {
		tileUpdateListener.add(listener);
	}

	public void removeTileUpdateListener(ITileUpdateListener listener) {
		tileUpdateListener.remove(listener);
	}

	private void fireTileAdded() {
		tileServiceManagementListener.forEach(listener -> listener.tileAdded(getSelectedTile()));
	}

	private void fireTileRemoved() {
		tileServiceManagementListener.forEach(listener -> listener.tileRemoved());
	}

	private void fireTileReordered() {
		tileServiceManagementListener.forEach(listener -> listener.tileReordered());
	}

	private void fireTileRedraw(List<Integer> selectedTileIndexList, boolean forceUpdate, boolean temporary) {
		if (selectedTileIndexList != null) {
			if (selectedTileIndexList.size() == 1) {
				tileUpdateListener.forEach(listener -> listener.redrawTiles(selectedTileIndexList,
						temporary ? RedrawMode.DrawTemporarySelectedTile : RedrawMode.DrawSelectedTile, forceUpdate));
			} else {
				tileUpdateListener.forEach(listener -> listener.redrawTiles(selectedTileIndexList,
						RedrawMode.DrawSelectedTiles, forceUpdate));
			}
		}
	}

	public void redrawTileViewer(List<Integer> selectedTileIndexList, boolean forceUpdate, boolean temporary) {
		fireTileRedraw(selectedTileIndexList, forceUpdate, temporary);
	}

	public static TileRepositoryService load(File fileName, String owner) {
		TileRepositoryService service = null;
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
		try {
			service = mapper.readValue(fileName, TileRepositoryService.class);
			ServiceFactory.addService(owner, service, false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return service;
	}

	public static void save(File fileName, TileRepositoryService service, String headerText) {
		try {
			FileWriter fw = new FileWriter(fileName);
			fw.write(headerText);
			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			mapper.writeValue(fw, service);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
