package de.drazil.nerdsuite.imaging.service;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;

import de.drazil.nerdsuite.constants.PencilMode;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;
import de.drazil.nerdsuite.widget.Layer;
import de.drazil.nerdsuite.widget.Tile;

public class PaintTileService extends AbstractImagingService {

	private TileService tileService = ServiceFactory.getService("REPOSITORY", TileService.class);

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
			System.out.println(x+":"+y);
			layer.getContent()[y * conf.tileWidth + x] = (conf.pencilMode == PencilMode.Draw)
					? layer.getSelectedColorIndex()
					: 0;
		}
	}

	public void paintTile(GC gc, Tile tile, ImagingWidgetConfiguration conf) {
		gc.drawImage(tileService.getImagePainterFactory().getImage(tile, true, conf), 0, 0);
	}

	public void paintTileAt(GC gc, int x, int y, ImagingWidgetConfiguration conf) {
		Tile tile = tileService.getTile(x);
		gc.drawImage(tileService.getImagePainterFactory().getImage(tile, true, conf), 0, 0);
	}

	public void paintAllTiles(GC gc, ImagingWidgetConfiguration conf) {
		int x = 0;
		int y = 0;
		for (int i = 0; i < tileService.getSize(); i++) {
			Tile tile = tileService.getTile(i);
			Image image = tileService.getImagePainterFactory().getImage(tile, false, conf);
			// image = new
			// Image(Display.getDefault(),image.getImageData().scaledTo(conf.getFullWidthPixel()
			// / 2, conf.getFullHeightPixel() / 2));
			gc.drawImage(image, x, y);
			x += image.getBounds().width;
		}
	}

}
