package de.drazil.nerdsuite.widget;

import java.util.List;

import lombok.Data;

@Data
public class TileModel {

	private String name = null;
	private boolean showOnlyActiveLayer = false;
	private boolean showInactiveLayerTranslucent = false;
	private boolean multicolor = false;
	private List<Layer> layerList = null;
	private List<Integer> layerIndexOrderList = null;
}
