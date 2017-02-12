package de.drazil.nerdsuite.widget;

import org.eclipse.swt.graphics.Color;

public interface IColorProvider
{
	public Color getColorByIndex(byte bitmapByte, byte bitmap[], int offset, int colorMapIndex);
}
