package de.drazil.nerdsuite.widget;

import de.drazil.nerdsuite.widget.ImagingWidget.GridStyle;
import de.drazil.nerdsuite.widget.ImagingWidget.WidgetMode;
import lombok.Getter;

@Getter
public class ImagingWidgetConfiguration {
	private int width = 8;
	private int currentWidth = 0;
	private int height = 8;
	private int tileColumns = 1;
	private int tileRows = 1;
	private int columns = 1;
	private int rows = 1;
	private int visibleColumns = 0;
	private int visibleRows = 0;
	private int pixelSize = 15;
	private int currentPixelWidth;
	private int currentPixelHeight;
	private int bytesPerRow;
	private int tileSize;
	private int iconSize;

	private boolean pixelGridEnabled = true;
	private boolean tileGridEnabled = true;
	private boolean tileSubGridEnabled = true;
	private boolean multiColorEnabled = true;
	private boolean tileCursorEnabled = false;
	private boolean separatorEnabled = true;
	private boolean layerViewEnabled = false;

	private GridStyle gridStyle = GridStyle.Line;
	private WidgetMode widgetMode;

}
