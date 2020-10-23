package de.drazil.nerdsuite.widget;

import java.util.Map;

import lombok.Data;

@Data
public class LayerModel {
	private int[] content = null;
	private boolean isActive = false;
	private boolean isLocked = false;
	private boolean visible = true;
	private int selectedColorIndex = 1;
	private Map<String, Integer> colorPalette;
}
