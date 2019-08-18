package de.drazil.nerdsuite.imaging.service;

import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;
import de.drazil.nerdsuite.widget.Tile;

public interface IImageService {
	public void setTile(Tile tile, ImagingWidgetConfiguration conf);

	public void setPixel(int x, int y, int colorIndex);
}
