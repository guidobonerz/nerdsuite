package de.drazil.nerdsuite.widget;

import de.drazil.nerdsuite.enums.CursorMode;
import de.drazil.nerdsuite.enums.GridType;
import de.drazil.nerdsuite.enums.PaintMode;
import de.drazil.nerdsuite.enums.PencilMode;
import de.drazil.nerdsuite.enums.PixelConfig;
import de.drazil.nerdsuite.enums.TileSelectionModes;
import lombok.Data;

@Data
public class ImagingWidgetConfiguration implements TileSelectionModes {

	public String owner;
	public int tileGap = 0;
	public int iconSize;
	public int tileSize;
	public int columns=1;
	public int rows=1;
	public int width;
	public int height;
	public int tileRows;
	public int tileColumns;
	public int tileHeight;
	public int tileWidth;
	public int tileWidthPixel;
	public int tileHeightPixel;
	public int fullWidthPixel;
	public int fullHeightPixel;
	public int pixelWidth = 1;
	public int pixelHeight = 1;
	public int cursorLineWidth = 1;
	public int tileSelectionModes = NONE;
	public boolean pixelGridEnabled = true;
	public boolean tileGridEnabled = true;
	public boolean tileSubGridEnabled = true;
	public boolean tileCursorEnabled = false;
	public boolean separatorEnabled = true;
	public boolean layerViewEnabled = false;
	public PixelConfig pixelConfig = PixelConfig.BC1;
	public PaintMode paintMode = PaintMode.Single;
	public PencilMode pencilMode = PencilMode.Draw;
	public GridType gridStyle = GridType.Line;
	public CursorMode cursorMode = CursorMode.Point;
	public boolean televisionModeEnabled = false;

	public void setPixelSize(int size) {
		pixelWidth = size;
		pixelHeight = size;
	}

	public void computeDimensions() {
		iconSize = width * height;
		tileSize = iconSize * columns * rows;
		tileWidth = width * tileColumns;
		tileHeight = height * tileRows;
		tileWidthPixel = tileWidth * pixelWidth;
		tileHeightPixel = tileHeight * pixelHeight;
		fullWidthPixel = tileWidthPixel * columns + (columns * tileGap) - tileGap;
		fullHeightPixel = tileWidthPixel * rows + (rows * tileGap) - tileGap;
	}
}
