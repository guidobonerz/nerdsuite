package de.drazil.nerdsuite.widget;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class Tile {

	private List<ITileListener> tileListenerList = null;
	private List<Layer> layerList = null;
	private List<Integer> layerIndexOrderList = null;

	@Setter
	@Getter
	private String name = null;
	private int size = 0;

	public Tile(int size) {
		this("<rename me>", size);
	}

	public Tile(String name, int size) {
		this.name = name;
		this.size = size;
		layerList = new ArrayList<>();
		layerIndexOrderList = new ArrayList<Integer>();
		tileListenerList = new ArrayList<ITileListener>();
		addLayer();
	}

	public List<Integer> getLayerIndexOrderList() {
		return layerIndexOrderList;
	}

	public Layer getLayer(int index) {
		return layerList.get(index);
	}

	public void addLayer() {
		addLayer("<rename me>");
	}

	public Layer addLayer(String name) {
		Layer layer = new Layer(name, size);
		if (layerList.size() == 0) {
			layer.setActive(true);
		}
		layerList.add(layer);
		layerIndexOrderList.add(layerList.indexOf(layer));
		fireLayerAdded();
		return layer;
	}

	public void removeLayer(int index) {
		int layerIndex = layerIndexOrderList.get(index);
		layerList.remove(layerIndex);
		layerIndexOrderList.remove(index);
		fireLayerRemoved();
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

	public void setLayerVisible(int index, boolean visible) {
		layerList.get(layerIndexOrderList.get(index)).setVisible(visible);
		fireLayerVisibilityChanged(index);
	}

	public void setLayerActive(int index, boolean active) {
		layerList.forEach(layer -> layer.setActive(false));
		layerList.get(layerIndexOrderList.get(index)).setActive(active);
		fireActiveLayerChanged(index);
	}

	public Layer getActiveLayer() {
		return layerList.stream().filter(x -> x.isActive()).findFirst().orElse(null);
	}

	public void setLayerContent(int index, int content[]) {
		layerList.get(layerIndexOrderList.get(index)).setContent(content);
		fireLayerContentChanged(index);
	}

	public void addTileListener(ITileListener listener) {
		tileListenerList.add(listener);
	}

	public void removeTileListener(ITileListener listener) {
		tileListenerList.remove(listener);
	}

	private void fireLayerAdded() {
		tileListenerList.forEach(listener -> listener.layerAdded());
	}

	private void fireLayerRemoved() {
		tileListenerList.forEach(listener -> listener.layerRemoved());
	}

	private void fireLayerVisibilityChanged(int layer) {
		tileListenerList.forEach(listener -> listener.layerVisibilityChanged(layer));
	}

	private void fireLayerContentChanged(int layer) {
		tileListenerList.forEach(listener -> listener.layerContentChanged(layer));
	}

	private void fireLayerReordered() {
		tileListenerList.forEach(listener -> listener.layerReordered());
	}

	private void fireActiveLayerChanged(int layer) {
		tileListenerList.forEach(listener -> listener.activeLayerChanged(layer));
	}

}
