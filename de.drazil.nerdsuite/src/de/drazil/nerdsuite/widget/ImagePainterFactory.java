package de.drazil.nerdsuite.widget;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class ImagePainterFactory {

	private Map<String, Image> imagePool = null;

	public ImagePainterFactory() {
		imagePool = new HashMap<>();
	}

	public Image getImage(Tile tile, boolean needsUpdate, ImagingWidgetConfiguration conf) {
		String name = tile.getName();
		Image image = imagePool.get("IMAGE-" + name);
		if (null == image || needsUpdate) {
			image = createOrUpdateImage(tile, conf);
			imagePool.put("IMAGE-" + name, image);
			System.out.println("create new IMAGE-" + name);
		}
		return image;
	}

	public boolean hasImages() {
		return !imagePool.isEmpty();
	}

	public void clear() {
		imagePool.clear();
	}

	private Image createOrUpdateImage(Tile tile, ImagingWidgetConfiguration conf) {
		Image image = new Image(Display.getDefault(), conf.fullWidthPixel,conf.fullHeightPixel);
		// ImageData id = image.getImageData().scaledTo(10, 10);

		GC gc = new GC(image);
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
		gc.dispose();
		return image;
	}

}
