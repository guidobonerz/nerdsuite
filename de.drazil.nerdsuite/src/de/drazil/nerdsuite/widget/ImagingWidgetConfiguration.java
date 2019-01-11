package de.drazil.nerdsuite.widget;

import lombok.Getter;

@Getter
public class ImagingWidgetConfiguration {
	public int width = 8;
	public int currentWidth = 0;
	public int height = 8;
	public int tileColumns = 1;
	public int tileRows = 1;
	public int columns = 1;
	public int rows = 1;
	public int visibleColumns = 0;
	public int visibleRows = 0;
	public int pixelSize = 15;
	public int currentPixelWidth;
	public int currentPixelHeight;
	public int bytesPerRow;
	public int tileSize;
	public int iconSize;
	public int cursorLineWidth = 1;
	public int fullWidth = 0;
	public int fullHeight = 0;
	public int tileWidth = 0;
	public int tileHeight = 0;

	public boolean pixelGridEnabled = true;
	public boolean tileGridEnabled = true;
	public boolean tileSubGridEnabled = true;
	public boolean tileCursorEnabled = false;
	public boolean separatorEnabled = true;
	public boolean layerViewEnabled = false;
	public boolean televisionModeEnabled = false;

	public PixelConfig pixelConfig = PixelConfig.BC1;
	public PaintMode paintMode = PaintMode.Simple;
	public PencilMode pencilMode = PencilMode.Draw;
	public GridStyle gridStyle = GridStyle.Line;

	public IConfigurationListener cl;

	public enum PaintMode {
		Simple, VerticalMirror, HorizontalMirror, Kaleidoscope
	}

	public enum PencilMode {
		Draw, Erase
	}

	public enum GridStyle {
		Dot, Line
	};

	public enum BrushStyle {
		Dot, Pattern
	}

	public enum PixelConfig {
		BC8("MultiColor256", 8, 0, 256, 1, 1), BC2("MultiColor4", 2, 3, 3, 4, 2), BC1("MonoColor", 1, 3, 1, 8, 1);

		public final String name;
		public final int bitCount;
		public final int shift;
		public final int mask;
		public final int mul;
		public final int pixmul;

		PixelConfig(String name, int bitCount, int shift, int mask, int mul, int pixmul) {
			this.name = name;
			this.bitCount = bitCount;
			this.shift = shift;
			this.mask = mask;
			this.mul = mul;
			this.pixmul = pixmul;
		}
	}

	public String widgetName = "<unknown>";

	public void setConfigurationListener(IConfigurationListener cl) {
		this.cl = cl;
	}

	public void setPixelSize(int pixelSize) {
		this.pixelSize = pixelSize;
		computeSizes();
	}

	public boolean isPixelGridEnabled() {
		return this.pixelGridEnabled;
	}

	public boolean isTileSubGridEnabled() {
		return this.tileSubGridEnabled;
	}

	public void setTileSubGridEnabled(boolean tileSubGridEnabled) {
		this.tileSubGridEnabled = tileSubGridEnabled;
	}

	public boolean isTileGridEnabled() {
		return this.tileGridEnabled;
	}

	public void setTileGridEnabled(boolean tileGridEnabled) {
		this.tileGridEnabled = tileGridEnabled;
	}

	public boolean isSeparatorEnabled() {
		return this.separatorEnabled;
	}

	public boolean isTileCursorEnabled() {
		return this.tileCursorEnabled;
	}

	public void setTileCursorEnabled(boolean tileCursorEnabled) {
		this.tileCursorEnabled = tileCursorEnabled;
	}

	public void setHeight(int height) {
		this.height = height;
		computeSizes();
	}

	public void setWidth(int width) {
		this.width = width;
		computeSizes();
	}

	public void setRows(int rows) {
		this.rows = rows;
		this.visibleRows = rows;
	}

	public void setColumns(int columns) {
		this.columns = columns;
		this.visibleColumns = columns;
	}

	public void setVisibleRows(int rows) {
		this.visibleRows = rows;
	}

	public void setVisibleColumns(int columns) {
		this.visibleColumns = columns;
	}

	public void setTileColumns(int tileColumns) {
		this.tileColumns = tileColumns;
		computeSizes();
	}

	public void setTileRows(int tileRows) {
		this.tileRows = tileRows;
		computeSizes();
	}

	public void setPixelConfig(PixelConfig pixelConfig) {
		this.pixelConfig = pixelConfig;
		computeSizes();
	}

	public void setWidgetName(String widgetName) {
		this.widgetName = widgetName;
	}

	public void setPixelGridEnabled(boolean pixelGridEnabled) {
		this.pixelGridEnabled = pixelGridEnabled;
	}

	public void setGridStyle(GridStyle gridStyle) {
		this.gridStyle = gridStyle;
	}

	public void setLayerViewEnabled(boolean layerViewEnabled) {
		this.layerViewEnabled = layerViewEnabled;
	}

	public void setSeparatorEnabled(boolean separatorEnabled) {
		this.separatorEnabled = separatorEnabled;
	}

	public void setPaintMode(PaintMode drawMode) {
		this.paintMode = drawMode;
	}

	public void setCursorLineWidth(int cursorLineWidth) {
		this.cursorLineWidth = cursorLineWidth;
	}

	public void setTelevisonModeEnabled(boolean televisionModeEnabled) {
		this.televisionModeEnabled = televisionModeEnabled;
	}

	public void computeSizes() {
		currentPixelWidth = pixelSize;
		currentPixelHeight = pixelSize;
		bytesPerRow = width >> pixelConfig.shift;
		iconSize = bytesPerRow * height;
		tileSize = iconSize * tileColumns * tileRows;
		tileWidth = width * tileColumns * pixelSize;
		tileHeight = height * tileRows * pixelSize;
		fullWidth = tileWidth * columns;
		fullHeight = tileHeight * rows;

	}

	public int computeTileOffset(int x, int y, int offset) {
		return tileSize * (x + (y * columns)) + offset;
	}
}
