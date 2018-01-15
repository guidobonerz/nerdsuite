package de.drazil.nerdsuite.widget;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;

import de.drazil.nerdsuite.Constants;

public class BitmapPainter extends AbstractBitmapWidget implements IDrawListener, PaintListener {

	public BitmapPainter(Composite parent, int style) {
		super(parent, style);

	}

	public void paintControl(PaintEvent e) {
		if ((drawMode & SET_DRAW_PIXEL) == SET_DRAW_PIXEL) {
			drawPixel(e.gc, cursorX, cursorY);
		}
		super.paintControl(e);
	}

	private void drawPixel(GC gc, int x, int y) {

		System.out.println(getPainterName() + ":drawPixel x:" + x + "  y:" + y);
		if (x < currentWidth * tileColumns && y < height * tileRows) {
			int ix = x % currentWidth;
			int iy = y % height;
			int ax = (x / currentWidth);
			int ay = (y / height) * tileColumns;
			int offset = (ax + ay) * (height * bytesPerRow);
			if (isMultiColorEnabled()) {
				int index = (((iy * currentWidth) + ix) >> 2) + offset;
				ix &= 3;
				int mask = (3 << ((3 - ix) * 2) ^ 0xff) & 0xff;
				byte byteMask = (byte) ((byteArray[index + getOffset()] & mask));
				byteMask |= selectedColorIndex << ((3 - ix) * 2);
				if (!isReadOnly()) {
					byteArray[index + getOffset()] = byteMask;
				}
				gc.setBackground(paintMode ? palette.get(String.valueOf(selectedColorIndex))
						: Constants.BITMAP_BACKGROUND_COLOR);
			} else {
				int index = (((iy * currentWidth) + ix) >> 3) + offset;
				byte byteMask = byteArray[index + getOffset()];
				int pixelMask = (1 << (7 - (ix % 8)) & 0xff);
				if (!isReadOnly()) {
					byteArray[index + getOffset()] = paintMode ? (byte) (byteMask | pixelMask)
							: (byte) (byteMask & ((pixelMask ^ 0xff) & 0xff));
				}
				gc.setBackground(paintMode ? palette.get(String.valueOf(selectedColorIndex))
						: Constants.BITMAP_BACKGROUND_COLOR);
			}

			int pix = isPixelGridEnabled() ? 1 : 0;
			gc.fillRectangle((x * currentPixelWidth) + pix, (y * currentPixelHeight) + pix, currentPixelWidth - pix,
					currentPixelHeight - pix);
		}

	}
}