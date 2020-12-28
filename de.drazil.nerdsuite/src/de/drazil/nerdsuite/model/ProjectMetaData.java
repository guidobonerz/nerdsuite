package de.drazil.nerdsuite.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
	private Integer blankValue;
	private String referenceId;
	private boolean multicolor;

	public void init(int width, int height, int columns, int rows, int storageSize, boolean multicolor) {
		this.multicolor = multicolor;
		this.width = width;
		this.height = height;
		this.columns = columns;
		this.rows = rows;
		this.storageEntity = storageSize;

	}
}
