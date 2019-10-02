package de.drazil.nerdsuite.widget;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import de.drazil.nerdsuite.json.IntArrayToStringConveter;
import de.drazil.nerdsuite.json.StringToIntArrayConverter;
import lombok.Data;

@Data
public class Layer {

	private String name = "rename me";
	@JsonSerialize(converter = IntArrayToStringConveter.class)
	@JsonDeserialize(converter = StringToIntArrayConverter.class)
	private int[] content = null;
	private boolean isActive = false;
	private boolean isLocked = false;
	private boolean visible = true;
	private int selectedColorIndex = 1;
	private Map<String, Integer> colorPalette;

	@JsonIgnore
	private int opacity = 0;

	public Layer() {

	}

	public Layer(String name, int size) {
		this.name = null == name ? this.name : name;
		content = new int[size];
	}

	public int getSelectedColorIndex() {
		return getColorIndex(selectedColorIndex);
	}

	@JsonIgnore
	public int getColorIndex(int index) {
		return colorPalette.get(Integer.toString(index));
	}

	@JsonIgnore
	public void setColorIndex(int index, int colorIndex) {
		setColorIndex(index, colorIndex, false);
	}

	public void setColorIndex(int index, int colorIndex, boolean select) {
		if (colorPalette == null) {
			colorPalette = new HashMap<>();
		}
		colorPalette.put(String.valueOf(index), colorIndex);
		if (select) {
			selectedColorIndex = index;
		}
	}

	public int size() {
		return content.length;
	}
}
