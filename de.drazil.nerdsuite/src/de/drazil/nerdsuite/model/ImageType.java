package de.drazil.nerdsuite.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageType {
	private String id;
	private int width;
	private int height;
	private String storage;
	private TileSize tileSize;
}
