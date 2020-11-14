package de.drazil.nerdsuite.imaging.service;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Rectangle;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.configuration.Configuration;
import de.drazil.nerdsuite.enums.RedrawMode;
import de.drazil.nerdsuite.model.Project;
import de.drazil.nerdsuite.model.ProjectMetaData;
import de.drazil.nerdsuite.widget.ITileListener;
import de.drazil.nerdsuite.widget.Layer;
import de.drazil.nerdsuite.widget.Tile;
import de.drazil.nerdsuite.widget.TileContainer;
import lombok.Getter;
import lombok.Setter;

public class TileRepositoryService implements IService {
	@Getter
	@Setter
	private String owner = null;
	@Getter
	@Setter
	private Rectangle selection;
	private TileContainer container;
	private List<ITileManagementListener> tileServiceManagementListener = null;
	private List<ITileUpdateListener> tileUpdateListener = null;
	private List<ITileListener> tileListenerList = null;
	@Getter
	private TileRepositoryService referenceRepository;

	public TileRepositoryService() {
		tileServiceManagementListener = new ArrayList<>();
		tileUpdateListener = new ArrayList<>();
		container = new TileContainer();
	}

	public void init(int initialSize) {
		container.setInitialSize(initialSize);
	}

	public boolean hasReference() {
		return referenceRepository != null;
	}

	public void setMetadata(ProjectMetaData metadata) {
		container.setMetadata(metadata);
	}

	public ProjectMetaData getMetadata() {
		return container.getMetadata();
	}

	public String getSelectedTileName() {
		return getSelectedTile().getName();
	}

	public String getTileName(int index) {
		return getTile(index).getName();
	}

	public Tile getSelectedTile() {
		return container.getSelectedTile();
	}

	public Layer getActiveLayerFromSelectedTile() {
		return getSelectedTile().getActiveLayer();
	}

	public Layer getActiveLayerFromTile(int index) {
		return getTile(index).getActiveLayer();
	}

	public int getSelectedTileIndex() {
		return container.getSelectedTileIndex();
	}

	public void setSelectedTileIndex(int index) {
		container.setSelectedTileIndex(index);
	}

	public void setSelectedTileIndexList(List<Integer> tileIndexList) {
		container.setSelectedTileIndexList(tileIndexList);
	}

	public List<Integer> getSelectedTileIndexList() {
		return container.getSelectedTileIndexList();
	}

	public void moveTile(int from, int to) {
		container.moveTile(from, to);
	}

	public int getSize() {
		return container.getSize();
	}

	public Tile getTile(int index) {
		return container.getTile(index);
	}

	public Tile getTile(int index, boolean naturalOrder) {
		return container.getTile(index, naturalOrder);
	}

	public int getTileIndex(int index) {
		return container.getTileIndex(index);
	}

	public Tile addTile() {
		return container.addTile();
	}

	public void addTileListener(ITileListener listener) {
		createTileListenerList();
		tileListenerList.add(listener);
	}

	public void removeTileListener(ITileListener listener) {
		createTileListenerList();
		tileListenerList.remove(listener);
	}

	private void fireLayerAdded() {
		createTileListenerList();
		tileListenerList.forEach(listener -> listener.layerAdded());
	}

	private void fireLayerRemoved() {
		createTileListenerList();
		tileListenerList.forEach(listener -> listener.layerRemoved());
	}

	private void fireLayerVisibilityChanged(int layer) {
		createTileListenerList();
		tileListenerList.forEach(listener -> listener.layerVisibilityChanged(layer));
	}

	private void fireLayerContentChanged(int layer) {
		createTileListenerList();
		tileListenerList.forEach(listener -> listener.layerContentChanged(layer));
	}

	private void fireLayerReordered() {
		createTileListenerList();
		tileListenerList.forEach(listener -> listener.layerReordered());
	}

	private void fireActiveLayerChanged(int layer) {
		createTileListenerList();
		tileListenerList.forEach(listener -> listener.activeLayerChanged(layer));
	}

	private void fireTileChanged() {
		createTileListenerList();
		tileListenerList.forEach(listener -> listener.tileChanged());
	}

	public void sendModificationNotification() {
		fireLayerContentChanged(0);
	}

	private void createTileListenerList() {
		if (tileListenerList == null) {
			tileListenerList = new ArrayList<>();
		}
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
		tileServiceManagementListener.forEach(listener -> listener.tileAdded(container.getSelectedTile()));
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
				tileUpdateListener.forEach(listener -> listener.redrawTiles(selectedTileIndexList, temporary ? RedrawMode.DrawTemporarySelectedTile : RedrawMode.DrawSelectedTile, action));
			} else {
				tileUpdateListener.forEach(listener -> listener.redrawTiles(selectedTileIndexList, RedrawMode.DrawSelectedTiles, action));
			}
		}
	}

	public void redrawTileViewer(List<Integer> selectedTileIndexList, int action, boolean temporary) {
		fireTileRedraw(selectedTileIndexList, action, temporary);
	}

	public TileContainer load(File fileName) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
		try {
			container = mapper.readValue(fileName, TileContainer.class);
			String referenceId = null;
			String referenceRepositoryLocation = container.getReferenceRepositoryLocation();
			if (null != referenceRepositoryLocation) {
				File referenceFile = Path.of(Configuration.WORKSPACE_PATH.toString(), referenceRepositoryLocation).toFile();
				referenceId = referenceFile.getName().split("\\.")[0].toUpperCase();
				referenceRepository = ServiceFactory.getService(referenceId, TileRepositoryService.class);
				referenceRepository.load(referenceFile);
				container.getMetadata().setReferenceRepositoryId(referenceId);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return container;
	}

	public void save(File file, Project project) {
		try {
			FileWriter fw = new FileWriter(file);
			fw.write(getHeaderText(project, container.getMetadata()));
			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			mapper.writeValue(fw, container);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String getHeaderText(Project project, ProjectMetaData metadata) {
		String s = String.format(Constants.PROJECT_FILE_INFO_HEADER, project.getName(), DateFormat.getDateInstance(DateFormat.SHORT).format(project.getCreatedOn()),
				DateFormat.getDateInstance(DateFormat.SHORT).format(project.getChangedOn()));
		return s;
	}
}
