package de.drazil.nerdsuite.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import de.drazil.nerdsuite.json.IntArrayToStringConverter;
import de.drazil.nerdsuite.json.StringToIntArrayConverter;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Layer {
	@JsonProperty(value = "name")
	private String name;
	@JsonSerialize(converter = IntArrayToStringConverter.class)
	@JsonDeserialize(converter = StringToIntArrayConverter.class)
	@JsonProperty(value = "content")
	private int[] content = null;
	@JsonSerialize(converter = IntArrayToStringConverter.class)
	@JsonDeserialize(converter = StringToIntArrayConverter.class)
	@JsonProperty(value = "brush")
	private int[] brush = null;
	@JsonProperty(value = "active")
	private boolean active = false;
	@JsonProperty(value = "locked")
	private boolean locked = false;
	@JsonProperty(value = "visible")
	private boolean visible = true;
	@JsonProperty(value = "selectedColorIndex")
	private int selectedColorIndex = 1;
	@JsonProperty(value = "colorPalette")
	private List<Integer> colorPalette = new ArrayList<Integer>();

	@JsonIgnore
	private int size;
	@JsonIgnore
	private boolean dirty = true;

	public Layer(String name, int size, int contentValue, int brushValue) {
		this.name = name;
		this.size = size;
		reset(contentValue, brushValue);
	}

	@JsonIgnore
	public void reset(int contentValue, int brushValue) {
		resetBrush(brushValue);
		resetContent(contentValue);
	}

	@JsonIgnore
	public void resetBrush(int blankValue) {
		this.brush = new int[content.length];
		Arrays.fill(brush, blankValue);
	}

	@JsonIgnore
	public void resetContent(int blankValue) {
		this.brush = new int[content.length];
		Arrays.fill(brush, blankValue);
	}
}
