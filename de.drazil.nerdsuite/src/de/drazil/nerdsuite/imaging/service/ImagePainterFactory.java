package de.drazil.nerdsuite.imaging.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;
import de.drazil.nerdsuite.widget.Layer;
import de.drazil.nerdsuite.widget.Tile;

public class ImagePainterFactory {

	private Map<String, Image> imagePool = null;

	public ImagePainterFactory() {
		imagePool = new HashMap<>();
	}

	public Image getImage(Tile tile, ImagingWidgetConfiguration conf) {
		String name = tile.getName();
		Image image = imagePool.get("IMAGE-" + name);
		if (null == image) {
			image = createOrUpdateImage(tile, conf, null);
			imagePool.put("IMAGE-" + name, image);
			System.out.println("create new IMAGE-" + name);
		} else {
			image = createOrUpdateImage(tile, conf, image);
		}
		return image;
	}

	public boolean hasImages() {
		return !imagePool.isEmpty();
	}

	public void clear() {
		imagePool.clear();
	}

	private Image createOrUpdateImage(Tile tile, ImagingWidgetConfiguration conf, Image image) {

		Image img = image;
		if (img == null) {
			img = new Image(Display.getDefault(), conf.fullWidthPixel, conf.fullHeightPixel);
		}
		// ImageData id = image.getImageData().scaledTo(10, 10);

		GC gc = new GC(img);
		gc.setAlpha(255);
		int width = conf.tileWidth;
		int size = tile.getLayer(0).size();
		int x = 0;
		int y = 0;
		List<Layer> layerList = tile.getLayerList();
		for (int i = 0; i < size; i++) {
			if (i % width == 0 && i > 0) {
				x = 0;
				y++;
			}
			Color c = tile.getBackgroundColor();

			for (Layer l : layerList) {
				int[] content = l.getContent();
				if (content[i] != 0 && (!tile.isShowOnlyActiveLayer() || (tile.isShowOnlyActiveLayer() && l.isActive())
						|| tile.isShowInactiveLayerTranslucent())) {
					c = l.getColor(content[i]);
					gc.setAlpha(tile.isShowInactiveLayerTranslucent() && !l.isActive() ? 50 : 255);
				}
				gc.setBackground(c);
				gc.fillRectangle(x * conf.pixelSize, y * conf.pixelSize, conf.pixelSize, conf.pixelSize);
			}
			x++;
		}
		gc.dispose();
		return img;
	}
}
