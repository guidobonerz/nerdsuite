package de.drazil.nerdsuite.widget;

public interface IDrawListener
{
	public void drawPixel(int x, int y);

	public void drawTile(int x, int y);

	public void drawAll();

	public void setSelectedTileOffset(int offset);
}
