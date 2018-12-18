package de.drazil.nerdsuite.widget;

import java.util.ArrayList;
import java.util.List;

public class Tile {

	private ImagingWidgetConfiguration configuration = null;
	private List<Layer> layerList = null;
	private String name;
	private int index;

	public Tile(ImagingWidgetConfiguration configuration) {
		this.configuration = configuration;
		layerList = new ArrayList<>();
	}

	public void addLayer() {
		layerList.add(new Layer());
	}

	public void remove(int index) {
		layerList.remove(index);
	}

	public void moveToFront(int index) {

	}

	public void moveToBack(int index) {

	}

	public void moveUp(int index) {

	}

	public void moveDown(int index) {

	}
}
