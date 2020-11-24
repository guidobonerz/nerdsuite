package de.drazil.nerdsuite.widget;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Point;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class Tile {
	@Getter
	@JsonProperty(value = "name")
	private String name = null;
	@JsonProperty(value = "showOnlyActiveLayer")
	private boolean showOnlyActiveLayer = true;
	@JsonProperty(value = "showInactiveLayerTranslucent")
	private boolean showInactiveLayerTranslucent = false;
	@Getter
	@JsonProperty(value = "multicolor")
	private boolean multicolorEnabled = false;
	@JsonProperty(value = "backgroundColor")
	private int backgroundColorIndex = 0;
	@JsonProperty(value = "originX")
	private int originX;
	@JsonProperty(value = "originY")
	private int originY;
	@JsonProperty(value = "layers")
	private List<Layer> layerList = new ArrayList<Layer>();
	@JsonProperty(value = "layerIndexOrder")
	private List<Integer> layerIndexOrderList = new ArrayList<Integer>();
	@JsonIgnore
	private List<ITileListener> tileListenerList = null;
	@JsonIgnore
	private int size;
	@JsonIgnore
	@Getter
	@Setter
	private boolean dirty = true;

	public Tile(String name, int size) {
		this.name = name;
		this.size = size;
	}

	@JsonIgnore
	public Layer getLayer(int index) {
		return layerList.get(index);
	}

	@JsonIgnore
	public Layer addLayer(String name, int size, int brushValue) {
		Layer layer = new Layer(name, size, brushValue);
		layer.getColorPalette().add(0);
		layer.getColorPalette().add(1);
		layer.getColorPalette().add(2);
		layer.getColorPalette().add(3);

		layerList.add(layer);
		layerIndexOrderList.add(layerList.indexOf(layer));
		layerList.forEach(l -> l.setActive(false));
		layerList.get(layerIndexOrderList.size() - 1).setActive(true);
		layer.setSelectedColorIndex(0);
		fireLayerAdded();
		return layer;
	}

	@JsonIgnore
	public void removeActiveLayer() {

	}

	@JsonIgnore
	public void removeLayer(int index) {
		if (layerIndexOrderList.size() > 0) {
			int layerIndex = layerIndexOrderList.get(index);
			layerList.remove(layerIndex);
			layerIndexOrderList.remove(index);
			fireLayerRemoved();
		}
	}

	@JsonIgnore
	public void moveToFront(int index) {
		if (index < 1) {
			return;
		}
		layerIndexOrderList.remove(index);
		layerIndexOrderList.add(0, index);
		fireLayerReordered();
	}

	@JsonIgnore
	public void moveToBack(Tile tile, int index) {
		if (index < 1) {
			return;
		}
		layerIndexOrderList.remove(index);
		layerIndexOrderList.add(index);
		fireLayerReordered();
	}

	@JsonIgnore
	public void moveUp(int index) {
		if (index < 1) {
			return;
		}
		layerIndexOrderList.remove(index);
		layerIndexOrderList.add(index - 1, index);
		fireLayerReordered();
	}

	@JsonIgnore
	public void moveDown(int index) {
		if (index < 1) {
			return;
		}
		layerIndexOrderList.remove(index);
		layerIndexOrderList.add(index + 1, index);
		fireLayerReordered();
	}

	@JsonIgnore
	public void move(int from, int to) {

	}

	@JsonIgnore
	public void setMulticolorEnabled(boolean multicolorEnabled) {
		this.multicolorEnabled = multicolorEnabled;
		fireTileChanged();
	}

	@JsonIgnore
	public void setShowOnlyActiveLayer(boolean showOnlyActiveLayer) {
		this.showOnlyActiveLayer = showOnlyActiveLayer;
		fireLayerVisibilityChanged(-1);
	}

	@JsonIgnore
	public void setShowInactiveLayerTranslucent(boolean showInactiveLayerTranslucent) {
		setShowInactiveLayerTranslucent(showInactiveLayerTranslucent);
		fireLayerVisibilityChanged(-1);
	}

	@JsonIgnore
	public void setLayerVisible(int index, boolean visible) {
		layerList.get(layerIndexOrderList.get(index)).setVisible(visible);
		fireLayerVisibilityChanged(index);
	}

	@JsonIgnore
	public void setLayerActive(int index, boolean active) {
		layerList.forEach(layer -> layer.setActive(false));
		layerList.get(layerIndexOrderList.get(index)).setActive(active);
		fireActiveLayerChanged(index);
	}

	@JsonIgnore
	public void setLayerLocked(int index, boolean active) {
		layerList.get(layerIndexOrderList.get(index)).setLocked(active);
		fireActiveLayerChanged(index);
	}

	@JsonIgnore
	public void resetActiveLayer() {
		int size = getActiveLayer().getContent().length;

	}

	@JsonIgnore
	public Layer getActiveLayer() {
		return layerList.stream().filter(x -> x.isActive()).findFirst().orElse(null);
	}

	@JsonIgnore
	public void setActiveLayerColorIndex(int index, int colorIndex, boolean select) {
		Layer layer = getActiveLayer();
		layer.getColorPalette().set(index, colorIndex);
		if (select) {
			getActiveLayer().setSelectedColorIndex(index);
		}
		fireActiveLayerChanged(-1);
	}

	@JsonIgnore
	public int getColorIndex(int colorIndex) {
		return getActiveLayer().getColorPalette().get(colorIndex);
	}

	@JsonIgnore
	public void setOrigin(Point origin) {
		this.originX = origin.x;
		this.originY = origin.y;
	}

	@JsonIgnore
	public Point getOrigin() {
		return new Point(originX, originY);
	}

	@JsonIgnore
	public void addTileListener(ITileListener listener) {
		createTileListenerList();
		tileListenerList.add(listener);
	}

	@JsonIgnore
	public void removeTileListener(ITileListener listener) {
		createTileListenerList();
		tileListenerList.remove(listener);
	}

	@JsonIgnore
	private void createTileListenerList() {
		if (tileListenerList == null) {
			tileListenerList = new ArrayList<>();
		}
	}

	@JsonIgnore
	private void fireLayerAdded() {
		createTileListenerList();
		tileListenerList.forEach(listener -> listener.layerAdded());
	}

	@JsonIgnore
	private void fireLayerRemoved() {
		createTileListenerList();
		tileListenerList.forEach(listener -> listener.layerRemoved());
	}

	@JsonIgnore
	private void fireLayerVisibilityChanged(int layer) {
		createTileListenerList();
		tileListenerList.forEach(listener -> listener.layerVisibilityChanged(layer));
	}

	@JsonIgnore
	private void fireLayerContentChanged(int layer) {
		createTileListenerList();
		tileListenerList.forEach(listener -> listener.layerContentChanged(layer));
	}

	@JsonIgnore
	private void fireLayerReordered() {
		createTileListenerList();
		tileListenerList.forEach(listener -> listener.layerReordered());
	}

	@JsonIgnore
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

}
