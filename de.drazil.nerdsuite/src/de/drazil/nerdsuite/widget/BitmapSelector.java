package de.drazil.nerdsuite.widget;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;

import de.drazil.nerdsuite.Constants;

public class BitmapSelector extends AbstractBitmapWidget implements IDrawListener, PaintListener {

	public BitmapSelector(Composite parent, int style) {
		super(parent, style);

	}

	public void paintControl(PaintEvent e) {

		if ((drawMode & SET_DRAW_ALL) == SET_DRAW_ALL) {
			drawImage(e.gc);
		}
		super.paintControl(e);
		if (isTileCursorEnabled()) {
			drawTileCursor(e.gc, mouseIn);
		}
		drawMode = DRAW_NOTHING;
	}

	public void drawImage(GC gc) {
		int x = 0;
		int y = 0;
		int b1 = bytesPerRow * height;
		int b2 = b1 * tileColumns;
		for (int tc = 0; tc < columns; tc++) {
			for (int tr = 0; tr < rows; tr++) {
				offset = (getWidth() / 8) * getHeight() * tileColumns * tileRows
						* (tc + (tr * columns));
				
				for (int i = offset, k = 0; i < (offset + getViewportSize()); i++, k++) {
					int b = (byteArray[i] & 0xff);
					int xi = (k % bytesPerRow) * (8 / (isMultiColorEnabled() ? 2 : 1));
					int xo = (k / b1) % tileColumns;
					x = xi + (xo * currentWidth) + (tc * width * tileColumns);

					int yi = (k / bytesPerRow) % height;
					int yo = (k / b2) % tileRows;
					y = yi + (yo * height) + (tr * height * tileRows);
					int colorMapIndex = k / width;

					if (isMultiColorEnabled()) {
						for (int j = 6; j >= 0; j -= 2) {
							int bi = b;
							int colorIndex = (bi >> j) & 3;
							gc.setBackground(
									colorProvider.getColorByIndex((byte) colorIndex, byteArray, offset, colorMapIndex));
							int pix = isPixelGridEnabled() ? 1 : 0;
							gc.fillRectangle((x * currentPixelWidth) + pix, (y * currentPixelHeight) + pix,
									currentPixelWidth - pix, currentPixelHeight - pix);
							x++;
						}
					} else {
						for (int j = 128; j > 0; j >>= 1) {
							gc.setBackground((b & j) == j ? palette.get(String.valueOf(selectedColorIndex))
									: Constants.BITMAP_BACKGROUND_COLOR);
							int pix = isPixelGridEnabled() ? 1 : 0;
							gc.fillRectangle((x * currentPixelWidth) + pix, (y * currentPixelHeight) + pix,
									currentPixelWidth - pix, currentPixelHeight - pix);
							x++;
						}
					}
				}
			}
		}
	}

}