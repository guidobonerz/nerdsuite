package de.drazil.nerdsuite.widget;

import java.util.ArrayList;
import java.util.List;

public class Workspace {

	private List<Layer> layerList = null;

	public Workspace() {
		layerList = new ArrayList<>();
	}

	public void addLayer(Layer layer) {
		layerList.add(layer);
	}

	public void remove(Layer layer) {
		layerList.remove(layer);
	}

	public void remove(int index) {
		layerList.remove(index);
	}  
}
