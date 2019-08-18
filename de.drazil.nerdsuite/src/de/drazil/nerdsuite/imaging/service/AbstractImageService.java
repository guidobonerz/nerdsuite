package de.drazil.nerdsuite.imaging.service;

import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;
import de.drazil.nerdsuite.widget.Layer;
import de.drazil.nerdsuite.widget.Tile;

public abstract class AbstractImageService implements IImageService {

	protected Tile tile;
	protected Layer activeLayer;
	protected ImagingWidgetConfiguration conf;

	@Override
	public void setTile(Tile tile, ImagingWidgetConfiguration conf) {
		this.conf = conf;
		this.tile = tile;
		this.activeLayer = tile.getActiveLayer();
	}

	@Override
	public abstract void setPixel(int x, int y, int colorIndex);

}
