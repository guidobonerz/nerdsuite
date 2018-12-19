package de.drazil.nerdsuite.widget;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.GC;

import lombok.Setter;

public class Tile {

	private ImagingWidgetConfiguration configuration = null;
	private List<Layer> layerList = null;
	private List<Integer> layerIndexOrderList = null;
	@Setter
	private String name = "rename me";

	public Tile(ImagingWidgetConfiguration configuration) {
		this.configuration = configuration;
		layerList = new ArrayList<>();
	}

	public void addLayer() {
		Layer layer = new Layer();
		layerList.add(layer);
		layerIndexOrderList.add(layerList.indexOf(layer));
	}

	public void removeLayer(int index) {
		Layer layer = layerList.get(index);
		layerList.remove(index);
	}

	public void moveToFront(int index) {
		if (index < 1) {
			return;
		}
		layerIndexOrderList.remove(index);
		layerIndexOrderList.add(0, index);
	}

	public void moveToBack(int index) {
		if (index < 1) {
			return;
		}
		layerIndexOrderList.remove(index);
		layerIndexOrderList.add(index);
	}

	public void moveUp(int index) {
		if (index < 1) {
			return;
		}
		layerIndexOrderList.remove(index);
		layerIndexOrderList.add(index - 1, index);
	}

	public void moveDown(int index) {
		if (index < 1) {
			return;
		}
		layerIndexOrderList.remove(index);
		layerIndexOrderList.add(index + 1, index);
	}

	public void paint(GC gc, int x, int y) {

	}

}
