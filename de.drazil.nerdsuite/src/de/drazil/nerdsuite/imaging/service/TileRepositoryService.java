package de.drazil.nerdsuite.imaging.service;

import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Rectangle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.enums.RedrawMode;
import de.drazil.nerdsuite.model.Project;
import de.drazil.nerdsuite.model.ProjectMetaData;
import de.drazil.nerdsuite.widget.Tile;
import lombok.Getter;
import lombok.Setter;

public class TileRepositoryService implements IService {

	@JsonIgnore
	@Setter
	@Getter
	private String owner = null;
	@Getter
	@Setter
	private String reference = null;
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
	@Getter
	private ProjectMetaData metadata;
	@Getter
	@Setter
	@JsonIgnore
	private Rectangle selection;
	@Getter
	@JsonIgnore
	private ImagePainterFactory imagePainterFactory;

	public TileRepositoryService() {
		tileList = new ArrayList<>();
		tileIndexOrderList = new ArrayList<>();
		tileServiceManagementListener = new ArrayList<>();
		tileUpdateListener = new ArrayList<>();
		selectedTileIndexList = new ArrayList<Integer>();
		imagePainterFactory = new ImagePainterFactory();
	}

	public void setMetadata(ProjectMetaData metadata) {
		this.metadata = metadata;
	}

	public void setInitialSize(int size) {
		for (int i = 0; i < size; i++) {
			addTileInternal("tile_" + (tileList.size() + 1));
		}
		setSelectedTileIndex(0);
	}

	public Tile addTile() {
		return addTile("tile_" + (tileList.size() + 1));
	}

	public Tile addTile(String name) {
		Tile tile = addTileInternal(name);
		setSelectedTileIndex(tileIndexOrderList.get(getSize() - 1));
		fireTileAdded();
		return tile;
	}

	private Tile addTileInternal(String name) {
		Tile tile = new Tile(name,
				metadata.getHeight() * metadata.getWidth() * metadata.getColumns() * metadata.getRows());
		tileList.add(tile);
		tileIndexOrderList.add(tileList.indexOf(tile));
		return tile;
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
		fireTileRedraw(tileIndexList, ImagePainterFactory.READ, false);
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
		return getTile(index, false);
	}

	public Tile getTile(int index, boolean naturalOrder) {
		return tileList.get(naturalOrder ? index : tileIndexOrderList.get(index));
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

	public void redrawTileViewer(List<Integer> selectedTileIndexList, int action, boolean temporary) {
		fireTileRedraw(selectedTileIndexList, action, temporary);
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

	public static void save(File file, TileRepositoryService service, Project project) {
		try {
			FileWriter fw = new FileWriter(file);
			fw.write(getHeaderText(project, service.getMetadata()));
			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			mapper.writeValue(fw, service);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String getHeaderText(Project project, ProjectMetaData metadata) {
		String s = String.format(Constants.PROJECT_FILE_INFO_HEADER, project.getName(),
				DateFormat.getDateInstance(DateFormat.SHORT).format(project.getCreatedOn()),
				DateFormat.getDateInstance(DateFormat.SHORT).format(project.getChangedOn()));
		return s;
	}
}
