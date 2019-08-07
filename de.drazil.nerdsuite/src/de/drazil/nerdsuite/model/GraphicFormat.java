package de.drazil.nerdsuite.model;

import lombok.Data;

@Data
public class GraphicFormat {
	private String id;
	private String metadataRefId;
	private GraphicContentStructure contentStructure;
	private GraphicMetadata metadata;
}
