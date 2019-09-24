package de.drazil.nerdsuite.widget;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

public class Tile {

	@JsonIgnore
	private List<ITileListener> tileListenerList = null;
	@Setter
	@Getter
	private String name = null;
	private int size = 0;
	@Setter
	@Getter
	@JsonIgnore
	private int backgroundColorIndex = 0;
	@Getter
	private boolean showOnlyActiveLayer = false;
	@Getter
	private boolean showInactiveLayerTranslucent = false;
	@Setter
	@Getter
	private boolean multicolor = false;
	@Getter
	@JsonProperty(value = "layers")
	private List<Layer> layerList = null;
	@JsonProperty(value = "layerIndexOrder")
	private List<Integer> layerIndexOrderList = null;
	@Setter
	@Getter
	private boolean isEmpty = true;

	public Tile() {
		createTileListenerList();
		createLayerList();
	}

	public Tile(int size) {
		this("rename_me", size);
	}

	public Tile(String name, int size) {
		this();
		this.name = name;
		this.size = size;
		addLayer();
	}

	public List<Integer> getLayerIndexOrderList() {
		return layerIndexOrderList;
	}

	public Layer getLayer(int index) {
		return layerList.get(index);
	}

	public Layer addLayer() {
		return addLayer("layer_" + (layerList.size() + 1));
	}

	public Layer addLayer(String name) {

		createLayerList();
		Layer layer = new Layer(name, size);

		// default palette
		layer.setColorIndex(0, 0);
		layer.setColorIndex(1, 1);
		layer.setColorIndex(2, 2);
		layer.setColorIndex(3, 3);

		layerList.add(layer);
		layerIndexOrderList.add(layerList.indexOf(layer));
		layerList.forEach(l -> l.setActive(false));
		layerList.get(layerIndexOrderList.size() - 1).setActive(true);
		layer.setSelectedColorIndex(0);
		fireLayerAdded();
		return layer;
	}

	public void removeActiveLayer() {

	}

	public void removeLastLayer() {
		if (layerIndexOrderList.size() > 0) {
			removeLayer(layerIndexOrderList.get(layerIndexOrderList.size()) - 1);
		}
	}

	public void removeLayer(int index) {
		if (layerIndexOrderList.size() > 0) {
			int layerIndex = layerIndexOrderList.get(index);
			layerList.remove(layerIndex);
			layerIndexOrderList.remove(index);
			fireLayerRemoved();
		}
	}

	public void moveToFront(int index) {
		if (index < 1) {
			return;
		}
		layerIndexOrderList.remove(index);
		layerIndexOrderList.add(0, index);
		fireLayerReordered();
	}

	public void moveToBack(int index) {
		if (index < 1) {
			return;
		}
		layerIndexOrderList.remove(index);
		layerIndexOrderList.add(index);
		fireLayerReordered();
	}

	public void moveUp(int index) {
		if (index < 1) {
			return;
		}
		layerIndexOrderList.remove(index);
		layerIndexOrderList.add(index - 1, index);
		fireLayerReordered();
	}

	public void moveDown(int index) {
		if (index < 1) {
			return;
		}
		layerIndexOrderList.remove(index);
		layerIndexOrderList.add(index + 1, index);
		fireLayerReordered();
	}

	public void setShowOnlyActiveLayer(boolean showOnlyActiveLayer) {
		this.showOnlyActiveLayer = showOnlyActiveLayer;
		fireLayerVisibilityChanged(-1);
	}

	public void setShowInactiveLayerTranslucent(boolean showInactiveLayerTranslucent) {
		this.showInactiveLayerTranslucent = showInactiveLayerTranslucent;
		fireLayerVisibilityChanged(-1);
	}

	public void setLayerVisible(int index, boolean visible) {
		layerList.get(layerIndexOrderList.get(index)).setVisible(visible);
		fireLayerVisibilityChanged(index);
	}

	public void setLayerActive(int index, boolean active) {
		layerList.forEach(layer -> layer.setActive(false));
		layerList.get(layerIndexOrderList.get(index)).setActive(active);
		fireActiveLayerChanged(index);
	}

	@JsonIgnore
	public Layer getActiveLayer() {
		return layerList.stream().filter(x -> x.isActive()).findFirst().orElse(null);
	}

	public void setLayerContent(int index, int content[]) {
		layerList.get(layerIndexOrderList.get(index)).setContent(content);
		fireLayerContentChanged(index);
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

	public void sendModificationNotification() {
		fireLayerContentChanged(0);
	}

	private void createTileListenerList() {
		if (tileListenerList == null) {
			tileListenerList = new ArrayList<>();
			layerIndexOrderList = new ArrayList<>();
		}
	}

	private void createLayerList() {
		if (layerList == null) {
			layerList = new ArrayList<>();
		}
	}
}
