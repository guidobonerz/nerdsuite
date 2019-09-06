package de.drazil.nerdsuite.widget;

import org.eclipse.swt.graphics.Color;

public interface IColorRenderer {
	public Color getColor(Tile tile, int x, int y);

	public int getBackgroundColorIndex(Tile tile);
}
