package de.drazil.nerdsuite.widget;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Tile {
	private String name = null;
	private boolean showOnlyActiveLayer = true;
	private boolean showInactiveLayerTranslucent = false;
	private boolean multicolor = false;
	private int originX;
	private int originY;
	@JsonProperty(value = "layers")
	private List<Layer> layerList = new ArrayList<Layer>();
	@JsonProperty(value = "layerIndexOrder")
	private List<Integer> layerIndexOrderList = new ArrayList<Integer>();

}
