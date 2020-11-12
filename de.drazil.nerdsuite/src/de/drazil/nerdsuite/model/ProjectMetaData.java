package de.drazil.nerdsuite.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectMetaData {
	private String id;
	private String platform;
	private String type;
	private String variant;
	private int width;
	private int height;
	private int columns;
	private int rows;
	private int storageEntity;
	private int blankValue;
	@JsonIgnore
	private String referenceRepositoryId;
	@JsonIgnore
	private int defaultPixelSize;
	@JsonIgnore
	private int currentPixelWidth;
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
	@JsonIgnore
	@Getter
	private int tileWidthPixel;
	@JsonIgnore
	@Getter
	private int tileHeightPixel;

	@JsonIgnore
	public void computeSizes() {
		iconSize = width * height;
		tileSize = iconSize * columns * rows;
		tileWidth = width * columns;
		tileHeight = height * rows;
		tileWidthPixel = tileWidth * currentPixelWidth;
		tileHeightPixel = tileHeight * currentPixelHeight;
	}
}
