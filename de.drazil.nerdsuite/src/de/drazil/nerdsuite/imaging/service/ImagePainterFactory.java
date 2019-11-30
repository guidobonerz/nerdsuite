package de.drazil.nerdsuite.imaging.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import de.drazil.nerdsuite.enums.ScaleMode;
import de.drazil.nerdsuite.widget.IColorPaletteProvider;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;
import de.drazil.nerdsuite.widget.Layer;
import de.drazil.nerdsuite.widget.Tile;

public class ImagePainterFactory {

	private Map<String, Image> imagePool = null;

	public ImagePainterFactory() {
		imagePool = new HashMap<>();
	}

	public Image getImage(Tile tile, int x, int y, boolean pixelOnly, ImagingWidgetConfiguration conf,
			IColorPaletteProvider colorPaletteProvider, boolean forceUpdate) {
		String name = tile.getName();
		Image scaledImage = null;
		Image mainImage = imagePool.get(name);
		if (null == mainImage || forceUpdate) {
			if (forceUpdate && mainImage != null) {
				mainImage.dispose();
			}
			mainImage = new Image(Display.getDefault(), conf.tileWidthPixel, conf.tileHeightPixel);
			mainImage = updateImage(tile, x, y, pixelOnly, conf, mainImage, name, colorPaletteProvider);
			imagePool.put(name, mainImage);
		}

		ScaleMode scaleMode = conf.getScaleMode();
		//if (conf.getScaleMode() != ScaleMode.None) {
			String sm = name + "_" + conf.getScaleMode().name();
			scaledImage = imagePool.get(sm);
			if (null == scaledImage || forceUpdate) {
				if (forceUpdate && scaledImage != null) {
					scaledImage.dispose();
				}
				// System.out.println("new scaled image");
				int scaledWidth = scaleMode.getDirection() ? conf.fullWidthPixel << scaleMode.getScaleFactor()
						: conf.fullWidthPixel >> scaleMode.getScaleFactor();
				int scaledHeight = scaleMode.getDirection() ? conf.fullHeightPixel << scaleMode.getScaleFactor()
						: conf.fullHeightPixel >> scaleMode.getScaleFactor();
				scaledImage = new Image(Display.getDefault(),
						mainImage.getImageData().scaledTo(scaledWidth, scaledHeight));
				imagePool.put(sm, scaledImage);
			}
			mainImage = scaledImage;
		//}
		conf.setScaledTileWidth(mainImage.getBounds().width);
		conf.setScaledTileHeight(mainImage.getBounds().height);
		return mainImage;
	}

	public boolean hasImages() {
		return !imagePool.isEmpty();
	}

	public void clear() {
		imagePool.clear();
	}

	private Image updateImage(Tile tile, int px, int py, boolean pixelOnly, ImagingWidgetConfiguration conf,
			Image image, String imageName, IColorPaletteProvider colorPaletteProvider) {
		GC gc = new GC(image);
		gc.setAlpha(255);
		int width = conf.tileWidth;
		int size = tile.getLayer(0).size();
		int x = 0;
		int y = 0;
		List<Layer> layerList = tile.getLayerList();
		if (pixelOnly) {
			int offset = py * width + px;
			if (offset < size) {
				// System.out.println("pixel only:" + px + " y:" + py + " offset:" + offset);
				draw(gc, offset, layerList, tile, conf, px, py, colorPaletteProvider);
			}
		} else {
			for (int i = 0; i < size; i++) {
				if (i % width == 0 && i > 0) {
					x = 0;
					y++;
				}
				draw(gc, i, layerList, tile, conf, x, y, colorPaletteProvider);
				x++;
			}
		}
		gc.dispose();
		return image;
	}

	private void draw(GC gc, int offset, List<Layer> layerList, Tile tile, ImagingWidgetConfiguration conf, int x,
			int y, IColorPaletteProvider colorPaletteProvider) {
		for (Layer l : layerList) {
			int[] content = l.getContent();
			if (content[offset] != 0 && (!tile.isShowOnlyActiveLayer() || (tile.isShowOnlyActiveLayer() && l.isActive())
					|| tile.isShowInactiveLayerTranslucent())) {
				gc.setAlpha(tile.isShowInactiveLayerTranslucent() && !l.isActive() ? 50 : 255);
			}
			gc.setBackground(colorPaletteProvider.getColorByIndex(content[offset]));
			gc.fillRectangle(x * conf.pixelSize, y * conf.pixelSize, conf.pixelSize, conf.pixelSize);
		}
	}
}
