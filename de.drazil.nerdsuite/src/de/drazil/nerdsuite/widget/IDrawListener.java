package de.drazil.nerdsuite.widget;

import de.drazil.nerdsuite.widget.ImagingWidget.PencilMode;

public interface IDrawListener {

	public void doDrawPixel(int x, int y, PencilMode pencilMode);

	public void doDrawTile();

	public void doDrawAllTiles();

	public void setSelectedTileOffset(int offset);

}
