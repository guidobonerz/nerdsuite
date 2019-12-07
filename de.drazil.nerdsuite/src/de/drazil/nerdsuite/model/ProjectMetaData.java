package de.drazil.nerdsuite.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectMetaData {
	private String targetPlatform;
	private String id;
	private String type;
	private String variant;
	private CustomSize customSize;
	private String referenceRepositoryName;
}
