package de.drazil.nerdsuite.widget;

import org.eclipse.swt.graphics.Color;

public interface IColorPaletteProvider {
	public Color getColor(Tile tile, int x, int y);

	public Color getColorByIndex(int index);

}
