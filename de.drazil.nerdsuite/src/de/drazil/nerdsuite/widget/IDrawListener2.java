package de.drazil.nerdsuite.widget;

import de.drazil.nerdsuite.constants.PencilMode;

public interface IDrawListener2 {

	public void doDrawPixel(BaseImagingWidget2 source, int x, int y, PencilMode pencilMode);

	public void doDrawTile();

	public void doDrawAllTiles();

	public void setSelectedTile(Tile tile);

}
