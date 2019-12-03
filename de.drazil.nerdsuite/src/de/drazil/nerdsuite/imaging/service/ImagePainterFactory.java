package de.drazil.nerdsuite.imaging.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.enums.ScaleMode;
import de.drazil.nerdsuite.widget.IColorPaletteProvider;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;
import de.drazil.nerdsuite.widget.Layer;
import de.drazil.nerdsuite.widget.Tile;

public class ImagePainterFactory {

	private Map<String, Image> imagePool = null;

	public final static int NONE = 0;
	public final static int READ = 1;
	public final static int UPDATE = 2;
	public final static int UPDATE_PIXEL = 8;

	public ImagePainterFactory() {
		imagePool = new HashMap<>();
	}

	public Image getImage(Tile tile, int x, int y, int update, ImagingWidgetConfiguration conf,
			IColorPaletteProvider colorPaletteProvider) {
		String name = tile.getName();
		Image scaledImage = null;
		Image mainImage = imagePool.get(name);
		if (null == mainImage || checkMode(update, UPDATE)) {
			if (mainImage != null && checkMode(update, UPDATE)) {
				mainImage.dispose();
			}
			System.out.println("new image");
			mainImage = new Image(Display.getDefault(), conf.tileWidthPixel, conf.tileHeightPixel);
			mainImage.setBackground(Constants.BLACK);
			imagePool.put(name, mainImage);
		}
		if (checkMode(update, UPDATE) || checkMode(update, UPDATE_PIXEL)) {
			System.out.println("pixel");
			mainImage = updateImage(tile, x, y, update, conf, mainImage, name, colorPaletteProvider);
		}

		System.out.println("update:" + update);

		ScaleMode scaleMode = conf.getScaleMode();
		if (conf.getScaleMode() != ScaleMode.None) {
			String sm = name + "_" + conf.getScaleMode().name();
			scaledImage = imagePool.get(sm);
			if (null == scaledImage || checkMode(update, UPDATE)) {
				if (scaledImage != null && checkMode(update, UPDATE)) {
					scaledImage.dispose();
				}
				System.out.println("new scaled image");
				int scaledWidth = scaleMode.getDirection() ? conf.fullWidthPixel << scaleMode.getScaleFactor()
						: conf.fullWidthPixel >> scaleMode.getScaleFactor();
				int scaledHeight = scaleMode.getDirection() ? conf.fullHeightPixel << scaleMode.getScaleFactor()
						: conf.fullHeightPixel >> scaleMode.getScaleFactor();
				scaledImage = new Image(Display.getDefault(),
						mainImage.getImageData().scaledTo(scaledWidth, scaledHeight));
				imagePool.put(sm, scaledImage);

			}
			mainImage = scaledImage;
		}
		conf.setScaledTileWidth(mainImage.getBounds().width);
		conf.setScaledTileHeight(mainImage.getBounds().height);
		return mainImage;
	}

	private boolean checkMode(int update, int value) {
		return (update & value) == value;
	}

	public boolean hasImages() {
		return !imagePool.isEmpty();
	}

	public void clear() {
		imagePool.clear();
	}

	private Image updateImage(Tile tile, int px, int py, int update, ImagingWidgetConfiguration conf, Image image,
			String imageName, IColorPaletteProvider colorPaletteProvider) {
		GC gc = new GC(image);
		gc.setAlpha(255);
		int width = conf.tileWidth;
		int size = tile.getLayer(0).size();
		int x = 0;
		int y = 0;
		List<Layer> layerList = tile.getLayerList();
		if (checkMode(update, UPDATE_PIXEL)) {
			int offset = py * width + px;
			if (offset < size) {
				draw(gc, offset, layerList, tile, conf, px, py, colorPaletteProvider);
			}
		} else if (checkMode(update, UPDATE)) {
			for (int i = 0; i < size; i++) {
				if (i % width == 0 && i > 0) {
					x = 0;
					y++;
				}
				draw(gc, i, layerList, tile, conf, x, y, colorPaletteProvider);
				x++;
			}
		} else {

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
