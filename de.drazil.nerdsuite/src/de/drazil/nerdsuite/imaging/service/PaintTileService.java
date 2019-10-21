package de.drazil.nerdsuite.imaging.service;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import de.drazil.nerdsuite.enums.PencilMode;
import de.drazil.nerdsuite.widget.IColorPaletteProvider;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;
import de.drazil.nerdsuite.widget.Layer;
import de.drazil.nerdsuite.widget.Tile;
import lombok.Setter;

public class PaintTileService extends AbstractImagingService {

	@Setter
	private TileRepositoryService tileRepositoryService;
	@Setter
	private ImagePainterFactory imagePainterFactory;

	public void setPixel(Tile tile, int x, int y, ImagingWidgetConfiguration conf) {
		Layer layer = tile.getActiveLayer();

		switch (conf.paintMode) {
		case Single: {
			setPixel(layer, x, y, conf);
			break;
		}
		case VerticalMirror: {
			setPixel(layer, x, y, conf);
			int centerX = ((conf.width * conf.tileColumns) / 2);
			int diff = centerX - x - 1;
			setPixel(layer, centerX + diff, y, conf);
			break;
		}
		case HorizontalMirror: {
			setPixel(layer, x, y, conf);
			int centerY = ((conf.height * conf.tileRows) / 2);
			int diff = centerY - y - 1;
			setPixel(layer, x, centerY + diff, conf);
			break;
		}
		case Kaleidoscope: {
			setPixel(layer, x, y, conf);
			int centerX = ((conf.width * conf.tileColumns) / 2);
			int diffX = centerX - x - 1;
			setPixel(layer, centerX + diffX, y, conf);
			int centerY = ((conf.height * conf.tileRows) / 2);
			int diffY = centerY - y - 1;
			setPixel(layer, x, centerY + diffY, conf);
			setPixel(layer, centerX + diffX, centerY + diffY, conf);
			break;
		}
		}
	}

	private void setPixel(Layer layer, int x, int y, ImagingWidgetConfiguration conf) {
		if (x >= 0 && y >= 0 && x < conf.tileWidth && y < conf.tileHeight) {
			layer.getContent()[y * conf.tileWidth + x] = (conf.pencilMode == PencilMode.Draw)
					? layer.getSelectedColorIndex()
					: 0;
		}
	}

	public void paintPixel(GC gc, Tile tile, int x, int y, ImagingWidgetConfiguration conf,
			IColorPaletteProvider colorPaletteProvider) {
		gc.drawImage(imagePainterFactory.getImage(tile, x, y, true, conf, colorPaletteProvider), 0, 0);
	}

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

	public void paintAllTiles(Composite parent, GC gc, ImagingWidgetConfiguration conf,
			IColorPaletteProvider colorPaletteProvider) {
		int x = 0;
		int y = 0;
		int parentWidth = parent.getBounds().width;
		for (int i = 0; i < tileRepositoryService.getSize(); i++) {
			Tile tile = tileRepositoryService.getTile(i);

			Image image = imagePainterFactory.getImage(tile, 0, 0, false, conf, colorPaletteProvider);
			int imageWidth = image.getBounds().width;
			int imageHeight = image.getBounds().height;
			gc.drawImage(image, x, y);
			x += imageWidth;
			int columns = (int) (parentWidth / imageWidth);
			conf.setColumns(columns);
			if ((i + 1) % columns == 0) {
				y += imageHeight;
				x = 0;
			}
		}
	}
}
