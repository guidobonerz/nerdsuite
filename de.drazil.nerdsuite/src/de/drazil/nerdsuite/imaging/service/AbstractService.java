package de.drazil.nerdsuite.imaging.service;

import java.util.List;

import de.drazil.nerdsuite.model.TileLocation;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;
import lombok.Setter;

public abstract class AbstractService implements IImagingService {
	@Setter
	protected ImagingWidgetConfiguration conf = null;
	@Setter
	private int navigationOffset = 0;

	public enum ConversionMode {
		toWorkArray, toBitplane
	}

	public void runService(int action, List<TileLocation> tileLocationList, ImagingWidgetConfiguration configuration,
			int offset, byte bitplane[]) {
		navigationOffset = offset;
		int width = conf.width * conf.tileColumns;
		int height = conf.height * conf.tileRows;
		for (int i = 0; i < tileLocationList.size(); i++) {
			byte workArray[] = null;
			if (needsConversion()) {
				workArray = createWorkArray();
				convert(workArray, bitplane, tileLocationList.get(i).x, tileLocationList.get(i).y,
						ConversionMode.toWorkArray);
			}
			int ofs = conf.computeTileOffset(tileLocationList.get(i).x, tileLocationList.get(i).y, offset);
			workArray = each(action, tileLocationList.get(i), configuration, ofs, bitplane, workArray, width, height);
			if (needsConversion()) {
				convert(workArray, bitplane, tileLocationList.get(i).x, tileLocationList.get(i).y,
						ConversionMode.toBitplane);
			}
		}
	}

	private void convert(byte workArray[], byte bitplane[], int x, int y, ConversionMode mode) {
		int iconSize = conf.getIconSize();
		int tileSize = conf.getTileSize();
		int tileOffset = conf.computeTileOffset(x, y, navigationOffset);

		for (int si = 0, s = 0; si < tileSize; si += conf.bytesPerRow, s += conf.bytesPerRow) {
			s = (si % (iconSize)) == 0 ? 0 : s;
			int xo = ((si / iconSize) & (conf.tileColumns - 1)) * conf.width;
			int yo = (si / (iconSize * conf.tileColumns)) * conf.height * conf.width * conf.tileColumns;
			int ro = ((s / conf.bytesPerRow) * conf.width) * conf.tileColumns;
			int wai = ro + xo + yo;

			for (int i = 0; i < conf.bytesPerRow; i++) {
				bitplane[tileOffset + si + i] = mode == ConversionMode.toBitplane ? 0 : bitplane[tileOffset + si + i];
				for (int m = 7, ti = 0; m >= 0; m -= (conf.isMultiColorEnabled() ? 2 : 1), ti++) {
					if (mode == ConversionMode.toWorkArray) {
						workArray[wai + (8 * i) + ti] = (byte) ((bitplane[tileOffset + si + i] >> m)
								& (conf.isMultiColorEnabled() ? 3 : 1));
					} else if (mode == ConversionMode.toBitplane) {
						(bitplane[tileOffset + si + i]) |= (workArray[wai + (8 * i) + ti] << m);
					}
				}
			}
		}
	}

	protected byte[] createWorkArray() {
		return new byte[conf.getTileSize() * (conf.isMultiColorEnabled() ? 4 : 8)];
	}

	@Override
	public byte[] each(int action, TileLocation tileLocation, ImagingWidgetConfiguration configuration, int offset,
			byte[] bitplane, byte workArray[], int width, int height) {
		return null;
	}

	@Override
	public boolean needsConversion() {
		return true;
	}

	private void printResult(byte workArray[]) {
		System.out.println("-----------------------------------------");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < workArray.length; i++) {
			if (i % (conf.width * conf.tileColumns) == 0) {
				sb.append("\n");
			}
			sb.append(workArray[i]);
		}
		System.out.println(sb);
	}
}
