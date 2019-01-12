package de.drazil.nerdsuite.imaging.service;

import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;
import de.drazil.nerdsuite.widget.Tile;

public class PaintService extends AbstractImagingService {

	public void setPixel(Tile tile, int x, int y, int value, ImagingWidgetConfiguration conf) {
		int layerContent[] = tile.getActiveLayer().getContent();

		switch (conf.paintMode) {
		case Simple: {
			setPixel(layerContent, x, y, value, conf);
			break;
		}
		case VerticalMirror: {
			setPixel(layerContent, x, y, value, conf);
			int centerX = ((conf.width * conf.tileColumns) / 2);
			int diff = centerX - x - 1;
			setPixel(layerContent, centerX + diff, y, value, conf);
			break;
		}
		case HorizontalMirror: {
			setPixel(layerContent, x, y, value, conf);
			int centerY = ((conf.height * conf.tileRows) / 2);
			int diff = centerY - y - 1;
			setPixel(layerContent, x, centerY + diff, value, conf);
			break;
		}
		case Kaleidoscope: {
			setPixel(layerContent, x, y, value, conf);
			int centerX = ((conf.width * conf.tileColumns) / 2);
			int diffX = centerX - x - 1;
			setPixel(layerContent, centerX + diffX, y, value, conf);
			int centerY = ((conf.height * conf.tileRows) / 2);
			int diffY = centerY - y - 1;
			setPixel(layerContent, x, centerY + diffY, value, conf);
			setPixel(layerContent, centerX + diffX, centerY + diffY, value, conf);
			break;
		}
		}
	}

	private void setPixel(int layerContent[], int x, int y, int value, ImagingWidgetConfiguration conf) {
		layerContent[y * conf.tileHeight + x] = value;
	}

	public void paint(Tile tile, ImagingWidgetConfiguration conf) {

	}
}
