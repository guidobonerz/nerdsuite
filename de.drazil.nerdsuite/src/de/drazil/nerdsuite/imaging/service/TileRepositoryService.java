package de.drazil.nerdsuite.imaging.service;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
	@Getter
	private ImagePainterFactory imagePainterFactory;
	private TileContainer container;
	private List<ITileManagementListener> tileServiceManagementListener = null;
	private List<ITileUpdateListener> tileUpdateListener = null;
	private List<ITileListener> tileListenerList = null;
	private TileRepositoryService referenceRepository;

	private int tileSize;

	public TileRepositoryService() {
		tileServiceManagementListener = new ArrayList<>();
		tileUpdateListener = new ArrayList<>();
		imagePainterFactory = new ImagePainterFactory();
		container = new TileContainer();
	}

	public Image getSelectedImage() {
		return imagePainterFactory.getImageByName(getSelectedTile().getName());
	}

	public Image getImage(int index) {
		return imagePainterFactory.getImageByName(getTile(index).getName());
	}

	public void setMetadata(ProjectMetaData metadata) {
		container.setMetadata(metadata);
		computeTileSize();
	}

	public ProjectMetaData getMetadata() {
		return container.getMetadata();
	}

	private void computeTileSize() {
		tileSize = container.getMetadata().getHeight() * container.getMetadata().getWidth()
				* container.getMetadata().getColumns() * container.getMetadata().getRows();
	}

	public void setInitialSize(int size) {
		for (int i = 0; i < size; i++) {
			addTileInternal("tile_" + (container.getTileList().size() + 1));
		}
		setSelectedTileIndex(0);
	}

	public Tile addTile() {
		return addTile("tile_" + (container.getTileList().size() + 1));
	}

	public Tile addTile(String name) {
		Tile tile = addTileInternal(name);
		setSelectedTileIndex(container.getTileIndexOrderList().get(getSize() - 1));
		fireTileAdded();
		return tile;
	}

	private Tile addTileInternal(String name) {
		Tile tile = new Tile();
		tile.setName(name);
		addLayer(tile, name, tileSize);
		container.getTileList().add(tile);
		container.getTileIndexOrderList().add(container.getTileList().indexOf(tile));
		return tile;
	}

	public void removeLast() {
		if (container.getTileIndexOrderList().size() > 0) {
			List<Integer> l = new ArrayList<Integer>();
			l.add(container.getTileIndexOrderList().size() - 1);
			removeTile(l);
		}
	}

	public void removeSelected() {
		removeTile(container.getSelectedTileIndexList());
	}

	public void removeTile(List<Integer> tileIndexList) {
		if (container.getTileIndexOrderList().size() > 0) {
			for (int i = 0; i < tileIndexList.size(); i++) {
				int tileIndex = container.getTileIndexOrderList().get(i);
				container.getTileList().remove(tileIndex);
				container.getTileIndexOrderList().remove(i);
			}
			fireTileRemoved();
		}
	}

	public void moveTile(int from, int to) {
		int v = container.getTileIndexOrderList().get(from);
		if (to < from) {
			container.getTileIndexOrderList().remove(from);
			container.getTileIndexOrderList().add(to, v);
		} else {
			container.getTileIndexOrderList().add(to, v);
			container.getTileIndexOrderList().remove(from);
		}
		fireTileReordered();
	}

	public int getTileIndex(int index) {
		return container.getTileIndexOrderList().get(index);
	}

	public void setSelectedTileIndex(int index) {
		container.getSelectedTileIndexList().clear();
		container.getSelectedTileIndexList().add(index);
		fireTileRedraw(container.getSelectedTileIndexList(), ImagePainterFactory.READ, false);

	}

	public void setSelectedTileIndexList(List<Integer> tileIndexList) {
		container.setSelectedTileIndexList(tileIndexList);
		fireTileRedraw(tileIndexList, ImagePainterFactory.READ, false);
	}

	public List<Integer> getSelectedTileIndexList() {
		return container.getSelectedTileIndexList();
	}

	@JsonIgnore
	public Tile getSelectedTile() {
		int index = container.getSelectedTileIndexList().get(0);
		return getTile(index);
	}

	@JsonIgnore
	public int getSelectedTileIndex() {
		return container.getTileList().indexOf(getSelectedTile());
	}

	public Tile getTile(int index) {
		return getTile(index, false);
	}

	public Tile getTile(int index, boolean naturalOrder) {
		return container.getTileList().get(naturalOrder ? index : container.getTileIndexOrderList().get(index));
	}

	public int getSize() {
		return container.getTileList().size();
	}

	public void setOrigin(Point origin) {
		setOrigin(getSelectedTile(), origin);
	}

	public void setOrigin(int tileIndex, Point origin) {
		setOrigin(getTile(tileIndex), origin);
	}

	public void setOrigin(Tile tile, Point origin) {
		tile.setOriginX(origin.x);
		tile.setOriginX(origin.y);
	}

	public Point getOrigin() {
		return getOrigin(getSelectedTile());
	}

	public Point getOrigin(int tileIndex) {
		return getOrigin(getTile(tileIndex));
	}

	public Point getOrigin(Tile tile) {
		return new Point(tile.getOriginX(), tile.getOriginY());
	}

	public List<Integer> getLayerIndexOrderList(Tile tile) {
		return tile.getLayerIndexOrderList();
	}

	public List<Integer> getLayerIndexOrderList() {
		return getLayerIndexOrderList(getSelectedTile());
	}

	public Layer getLayer(int tileIndex, int index) {
		return getLayer(getTile(tileIndex), index);
	}

	public Layer getLayer(Tile tile, int index) {
		return tile.getLayerList().get(index);
	}

	public Layer getLayer(int index) {
		return getLayer(getSelectedTile(), index);
	}

	public Layer addLayer(Tile tile) {
		return addLayer(tile, "layer_" + (tile.getLayerList().size() + 1), tileSize);
	}

	public Layer addLayer() {
		return addLayer(getSelectedTile());
	}

	public Layer addLayer(Tile tile, String name, int size) {
		Layer layer = new Layer();
		layer.setName(name);
		layer.setContent(new int[tileSize]);
		layer.getColorPalette().add(0);
		layer.getColorPalette().add(1);
		layer.getColorPalette().add(2);
		layer.getColorPalette().add(3);

		tile.getLayerList().add(layer);
		tile.getLayerIndexOrderList().add(tile.getLayerList().indexOf(layer));
		tile.getLayerList().forEach(l -> l.setActive(false));
		tile.getLayerList().get(tile.getLayerIndexOrderList().size() - 1).setActive(true);
		layer.setSelectedColorIndex(0);
		fireLayerAdded();
		return layer;
	}

	public void removeActiveLayer() {

	}

	public void removeLastLayer() {
		removeLastLayer(getSelectedTile());
	}

	public void removeLastLayer(Tile tile) {
		if (tile.getLayerIndexOrderList().size() > 0) {
			removeLayer(tile.getLayerIndexOrderList().get(tile.getLayerIndexOrderList().size()) - 1);
		}
	}

	public void removeLayer(int index) {
		removeLayer(getSelectedTile(), index);
	}

	public void removeLayer(Tile tile, int index) {
		if (tile.getLayerIndexOrderList().size() > 0) {
			int layerIndex = tile.getLayerIndexOrderList().get(index);
			tile.getLayerList().remove(layerIndex);
			tile.getLayerIndexOrderList().remove(index);
			fireLayerRemoved();
		}
	}

	public void moveToFront(int index) {
		moveToFront(getSelectedTile(), index);
	}

	public void moveToFront(Tile tile, int index) {
		if (index < 1) {
			return;
		}
		tile.getLayerIndexOrderList().remove(index);
		tile.getLayerIndexOrderList().add(0, index);
		fireLayerReordered();
	}

	public void moveToBack(int index) {
		moveToBack(getSelectedTile(), index);
	}

	public void moveToBack(Tile tile, int index) {
		if (index < 1) {
			return;
		}
		tile.getLayerIndexOrderList().remove(index);
		tile.getLayerIndexOrderList().add(index);
		fireLayerReordered();
	}

	public void moveUp(int index) {
		moveUp(getSelectedTile(), index);
	}

	public void moveUp(Tile tile, int index) {
		if (index < 1) {
			return;
		}
		tile.getLayerIndexOrderList().remove(index);
		tile.getLayerIndexOrderList().add(index - 1, index);
		fireLayerReordered();
	}

	public void moveDown(int index) {
		moveDown(getSelectedTile(), index);
	}

	public void moveDown(Tile tile, int index) {
		if (index < 1) {
			return;
		}
		tile.getLayerIndexOrderList().remove(index);
		tile.getLayerIndexOrderList().add(index + 1, index);
		fireLayerReordered();
	}

	public void move(int from, int to) {

	}

	public void setMulticolorEnabled(boolean multicolorEnabled) {
		setMulticolorEnabled(getSelectedTile(), multicolorEnabled);
	}

	public void setMulticolorEnabled(Tile tile, boolean multicolorEnabled) {
		tile.setMulticolor(multicolorEnabled);
		fireTileChanged();
	}

	public void setShowOnlyActiveLayer(boolean showOnlyActiveLayer) {
		setShowOnlyActiveLayer(getSelectedTile(), showOnlyActiveLayer);
	}

	public void setShowOnlyActiveLayer(Tile tile, boolean showOnlyActiveLayer) {
		tile.setShowOnlyActiveLayer(showOnlyActiveLayer);
		fireLayerVisibilityChanged(-1);
	}

	public void setShowInactiveLayerTranslucent(boolean showInactiveLayerTranslucent) {
		setShowInactiveLayerTranslucent(getSelectedTile(), showInactiveLayerTranslucent);
	}

	public void setShowInactiveLayerTranslucent(Tile tile, boolean showInactiveLayerTranslucent) {
		tile.setShowInactiveLayerTranslucent(showInactiveLayerTranslucent);
		fireLayerVisibilityChanged(-1);
	}

	public void setLayerVisible(int index, boolean visible) {
		setLayerVisible(getSelectedTile(), index, visible);
	}

	public void setLayerVisible(Tile tile, int index, boolean visible) {
		tile.getLayerList().get(tile.getLayerIndexOrderList().get(index)).setVisible(visible);
		fireLayerVisibilityChanged(index);
	}

	public void setLayerActive(int index, boolean active) {
		setLayerActive(getSelectedTile(), index, active);
	}

	public void setLayerActive(Tile tile, int index, boolean active) {
		tile.getLayerList().forEach(layer -> layer.setActive(false));
		tile.getLayerList().get(tile.getLayerIndexOrderList().get(index)).setActive(active);
		fireActiveLayerChanged(index);
	}

	public void setLayerLocked(int index, boolean active) {
		setLayerLocked(getSelectedTile(), index, active);
	}

	public void setLayerLocked(Tile tile, int index, boolean active) {
		tile.getLayerList().get(tile.getLayerIndexOrderList().get(index)).setLocked(active);
		fireActiveLayerChanged(index);
	}

	public void resetActiveLayer() {
		resetActiveLayer(getSelectedTile());
	}

	public void resetActiveLayer(int tileIndex) {
		resetActiveLayer(getTile(tileIndex));
	}

	public void resetActiveLayer(Tile tile) {
		getActiveLayer(tile).setContent(new int[tileSize]);
	}

	public void resetLayer(int index) {
		resetLayer(getSelectedTile(), index);
	}

	public void resetLayer(int tileIndex, int index) {
		resetLayer(getTile(tileIndex), index);
	}

	public void resetLayer(Tile tile, int index) {
		getLayer(tile, index).setContent(new int[tileSize]);
	}

	public Layer getActiveLayer() {
		return getActiveLayer(getSelectedTile());
	}

	public Layer getActiveLayer(int tileIndex) {
		return getActiveLayer(getTile(tileIndex));
	}

	public Layer getActiveLayer(Tile tile) {
		return tile.getLayerList().stream().filter(x -> x.isActive()).findFirst().orElse(null);
	}

	public void setActiveLayerColorIndex(int index, int colorIndex, boolean select) {
		getActiveLayer().getColorPalette().set(index, colorIndex);
		if (select) {
			getActiveLayer().setSelectedColorIndex(index);
		}
		fireActiveLayerChanged(-1);
	}

	public int getColorIndex(int colorIndex) {
		return getActiveLayer().getColorPalette().get(colorIndex);
	}

	/*
	 * public void setLayerContent(int index, int content[]) {
	 * layerList.get(layerIndexOrderList.get(index)).setContent(content);
	 * fireLayerContentChanged(index); }
	 */
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

	public TileContainer load(File fileName) {
		TileContainer container = null;
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
		try {
			container = mapper.readValue(fileName, TileContainer.class);

			String referenceRepositoryLocation = container.getReferenceRepositoryLocation();
			if (null != referenceRepositoryLocation) {
				File referenceFile = Path.of(Configuration.WORKSPACE_PATH.toString(), referenceRepositoryLocation)
						.toFile();
				String referenceOwner = "C64_UPPER";
				referenceRepository = ServiceFactory.getService(referenceOwner, TileRepositoryService.class);
				referenceRepository.load(referenceFile);
			}

			computeTileSize();
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
		String s = String.format(Constants.PROJECT_FILE_INFO_HEADER, project.getName(),
				DateFormat.getDateInstance(DateFormat.SHORT).format(project.getCreatedOn()),
				DateFormat.getDateInstance(DateFormat.SHORT).format(project.getChangedOn()));
		return s;
	}
}
