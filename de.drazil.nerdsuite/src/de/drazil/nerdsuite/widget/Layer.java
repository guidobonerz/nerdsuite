package de.drazil.nerdsuite.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import de.drazil.nerdsuite.json.IntArrayToStringConverter;
import de.drazil.nerdsuite.json.StringToIntArrayConverter;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Layer {
	private String name;
	@JsonSerialize(converter = IntArrayToStringConverter.class)
	@JsonDeserialize(converter = StringToIntArrayConverter.class)
	private int[] content = null;
	@JsonSerialize(converter = IntArrayToStringConverter.class)
	@JsonDeserialize(converter = StringToIntArrayConverter.class)
	private int[] brush = null;
	private boolean isActive = false;
	private boolean isLocked = false;
	private boolean visible = true;
	private int selectedColorIndex = 1;
	private List<Integer> colorPalette = new ArrayList<Integer>();

	@JsonIgnore
	private int size;

	public Layer(String name, int size) {
		this.name = name;
		this.size = size;
		initAll();
	}

	@JsonIgnore
	public void initAll() {
		this.content = new int[size];
		this.brush = new int[size];
	}

	@JsonIgnore
	public void initBrush(int blankValue) {
		this.brush = new int[content.length];
		Arrays.fill(brush, blankValue);
	}
}
