package de.drazil.nerdsuite.widget;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Color;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.assembler.InstructionSet;
import lombok.Getter;
import lombok.Setter;

public class Tile {

	private List<ITileListener> tileListenerList = null;
	@Getter
	private List<Layer> layerList = null;
	private List<Integer> layerIndexOrderList = null;

	@Setter
	@Getter
	private String name = null;
	private int size = 0;
	@Setter
	@Getter
	private Color backgroundColor = Constants.BLACK;
	@Getter
	private boolean showOnlyActiveLayer = false;
	@Getter
	private boolean showInactiveLayerTranslucent = false;

	public Tile(int size) {
		this("rename_me", size);
	}

	public Tile(String name, int size) {
		this.name = name;
		this.size = size;
		layerList = new ArrayList<>();
		layerIndexOrderList = new ArrayList<>();
		tileListenerList = new ArrayList<>();
		addLayer();
	}

	public List<Integer> getLayerIndexOrderList() {
		return layerIndexOrderList;
	}

	public Layer getLayer(int index) {
		return layerList.get(index);
	}

	public Layer addLayer() {
		return addLayer("rename_me");
	}

	public Layer addLayer(String name) {
		Layer layer = new Layer(name, size);

		// default palette
		layer.setColor(0, InstructionSet.getPlatformData().getColorPalette().get(0).getColor());
		layer.setColor(1, InstructionSet.getPlatformData().getColorPalette().get(1).getColor());
		layer.setColor(2, InstructionSet.getPlatformData().getColorPalette().get(2).getColor());
		layer.setColor(3, InstructionSet.getPlatformData().getColorPalette().get(3).getColor());

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

	public void sendModificationNotification() {
		fireLayerContentChanged(0);
	}
}
