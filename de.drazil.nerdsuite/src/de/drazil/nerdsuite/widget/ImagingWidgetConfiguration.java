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
	public static final int AUTOMATIC = -1;
	public String owner;
	public int tileGap = 0;
	public int storageSize = 0;
	public int iconSize;
	public int tileSize;
	public int columns = 1;
	public int rows = 1;
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
	public int pixelSize = 1;
	public int pixelPaintWidth = 1;
	public int pixelPaintHeight = 1;
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

	public void computeDimensions(int tileCount) {
		pixelPaintWidth = pixelSize;
		pixelPaintHeight = pixelSize;
		iconSize = width * height;
		tileSize = iconSize * tileColumns * tileRows;
		tileWidth = width * tileColumns;
		tileHeight = height * tileRows;
		tileWidthPixel = tileWidth * pixelPaintWidth;
		tileHeightPixel = tileHeight * pixelPaintHeight;
		if (rows == AUTOMATIC && columns > 0) {
			fullWidthPixel = tileWidthPixel * columns + (columns * tileGap) - tileGap;
			rows = fullWidthPixel / columns;
			fullHeightPixel = tileHeightPixel * rows + (rows * tileGap) - tileGap;
		} else if (columns == AUTOMATIC && rows > 0) {
			fullHeightPixel = tileHeightPixel * rows + (rows * tileGap) - tileGap;
			columns = fullHeightPixel / rows;
			fullWidthPixel = tileWidthPixel * columns + (columns * tileGap) - tileGap;
		} else {
			fullWidthPixel = tileWidthPixel * columns + (columns * tileGap) - tileGap;
			fullHeightPixel = tileHeightPixel * rows + (rows * tileGap) - tileGap;
		}

	}
}
