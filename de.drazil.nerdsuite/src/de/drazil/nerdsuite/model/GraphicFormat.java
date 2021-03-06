package de.drazil.nerdsuite.model;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class GraphicFormat {
	private String id;
	private String name;
	private int width;
	private int height;
	private boolean supportsLayers;
	private boolean supportsMulticolor;
	private int storageSize;
	private String storageType;
	private int maxItems;
	private int blankValue;
	private String colorRendererClass;
	private TileSize tileSize;
	private Map<String, Object> properties;
	private List<GraphicFormatVariant> variants;
}
