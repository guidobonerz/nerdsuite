package de.drazil.nerdsuite.widget;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import de.drazil.nerdsuite.Constants;

public class ImagePainterFactory {

	private Map<String, Image> imagePool = null;

	public ImagePainterFactory() {
		imagePool = new HashMap<>();
	}

	public Image getImage(Tile tile, boolean needsUpdate, ImagingWidgetConfiguration conf) {
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

		// ImageData id = image.getImageData().scaledTo(10, 10);

		GC gc = new GC(img);
		int width = conf.tileWidth;
		tile.getLayerIndexOrderList().forEach(index -> {
			Layer layer = tile.getLayer(index);
			if (!tile.isShowOnlyActiveLayer() || (tile.isShowOnlyActiveLayer() && layer.isActive())) {
				int content[] = layer.getContent();
				int x = 0;
				int y = 0;
				for (int i = 0; i < content.length; i++) {
					if (i % width == 0 && i > 0) {
						x = 0;
						y++;
					}
					Color c = layer.getColor(content[i]);
					if (content[i] != tile.getBackgroundColorIndex() || tile.isShowOnlyActiveLayer()) {
						gc.setBackground(c);
						gc.fillRectangle(x * conf.pixelSize, y * conf.pixelSize, conf.pixelSize, conf.pixelSize);
					}
					x++;
				}
			}
		});
		gc.dispose();
		return img;
	}

}
