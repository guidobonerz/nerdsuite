package de.drazil.nerdsuite.imaging.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import de.drazil.nerdsuite.enums.ScaleMode;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;
import de.drazil.nerdsuite.widget.Layer;
import de.drazil.nerdsuite.widget.Tile;

public class ImagePainterFactory {

	private Map<String, Image> imagePool = null;
	private Map<String, GC> gcCache = null;
	private GC gc;

	public ImagePainterFactory() {
		imagePool = new HashMap<>();
		gcCache = new HashMap<>();
	}

	public Image getImage(Tile tile, int x, int y, boolean pixelOnly, ImagingWidgetConfiguration conf) {

		String name = tile.getName();
		Image image = imagePool.get(name);
		if (null == image) {
			image = createOrUpdateImage(tile, x, y, pixelOnly, conf, null, name);
			imagePool.put(name, image);
			System.out.println("create new image" + name);
		} else {
			image = createOrUpdateImage(tile, x, y, pixelOnly, conf, image, name);
		}

		ScaleMode scaleMode = conf.getScaleMode();
		if (conf.getScaleMode() != ScaleMode.None) {

			int scaledWidth = scaleMode.getDirection() ? conf.fullWidthPixel << scaleMode.getScaleFactor()
					: conf.fullWidthPixel >> scaleMode.getScaleFactor();
			int scaledHeight = scaleMode.getDirection() ? conf.fullHeightPixel << scaleMode.getScaleFactor()
					: conf.fullHeightPixel >> scaleMode.getScaleFactor();
			System.out.printf("%s fw:%2d fh:%2d sw:%2d sh:%2d\n", conf.widgetName, conf.fullWidthPixel,
					conf.fullHeightPixel, scaledWidth, scaledHeight);
			image = new Image(Display.getDefault(), image.getImageData().scaledTo(scaledWidth, scaledHeight));
		}
		conf.setScaledTileWidth(image.getBounds().width);
		conf.setScaledTileHeight(image.getBounds().height);
		return image;
	}

	public boolean hasImages() {
		return !imagePool.isEmpty();
	}

	public void clear() {
		imagePool.clear();
	}

	private Image createOrUpdateImage(Tile tile, int px, int py, boolean pixelOnly, ImagingWidgetConfiguration conf,
			Image image, String imageName) {

		Image img = image;
		if (img == null) {
			img = new Image(Display.getDefault(), conf.tileWidthPixel, conf.tileHeightPixel);
		}
		gc = gcCache.get(imageName);
		if (gc == null) {
			gc = new GC(img);
			gcCache.put(imageName, gc);
		}

		gc.setAlpha(255);
		int width = conf.tileWidth;
		int size = tile.getLayer(0).size();
		int x = 0;
		int y = 0;
		List<Layer> layerList = tile.getLayerList();
		if (pixelOnly) {
			Color c = tile.getBackgroundColor();
			int offset = py * width + px;
			if (offset < size) {
				// System.out.println("pixel only:" + px + " y:" + py + " offset:" + offset);
				draw(gc, c, offset, layerList, tile, conf, px, py);
			}
		} else {
			for (int i = 0; i < size; i++) {
				if (i % width == 0 && i > 0) {
					x = 0;
					y++;
				}
				Color c = tile.getBackgroundColor();
				draw(gc, c, i, layerList, tile, conf, x, y);
				x++;
			}
		}
		// gc.dispose();
		return img;
	}

	private void draw(GC gc, Color color, int offset, List<Layer> layerList, Tile tile, ImagingWidgetConfiguration conf,
			int x, int y) {
		for (Layer l : layerList) {
			int[] content = l.getContent();
			if (content[offset] != 0 && (!tile.isShowOnlyActiveLayer() || (tile.isShowOnlyActiveLayer() && l.isActive())
					|| tile.isShowInactiveLayerTranslucent())) {
				color = l.getColor(content[offset]);
				gc.setAlpha(tile.isShowInactiveLayerTranslucent() && !l.isActive() ? 50 : 255);
			}
			gc.setBackground(color);
			gc.fillRectangle(x * conf.pixelSize, y * conf.pixelSize, conf.pixelSize, conf.pixelSize);
		}
	}
}
