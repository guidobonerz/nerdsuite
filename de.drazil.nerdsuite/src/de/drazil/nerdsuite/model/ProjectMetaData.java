package de.drazil.nerdsuite.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectMetaData {
	private String id;
	private String targetPlatform;
	private String type;
	private String typeName;
	private String variant;
	private String variantName;
	private int width;
	private int height;
	private int tileColumns;
	private int tileRows;
	private int storageEntity;
	private String storageType;
	private String referenceRepositoryName;
}
