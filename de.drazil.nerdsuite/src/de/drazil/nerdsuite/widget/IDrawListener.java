package de.drazil.nerdsuite.widget;

public interface IDrawListener {

	public void doDrawPixel(int x, int y, boolean paintMode);

	public void doDrawTile();

	public void doDrawAllTiles();

	public void setSelectedTileOffset(int offset);

}
