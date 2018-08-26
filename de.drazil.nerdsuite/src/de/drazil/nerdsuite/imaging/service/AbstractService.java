package de.drazil.nerdsuite.imaging.service;

import java.util.List;

import de.drazil.nerdsuite.model.TileLocation;
import de.drazil.nerdsuite.widget.IImagingCallback;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;
import lombok.Setter;

public abstract class AbstractService implements IImagingService {
	@Setter
	protected ImagingWidgetConfiguration conf = null;
	@Setter
	protected IImagingCallback callback = null;
	@Setter
	protected int navigationOffset = 0;
	@Setter
	protected Object source = null;
	protected List<TileLocation> tileLocationList = null;

	public enum ConversionMode {
		toWorkArray, toBitplane
	}

	public void runService(int action, List<TileLocation> tileLocationList, byte bitplane[]) {

		this.tileLocationList = tileLocationList;

		int width = conf.width * conf.tileColumns;
		int height = conf.height * conf.tileRows;
		callback.beforeRunService();
		for (int i = 0; i < tileLocationList.size(); i++) {
			int x = tileLocationList.get(i).x;
			int y = tileLocationList.get(i).y;
			byte workArray[] = null;
			if (needsConversion()) {
				workArray = createWorkArray();
				convert(workArray, bitplane, x, y, ConversionMode.toWorkArray);
			}
			int ofs = conf.computeTileOffset(x, y, navigationOffset);
			workArray = each(action, tileLocationList.get(i), conf, ofs, bitplane, workArray, width, height);
			if (needsConversion()) {
				convert(workArray, bitplane, x, y, ConversionMode.toBitplane);
			}
		}
		callback.afterRunService();
	}

	private void convert(byte workArray[], byte bitplane[], int x, int y, ConversionMode mode) {
		int iconSize = conf.getIconSize();
		int tileSize = conf.getTileSize();
		int tileOffset = conf.computeTileOffset(x, y, navigationOffset);
		int bc = conf.pixelConfig.bitCount;
		int mask = conf.pixelConfig.mask;
		for (int si = 0, s = 0; si < tileSize; si += conf.bytesPerRow, s += conf.bytesPerRow) {
			s = (si % (iconSize)) == 0 ? 0 : s;
			int xo = ((si / iconSize) & (conf.tileColumns - 1)) * conf.width;
			int yo = (si / (iconSize * conf.tileColumns)) * conf.height * conf.width * conf.tileColumns;
			int ro = ((s / conf.bytesPerRow) * conf.width) * conf.tileColumns;
			int wai = ro + xo + yo;

			for (int i = 0; i < conf.bytesPerRow; i++) {
				bitplane[tileOffset + si + i] = mode == ConversionMode.toBitplane ? 0 : bitplane[tileOffset + si + i];
				for (int m = 7, ti = 0; m >= 0; m -= bc, ti++) {
					if (mode == ConversionMode.toWorkArray) {
						workArray[wai + (8 * i) + ti] = (byte) ((bitplane[tileOffset + si + i] >> m) & mask);
					} else if (mode == ConversionMode.toBitplane) {
						(bitplane[tileOffset + si + i]) |= (workArray[wai + (8 * i) + ti] << m);
					}
				}
			}
		}
	}

	protected byte[] createWorkArray() {
		return new byte[conf.getTileSize() * (conf.pixelConfig.mul)];
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

	@Override
	public void setValue(int action, Object data) {
		// TODO Auto-generated method stub

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

	public int computeTileOffset(int x, int y, int offset) {
		return conf.tileSize * (x + (y * conf.columns)) + offset;
	}
}