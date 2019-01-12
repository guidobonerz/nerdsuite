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

	public Image getImage(Display display, int index, boolean needsUpdate, ImagingWidgetConfiguration conf,
			byte bitplane[], IColorProvider colorProvider, Map<String, Color> palette) {
		Image image = imagePool.get("ICON-" + index);
		if (null == image || needsUpdate) {
			image = createOrUpdateImage(display, index, conf, bitplane, colorProvider, palette);
			imagePool.put("ICON-" + index, image);
			System.out.println("create new ICON-" + index);
		}
		return image;
	}

	public boolean hasImages() {
		return !imagePool.isEmpty();
	}

	public void clear() {
		imagePool.clear();
	}

	private Image createOrUpdateImage(Display display, int index, ImagingWidgetConfiguration conf, byte bitplane[],
			IColorProvider colorProvider, Map<String, Color> palette) {
		Image image = new Image(display, 10, 10);
		// ImageData id = image.getImageData().scaledTo(10, 10);

		GC gc = new GC(image);
		paintTile(gc, 0, 0, conf, bitplane, colorProvider, palette);
		// gc.setForeground(display.getSystemColor(SWT.COLOR_GREEN));
		// gc.drawString(String.valueOf(index), 0, 0);
		gc.dispose();
		return image;
	}

	private void paintTile(GC gc, int tileX, int tileY, ImagingWidgetConfiguration conf, byte bitplane[],
			IColorProvider colorProvider, Map<String, Color> palette) {
		int x = 0;
		int y = 0;
		int b1 = conf.bytesPerRow * conf.height;
		int b2 = b1 * conf.tileColumns;
		int bc = conf.pixelConfig.bitCount;
		int byteOffset = 0;
		int pix = conf.isPixelGridEnabled() ? 1 : 0;
		/*
		 * if (supportsMultiTileView()) { byteOffset = conf.computeTileOffset(tileX,
		 * tileY, navigationOffset); } else { byteOffset = selectedTileOffset; }
		 */

		for (int i = byteOffset, k = 0; i < (byteOffset + conf.tileSize); i++, k++) {
			int xi = (k % conf.bytesPerRow) * (8 / bc);
			int xo = (k / b1) % conf.tileColumns;
			x = (xi + (xo * conf.currentWidth) + (tileX * conf.currentWidth * conf.tileColumns));

			int yi = (k / conf.bytesPerRow) % conf.height;
			int yo = (k / b2) % conf.tileRows;
			y = (yi + (yo * conf.height) + (tileY * conf.height * conf.tileRows));

			if (i < bitplane.length) {
				int b = (bitplane[i] & 0xff);
				switch (conf.pixelConfig) {
				case BC1: {
					for (int j = 128; j > 0; j >>= 1) {
						gc.setBackground(
								(b & j) == j ? palette.get(String.valueOf(1)) : Constants.BITMAP_BACKGROUND_COLOR);
						gc.fillRectangle((x * conf.currentPixelWidth) + pix, (y * conf.currentPixelHeight) + pix,
								conf.currentPixelWidth - pix, conf.currentPixelHeight - pix);
						x++;
					}
					break;
				}
				case BC2: {
					for (int j = 6; j >= 0; j -= 2) {
						int bi = b;
						int colorIndex = (bi >> j) & 3;
						Color color = palette != null ? palette.get(String.valueOf(colorIndex)) : null;
						if (colorProvider != null) {
							color = colorProvider.getColorByIndex((byte) colorIndex, bitplane, tileX, tileY,
									conf.columns);
						}
						gc.setBackground(color);
						gc.fillRectangle((x * conf.currentPixelWidth) + pix, (y * conf.currentPixelHeight) + pix,
								conf.currentPixelWidth - pix, conf.currentPixelHeight - pix);
						x++;
					}
					break;
				}

				}
			}
		}
	}
}
