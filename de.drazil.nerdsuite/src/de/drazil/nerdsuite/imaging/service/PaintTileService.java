package de.drazil.nerdsuite.imaging.service;

import org.eclipse.swt.graphics.GC;

import de.drazil.nerdsuite.widget.IColorPaletteProvider;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;
import de.drazil.nerdsuite.widget.Tile;
import lombok.Setter;

public class PaintTileService extends AbstractImagingService {

	@Setter
	private TileRepositoryService tileRepositoryService;
	@Setter
	private ImagePainterFactory imagePainterFactory;

	public void paintTile(GC gc, Tile tile, ImagingWidgetConfiguration conf,
			IColorPaletteProvider colorPaletteProvider) {

		int y = 0;
		int x = 0;
		if (!conf.supportsPainting) {
			y = conf.scaledTileHeight * (tileRepositoryService.getSelectedTileIndex() / conf.getColumns());
			x = conf.scaledTileWidth * (tileRepositoryService.getSelectedTileIndex() % conf.getColumns());
		}
		gc.drawImage(imagePainterFactory.getImage(tile, 0, 0, false, conf, colorPaletteProvider), x, y);
		System.out.println("paint tile");

	}

}
