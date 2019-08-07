package de.drazil.nerdsuite.model;

import lombok.Data;

@Data
public class GraphicMetadata {
	private int width;
	private int height;
	private int tileRows;
	private int tileColumns;
	private boolean multicolor;

	public int getContentSize() {
		return width * height * tileColumns * tileRows;
	}
}
