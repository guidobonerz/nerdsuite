package de.drazil.nerdsuite.imaging;

import org.eclipse.swt.graphics.Color;

import de.drazil.nerdsuite.assembler.InstructionSet;
import de.drazil.nerdsuite.widget.IColorProvider;

public class KoalaColorProvider implements IColorProvider {

	@Override
	public Color getColorByIndex(byte bitmapByte, byte bitmap[], int x, int y, int columns) {
		int colorIndex = getBackgroundColorIndex(bitmap);
		int index = (y * columns) + x;
		if ((bitmapByte) == 1) {
			colorIndex = (bitmap[8000 + index] >> 4) & 0xf;
		} else if ((bitmapByte) == 2) {
			colorIndex = (bitmap[8000 + index] & 0xf);
		} else if ((bitmapByte) == 3) {
			colorIndex = (bitmap[9000 + index] & 0xf);
		}

		return InstructionSet.getPlatformData().getColorPalette().get(colorIndex).getColor();
	}

	@Override
	public int getBackgroundColorIndex(byte[] bitmap) {
		return bitmap[10000];
	}

	@Override
	public boolean isMultiColorEnabled() {
		return true;
	}

}
