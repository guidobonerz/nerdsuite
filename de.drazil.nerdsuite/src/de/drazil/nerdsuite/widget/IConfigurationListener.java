package de.drazil.nerdsuite.widget;

public interface IConfigurationListener {
	public void configurationChanged(int width, int height, int tileColumns, int tileRows, int painterPixelSize,
			int selectorPixelSize, int columns, int rows,int currentWidth);
}
