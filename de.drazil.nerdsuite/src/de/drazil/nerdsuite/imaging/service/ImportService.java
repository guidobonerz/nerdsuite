package de.drazil.nerdsuite.imaging.service;

import java.io.File;
import java.util.Map;

import de.drazil.nerdsuite.disassembler.BinaryFileHandler;
import de.drazil.nerdsuite.model.ProjectMetaData;
import de.drazil.nerdsuite.widget.Tile;
import lombok.Setter;

public class ImportService implements IService {
	@Setter
	String owner;

	public enum ConversionMode {
		toWorkArray, toBitplane
	}

	public void doImportGraphic(Map<String, Object> config) {
		String importFileName = (String) config.get("importFileName");
		TileRepositoryService repository = (TileRepositoryService) config.get("repository");
		int bytesToSkip = (Integer) config.get("bytesToSkip");

		byte[] importableContent = new byte[] {};
		try {
			importableContent = BinaryFileHandler.readFile(new File(importFileName), bytesToSkip);
			convert(importableContent, bytesToSkip, repository);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void convert(byte[] bitplane, int bytesToSkip, TileRepositoryService service) {
		ConversionMode mode = ConversionMode.toWorkArray;
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
		for (int o = 0; o < size; o += tileSize) {
			//Tile tile = service.addTile(tileSize * 8);
			Tile tile = service.addTile();
			int[] workArray = tile.getActiveLayer().getContent();
			for (int si = 0, s = 0; si < tileSize; si += bytesPerRow, s += bytesPerRow) {
				s = (si % (iconSize)) == 0 ? 0 : s;
				int xo = ((si / iconSize) & (columns - 1)) * width;
				int yo = (si / (iconSize * columns)) * height * width * columns;
				int ro = ((s / bytesPerRow) * width) * columns;
				int wai = ro + xo + yo;
				for (int i = 0; i < bytesPerRow; i++) {
					bitplane[o + si + i] = mode == ConversionMode.toBitplane ? 0 : bitplane[o + si + i];
					for (int m = 7, ti = 0; m >= 0; m -= bitPerPixel, ti++) {
						if (mode == ConversionMode.toWorkArray) {
							workArray[wai + (8 * i) + ti] = (byte) ((bitplane[o + si + i] >> m) & mask);
						} else if (mode == ConversionMode.toBitplane) {
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
