package de.drazil.nerdsuite.widget;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Point;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Tile {
	@JsonProperty(value = "name")
	private String name = null;
	@JsonProperty(value = "showOnlyActiveLayer")
	private boolean showOnlyActiveLayer = true;
	@JsonProperty(value = "showInactiveLayerTranslucent")
	private boolean showInactiveLayerTranslucent = false;
	@JsonProperty(value = "multicolor")
	private boolean multicolor = false;
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
	private int size;

	public Tile(String name, int size) {
		this.name = name;
		this.size = size;
	}

	@JsonIgnore
	public List<Integer> getLayerIndexOrderList() {
		return getLayerIndexOrderList();
	}

	@JsonIgnore
	public Layer getLayer(int index) {
		return layerList.get(index);
	}

	@JsonIgnore
	public Layer addLayer(String name, int size, int contentValue, int brushValue) {
		Layer layer = new Layer(name, size, contentValue, brushValue);
		layer.getColorPalette().add(0);
		layer.getColorPalette().add(1);
		layer.getColorPalette().add(2);
		layer.getColorPalette().add(3);

		getLayerList().add(layer);
		getLayerIndexOrderList().add(getLayerList().indexOf(layer));
		getLayerList().forEach(l -> l.setActive(false));
		getLayerList().get(getLayerIndexOrderList().size() - 1).setActive(true);
		layer.setSelectedColorIndex(0);
		// fireLayerAdded();
		return layer;
	}

	@JsonIgnore
	public void removeActiveLayer() {

	}

	@JsonIgnore
	public void removeLayer(Tile tile, int index) {
		if (tile.getLayerIndexOrderList().size() > 0) {
			int layerIndex = tile.getLayerIndexOrderList().get(index);
			tile.getLayerList().remove(layerIndex);
			tile.getLayerIndexOrderList().remove(index);
			// fireLayerRemoved();
		}
	}

	@JsonIgnore
	public void moveToFront(int index) {
		if (index < 1) {
			return;
		}
		getLayerIndexOrderList().remove(index);
		getLayerIndexOrderList().add(0, index);
		// fireLayerReordered();
	}

	@JsonIgnore
	public void moveToBack(Tile tile, int index) {
		if (index < 1) {
			return;
		}
		getLayerIndexOrderList().remove(index);
		getLayerIndexOrderList().add(index);
		// fireLayerReordered();
	}

	@JsonIgnore
	public void moveUp(int index) {
		if (index < 1) {
			return;
		}
		getLayerIndexOrderList().remove(index);
		getLayerIndexOrderList().add(index - 1, index);
		// fireLayerReordered();
	}

	@JsonIgnore
	public void moveDown(int index) {
		if (index < 1) {
			return;
		}
		getLayerIndexOrderList().remove(index);
		getLayerIndexOrderList().add(index + 1, index);
		// fireLayerReordered();
	}

	@JsonIgnore
	public void move(int from, int to) {

	}

	@JsonIgnore
	public void setMulticolorEnabled(boolean multicolorEnabled) {
		setMulticolor(multicolorEnabled);
		// fireTileChanged();
	}

	@JsonIgnore
	public void setShowOnlyActiveLayer(boolean showOnlyActiveLayer) {
		setShowOnlyActiveLayer(showOnlyActiveLayer);
		// fireLayerVisibilityChanged(-1);
	}

	@JsonIgnore
	public void setShowInactiveLayerTranslucent(boolean showInactiveLayerTranslucent) {
		setShowInactiveLayerTranslucent(showInactiveLayerTranslucent);
		// fireLayerVisibilityChanged(-1);
	}

	@JsonIgnore
	public void setLayerVisible(int index, boolean visible) {
		getLayerList().get(getLayerIndexOrderList().get(index)).setVisible(visible);
		// fireLayerVisibilityChanged(index);
	}

	@JsonIgnore
	public void setLayerActive(int index, boolean active) {
		getLayerList().forEach(layer -> layer.setActive(false));
		getLayerList().get(getLayerIndexOrderList().get(index)).setActive(active);
		// fireActiveLayerChanged(index);
	}

	@JsonIgnore
	public void setLayerLocked(int index, boolean active) {
		getLayerList().get(getLayerIndexOrderList().get(index)).setLocked(active);
		// fireActiveLayerChanged(index);
	}

	@JsonIgnore
	public void resetActiveLayer() {
		int size = getActiveLayer().getContent().length;
		// getActiveLayer().re
	}

	@JsonIgnore
	public Layer getActiveLayer() {
		return getLayerList().stream().filter(x -> x.isActive()).findFirst().orElse(null);
	}

	@JsonIgnore
	public void setActiveLayerColorIndex(int index, int colorIndex, boolean select) {
		Layer layer = getActiveLayer();
		layer.getColorPalette().set(index, colorIndex);
		if (select) {
			getActiveLayer().setSelectedColorIndex(index);
		}
		// fireActiveLayerChanged(-1);
	}

	@JsonIgnore
	public int getColorIndex(int colorIndex) {
		return getActiveLayer().getColorPalette().get(colorIndex);
	}

	@JsonIgnore
	public void setOrigin(Point origin) {
		setOriginX(origin.x);
		setOriginY(origin.y);
	}

	@JsonIgnore
	public Point getOrigin() {
		return new Point(getOriginX(), getOriginY());
	}
}
