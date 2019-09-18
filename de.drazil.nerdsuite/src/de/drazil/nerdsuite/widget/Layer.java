package de.drazil.nerdsuite.widget;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import de.drazil.nerdsuite.json.IntArrayToStringConveter;
import de.drazil.nerdsuite.json.StringToIntArrayConverter;
import lombok.Data;

@Data
public class Layer {
	@JsonSerialize(converter = IntArrayToStringConveter.class)
	@JsonDeserialize(converter = StringToIntArrayConverter.class)
	private int[] content = null;
	private boolean isActive = false;
	private boolean isLocked = false;
	private int opacity = 0;
	private boolean visible = true;
	private String name = "<rename me>";
	private Map<String, Color> colorPalette;
	private int selectedColorIndex = 0;

	public Layer(String name, int size) {
		this.name = null == name ? this.name : name;
		content = new int[size];
	}

	public Color getSelectedColor() {
		return getColor(selectedColorIndex);
	}

	public Color getColor(int index) {
		return colorPalette.get(Integer.toString(index));
	}

	public void setColor(int index, Color color) {
		if (colorPalette == null) {
			colorPalette = new HashMap<>();
		}
		colorPalette.put(String.valueOf(index), color);
	}

	public int size() {
		return content.length;
	}
}
