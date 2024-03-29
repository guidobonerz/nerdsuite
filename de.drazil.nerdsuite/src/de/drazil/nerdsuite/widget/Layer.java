package de.drazil.nerdsuite.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import de.drazil.nerdsuite.json.IntArrayToStringConverter;
import de.drazil.nerdsuite.json.StringToIntArrayConverter;
import de.drazil.nerdsuite.model.DirtyableImage;
import lombok.Data;

@Data

public class Layer {
	@JsonProperty(value = "id")
	private String id;
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
	@JsonProperty(value = "deleted")
	private boolean deleted = false;
	@JsonProperty(value = "selectedColorIndex")
	private int selectedColorIndex = 1;
	@JsonProperty(value = "colorPalette")
	private List<Integer> colorPalette = new ArrayList<Integer>();
	@JsonIgnore
	private Map<String, DirtyableImage> imagePool = null;

	@JsonIgnore
	private boolean dirty = true;

	public Layer() {
		imagePool = new HashMap<String, DirtyableImage>();
	}

	public Layer(String id, String name, int size, Integer brushValue) {
		this();
		this.id = id;
		this.name = name;
		reset(size, brushValue);
	}

	@JsonIgnore
	public void putImage(DirtyableImage image) {
		putImage(id, image);
	}

	@JsonIgnore
	public void putImage(String id, DirtyableImage image) {
		imagePool.put(id, image);
	}

	@JsonIgnore
	public DirtyableImage getImage(String id) {
		return imagePool.get(id);
	}

	@JsonIgnore
	public void removeImage() {
		removeImage(id);
	}

	@JsonIgnore
	public void removeImage(String id) {
		imagePool.get(id).dispose();

		imagePool.remove(id);
	}

	@JsonIgnore
	public int getSize() {
		return content.length;
	}

	@JsonIgnore
	public void reset(int size, Integer brushValue) {
		resetContent(size);
		resetBrush(size, brushValue);
	}

	@JsonIgnore
	public void resetBrush(int size, Integer blankValue) {
		if (blankValue != null) {
			brush = new int[size];
			Arrays.fill(brush, blankValue);
		}
	}

	@JsonIgnore
	public void resetContent(int contentLength) {
		content = new int[contentLength];
		Arrays.fill(content, 0);
	}
}
