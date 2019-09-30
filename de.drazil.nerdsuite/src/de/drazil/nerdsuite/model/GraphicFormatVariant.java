package de.drazil.nerdsuite.model;

import java.util.Map;

import lombok.Data;

@Data
public class GraphicFormatVariant {
	private String id;
	private String name;
	private String referenceVariant;
	private String colorRendererClass;
	private Map<String, Object> properties;
	private int tileRows;
	private int tileColumns;
	private int scaledPixelSize;
}
