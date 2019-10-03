package de.drazil.nerdsuite.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomSize {
	private int width;
	private int height;
	private int tileColumns;
	private int tileRows;
	private int storageEntity;
}
