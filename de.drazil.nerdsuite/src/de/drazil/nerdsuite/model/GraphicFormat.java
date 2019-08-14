package de.drazil.nerdsuite.model;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class GraphicFormat {
	private String id;
	private int width;
	private int height;
	private int storageEntity;
	private String storageType;
	private TileSize tileSize;
	private Map<String, Object> properties;
	private List<GraphicFormatVariant> variants;
}
