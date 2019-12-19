package de.drazil.nerdsuite.imaging.service;

import de.drazil.nerdsuite.model.ProjectMetaData;
import de.drazil.nerdsuite.widget.Tile;
import lombok.Setter;

public class IOBaseService implements IService {

	@Setter
	String owner;

	public enum ConversionMode {
		toWorkArray, toBitplane
	}

	protected static void convert(byte[] bitplane, int bytesToSkip, TileRepositoryService service,
			ConversionMode conversionMode) {

		ProjectMetaData metadata = service.getMetadata();
		String id = metadata.getPlatform() + "_" + metadata.getType();
		int width = metadata.getWidth();
		int height = metadata.getHeight();
		int columns = metadata.getColumns();
		int rows = metadata.getRows();
		int iconSize = (width / metadata.getStorageEntity()) * height;
		int tileSize = iconSize * columns * rows;
		int size = tileSize * bitplane.length / tileSize;
		int bytesPerRow = width / metadata.getStorageEntity();
		int bitPerPixel = 1;
		int mask = 1;
		for (int o = 0, tc = 0; o < size; o += tileSize, tc++) {
			// Tile tile = service.addTile(tileSize * 8);

			int[] workArray = null;
			if (conversionMode == ConversionMode.toWorkArray) {
				Tile tile = service.addTile();
				workArray = tile.getActiveLayer().getContent();
			} else if (conversionMode == ConversionMode.toBitplane) {
				workArray = service.getTile(tc).getActiveLayer().getContent();
			}
			for (int si = 0, s = 0; si < tileSize; si += bytesPerRow, s += bytesPerRow) {
				s = (si % (iconSize)) == 0 ? 0 : s;
				int xo = ((si / iconSize) & (columns - 1)) * width;
				int yo = (si / (iconSize * columns)) * height * width * columns;
				int ro = ((s / bytesPerRow) * width) * columns;
				int wai = ro + xo + yo;
				for (int i = 0; i < bytesPerRow; i++) {
					bitplane[o + si + i] = conversionMode == ConversionMode.toBitplane ? 0 : bitplane[o + si + i];
					for (int m = 7, ti = 0; m >= 0; m -= bitPerPixel, ti++) {
						if (conversionMode == ConversionMode.toWorkArray) {
							workArray[wai + (8 * i) + ti] = (byte) ((bitplane[o + si + i] >> m) & mask);
						} else if (conversionMode == ConversionMode.toBitplane) {
							bitplane[o + si + i] |= (workArray[wai + (8 * i) + ti] << m);
						}
					}
				}
				if (id.equals("C64_SPRITESET")) { // fix for byte no.64 in sprite memory
					o += ((si + bytesPerRow) % iconSize == 0 && si > 0) ? 1 : 0;
				}
			}
		}
	}
}
