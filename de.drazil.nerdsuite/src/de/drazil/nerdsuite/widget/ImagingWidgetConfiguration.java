package de.drazil.nerdsuite.widget;

import de.drazil.nerdsuite.constants.GridType;
import de.drazil.nerdsuite.constants.PaintMode;
import de.drazil.nerdsuite.constants.PencilMode;
import de.drazil.nerdsuite.constants.PixelConfig;
import de.drazil.nerdsuite.model.GraphicFormat;
import de.drazil.nerdsuite.model.GraphicFormatVariant;
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
	public int tileSize;
	public int iconSize;
	public int cursorLineWidth = 1;
	public int fullWidthPixel = 0;
	public int fullHeightPixel = 0;
	public int tileWidthPixel = 0;
	public int tileHeightPixel = 0;
	public int tileWidth = 0;
	public int tileHeight = 0;

	public boolean pixelGridEnabled = true;
	public boolean tileGridEnabled = true;
	public boolean tileSubGridEnabled = true;
	public boolean tileCursorEnabled = false;
	public boolean separatorEnabled = true;
	public boolean layerViewEnabled = false;
	public boolean televisionModeEnabled = false;
	public boolean supportsPainting = false;
	public boolean supportsMultiTileView = false;
	public boolean supportsSingleTileView = false;
	public boolean supportsSingleSelection = false;
	public boolean supportsMultiSelection = false;
	public boolean supportsReferenceIndexSelection = false;
	public boolean supportsDrawCursor = false;

	public PixelConfig pixelConfig = PixelConfig.BC1;
	public PaintMode paintMode = PaintMode.Single;
	public PencilMode pencilMode = PencilMode.Draw;
	public GridType gridStyle = GridType.Line;
	public GraphicFormat gfxFormat;
	public GraphicFormatVariant gfxFormatVariant;

	public String widgetName = "<unknown>";
	public String serviceOwnerId;

	public void setGraphicFormat(GraphicFormat gfxFormat, int variantIndex) {
		this.gfxFormat = gfxFormat;
		this.gfxFormatVariant = gfxFormat.getVariants().get(variantIndex);
		setPixelSize(gfxFormat.getPixelSize());
		setWidth(gfxFormat.getWidth());
		setHeight(gfxFormat.getHeight());
		setTileRows(gfxFormatVariant.getTileRows());
		setTileColumns(gfxFormatVariant.getTileColumns());
		if (gfxFormat.getId().contains("BITMAP")) {
			setPixelGridEnabled(false);
		}
	}

	public void setServiceOwner(String serviceOwnerId) {
		this.serviceOwnerId = serviceOwnerId;
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

	public void setGridStyle(GridType gridStyle) {
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

	protected boolean supportsPainting() {
		return supportsPainting;
	}

	protected boolean supportsMultiTileView() {
		return supportsMultiTileView;
	}

	protected boolean supportsSingleSelection() {
		return supportsSingleSelection;
	}

	protected boolean supportsMultiSelection() {
		return supportsMultiSelection;
	}

	protected boolean supportsReferenceIndexSelection() {
		return supportsReferenceIndexSelection;
	}

	protected boolean supportsDrawCursor() {
		return supportsDrawCursor;
	}

	public void computeSizes() {
		currentPixelWidth = pixelSize;
		currentPixelHeight = pixelSize;
		iconSize = width * height;
		tileSize = iconSize * tileColumns * tileRows;
		tileWidth = width * tileColumns;
		tileHeight = height * tileRows;
		tileWidthPixel = tileWidth * pixelSize;
		tileHeightPixel = tileHeight * pixelSize;
		fullWidthPixel = tileWidthPixel * columns;
		fullHeightPixel = tileHeightPixel * rows;
	}
}
