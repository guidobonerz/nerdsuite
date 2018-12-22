package de.drazil.nerdsuite.model;

import lombok.Data;

@Data
public class GraphicMetadata {
	private int width;
	private int height;
	private int rows;
	private int columns;
	private boolean multicolor;
}
