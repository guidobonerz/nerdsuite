package de.drazil.nerdsuite.widget;

import org.eclipse.swt.graphics.Color;

import de.drazil.nerdsuite.constants.PixelConfig;

public interface IColorProvider {
	public Color getColorByIndex(byte bitmapByte, byte bitmap[], int x, int y, int columns);

	public PixelConfig getPixelConfig();

	public int getBackgroundColorIndex(byte[] bitmap);
}
