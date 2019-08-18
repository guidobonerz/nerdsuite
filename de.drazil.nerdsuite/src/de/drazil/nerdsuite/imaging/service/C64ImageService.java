package de.drazil.nerdsuite.imaging.service;

import java.awt.Color;

import org.eclipse.swt.graphics.GC;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.constants.PencilMode;

public class C64ImageService extends AbstractImageService {

	public void setPixel(int x, int y) {

		int[] bitplane = activeLayer.getContent();
		int ix = x % conf.currentWidth;
		int iy = y % conf.height;
		int ax = (x / conf.currentWidth);
		int ay = (y / conf.height) * conf.tileColumns;
		int offset = (ax + ay) * (conf.height * conf.bytesPerRow);
		int index = 0;
		switch (conf.pixelConfig) {
		case BC1: {
			index = (((iy * conf.currentWidth) + ix) >> 3) + offset;
			int byteMask = bitplane[index];
			int pixelMask = (1 << (7 - (ix % 8)) & 0xff);
			bitplane[index] = conf.pencilMode == PencilMode.Draw ? (byte) (byteMask | pixelMask)
					: (byte) (byteMask & ((pixelMask ^ 0xff) & 0xff));
			break;
		}
		case BC2: {
			index = (((iy * conf.currentWidth) + ix) >> 2) + offset;
			ix &= 3;
			int mask = (3 << ((3 - ix) * 2) ^ 0xff) & 0xff;
			int byteMask = ((bitplane[index] & mask));
			byteMask |= activeLayer.getSelectedColorIndex() << ((3 - ix) * 2);
			bitplane[index] = byteMask;
			break;
		}
		}
	}

	private void paintTile(GC gc, int tileX, int tileY) {
		int x = 0;
		int y = 0;
		int b1 = conf.bytesPerRow * conf.height;
		int b2 = b1 * conf.tileColumns;
		int bc = conf.pixelConfig.bitCount;
		int byteOffset = 0;
		int pix = conf.isPixelGridEnabled() ? 1 : 0;
		if (supportsMultiTileView()) {
			byteOffset = conf.computeTileOffset(tileX, tileY, navigationOffset);
		} else {
			byteOffset = selectedTileOffset;
		}

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
						gc.setBackground((b & j) == j ? palette.get(String.valueOf(selectedColorIndex))
								: Constants.BITMAP_BACKGROUND_COLOR);
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
				case BC8: {

					if (null != imagePainterFactory) {
						gc.drawImage(
								imagePainterFactory.getImage(getDisplay(), b, false, conf, bitplane, colorProvider,
										palette),
								(x * conf.currentPixelWidth) + pix, (y * conf.currentPixelHeight) + pix);
					}

					/*
					 * gc.setForeground(Constants.DEFAULT_BINARY_COLOR);
					 * gc.drawString(String.valueOf(b), (x * conf.currentPixelWidth) + pix, (y *
					 * conf.currentPixelHeight) + pix);
					 */
					x++;
					break;
				}
				}
			}
		}
	}

}
