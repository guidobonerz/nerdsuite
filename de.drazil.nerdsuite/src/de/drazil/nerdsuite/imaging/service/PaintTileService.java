package de.drazil.nerdsuite.imaging.service;

import org.eclipse.swt.graphics.GC;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.constants.PencilMode;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;
import de.drazil.nerdsuite.widget.Layer;
import de.drazil.nerdsuite.widget.Tile;

public class PaintTileService extends AbstractImagingService {

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
		if (x < conf.tileWidth && y < conf.tileHeight) {
			layer.getContent()[y * conf.tileWidth + x] = (conf.pencilMode == PencilMode.Draw)
					? layer.getSelectedColorIndex()
					: 0;
		}
	}

	public void paintTile(GC gc, Tile tile, ImagingWidgetConfiguration conf) {
		int width = conf.tileWidth;
		tile.getLayerIndexOrderList().forEach(index -> {
			Layer layer = tile.getLayer(index);
			int content[] = layer.getContent();
			int x = 0;
			int y = 0;
			for (int i = 0; i < content.length; i++) {
				if (i % width == 0 && i > 0) {
					x = 0;
					y++;
				}
				gc.setBackground(layer.getColor(content[i]));
				gc.fillRectangle(x * conf.pixelSize, y * conf.pixelSize, conf.pixelSize, conf.pixelSize);
				x++;
			}
		});
	}
}
