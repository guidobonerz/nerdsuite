package de.drazil.nerdsuite.model;

import lombok.Data;

@Data
public class GraphicContentStructure {
	private int dataOffset;
	private int bitmapOffset;
	private int screenRamOffset;
	private int colorRamOffset;
	private int backgroundColorOffset;
}
