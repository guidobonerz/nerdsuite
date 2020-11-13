package de.drazil.nerdsuite.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectMetaData {
	private String id;
	private String platform;
	private String type;
	private String variant;
	private Integer width;
	private Integer height;
	private Integer columns;
	private Integer rows;
	private Integer storageEntity;
	private Integer blankValue;
	@JsonIgnore
	private String referenceRepositoryId;
	@JsonIgnore
	private int currentPixelHeight;
	@JsonIgnore
	private int iconSize;
	@JsonIgnore
	private int tileSize;
	@JsonIgnore
	private int tileWidth;
	@JsonIgnore
	private int tileHeight;

}
