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

	public boolean pixelGridEnabled = true;
	public boolean tileGridEnabled = true;
	public boolean tileSubGridEnabled = true;
	public boolean multiColorEnabled = true;
	public boolean tileCursorEnabled = false;
	public boolean separatorEnabled = true;
	public boolean layerViewEnabled = false;

	public PaintMode paintMode = PaintMode.Simple;
	public PencilMode pencilMode = PencilMode.Draw;
	public GridStyle gridStyle = GridStyle.Line;
	public WidgetMode widgetMode;

	public enum WidgetMode {
		Selector, Painter, Viewer, BitmapViewer
	};

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

	public enum PixelBits {
		OneBit("OneBit", 1), TwoBit("TwoBit", 2), Byte("Byte", 8);
		private final String name;
		private final int bits;

		PixelBits(String name, int bits) {
			this.name = name;
			this.bits = bits;
		}

		public String getName() {
			return name;
		}

		public int getBits() {
			return bits;
		}
	}

	public String widgetName = "<unknown>";

	public void setPixelSize(int pixelSize) {
		this.pixelSize = pixelSize;
		this.currentPixelWidth = pixelSize;
		this.currentPixelHeight = pixelSize;
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

	public boolean isMultiColorEnabled() {
		return this.multiColorEnabled;
	}

	public void setHeight(int height) {
		this.height = height;
		computeSizes();
	}

	public void setWidth(int width) {
		this.width = width;
		bytesPerRow = width >> 3;
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

	public void setWidgetMode(WidgetMode widgetMode) {
		this.widgetMode = widgetMode;
	}

	public void setWidgetName(String widgetName) {
		this.widgetName = widgetName;
	}

	public void setMultiColorEnabled(boolean multiColorEnabled) {
		this.multiColorEnabled = multiColorEnabled;
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

	public void computeSizes() {
		iconSize = bytesPerRow * height;
		tileSize = iconSize * tileColumns * tileRows;
	}

	public int computeTileOffset(int x, int y, int offset) {
		return tileSize * (x + (y * columns)) + offset;
	}
}
