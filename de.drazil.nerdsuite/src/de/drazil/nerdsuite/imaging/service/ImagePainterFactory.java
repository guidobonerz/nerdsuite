package de.drazil.nerdsuite.imaging.service;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGBA;
import org.eclipse.swt.widgets.Display;

import de.drazil.nerdsuite.Constants;
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
			img.setBackground(Constants.BLACK);
		}
		System.out.println("select tile:" + tile.getName());

		// ImageData id = image.getImageData().scaledTo(10, 10);

		GC gc = new GC(img);
		int width = conf.tileWidth;
		int size = tile.getLayer(0).size();

		int x = 0;
		int y = 0;
		for (int i = 0; i < size; i++) {
			if (i % width == 0 && i > 0) {
				x = 0;
				y++;
			}
			Color c = tile.getBackgroundColor();
			for (int l = 0; l < tile.getLayerList().size(); l++) {
				Layer la = tile.getLayer(l);
				int[] content = la.getContent();
				if (content[i] != 0
						&& (!tile.isShowOnlyActiveLayer() || (tile.isShowOnlyActiveLayer() && la.isActive()))) {

					c = la.getColor(content[i]);
					if (tile.isShowInactiveLayerTranslucent() && !la.isActive()) {
						c = new Color(Display.getCurrent(), c.getRed(), c.getGreen(), c.getBlue(), 100);
					}
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
