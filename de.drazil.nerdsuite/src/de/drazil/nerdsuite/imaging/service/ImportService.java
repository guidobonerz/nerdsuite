package de.drazil.nerdsuite.imaging.service;

import java.io.File;

import de.drazil.nerdsuite.disassembler.BinaryFileHandler;
import de.drazil.nerdsuite.model.CustomSize;
import de.drazil.nerdsuite.model.GraphicFormat;
import de.drazil.nerdsuite.model.GraphicFormatVariant;
import de.drazil.nerdsuite.widget.Tile;
import lombok.Setter;

public class ImportService implements IService {
	@Setter
	String owner;

	public enum ConversionMode {
		toWorkArray, toBitplane
	}

	public TileRepositoryService doImportGraphic(String owner, String fileName, GraphicFormat gf,
			GraphicFormatVariant variant, CustomSize customSize) {
		TileRepositoryService tileRepositoryService = ServiceFactory.getService(owner, TileRepositoryService.class);

		byte[] importableContent = new byte[] {};
		try {
			importableContent = BinaryFileHandler.readFile(new File(fileName), 0);
			convert(gf, variant, customSize, importableContent, tileRepositoryService);
			System.out.println("do import...");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return tileRepositoryService;
	}

	private void convert(GraphicFormat gf, GraphicFormatVariant variant, CustomSize customSize, byte[] bitplane,
			TileRepositoryService service) {
		ConversionMode mode = ConversionMode.toWorkArray;
		int width = customSize == null ? gf.getWidth() : customSize.getWidth();
		int height = customSize == null ? gf.getHeight() : customSize.getHeight();
		int columns = customSize == null ? variant.getTileColumns() : customSize.getTileColumns();
		int rows = customSize == null ? variant.getTileRows() : customSize.getTileRows();
		int iconSize = (width / gf.getStorageEntity()) * height;
		int tileSize = iconSize * columns * rows;
		
		int size = tileSize * 255;
		int bytesPerRow = width / gf.getStorageEntity();
		int bc = 1;
		int mask = 1;
		for (int o = 0; o < size; o += tileSize) {
			Tile tile = service.addTile(tileSize * 8);
			int[] workArray = tile.getActiveLayer().getContent();
			for (int si = 0, s = 0; si < tileSize; si += bytesPerRow, s += bytesPerRow) {
				s = (si % (iconSize)) == 0 ? 0 : s;
				int xo = ((si / iconSize) & (columns - 1)) * width;
				int yo = (si / (iconSize * columns)) * height * width * columns;
				int ro = ((s / bytesPerRow) * width) * columns;
				int wai = ro + xo + yo;
				for (int i = 0; i < bytesPerRow; i++) {
					bitplane[o + si + i] = mode == ConversionMode.toBitplane ? 0 : bitplane[o + si + i];
					for (int m = 7, ti = 0; m >= 0; m -= bc, ti++) {
						if (mode == ConversionMode.toWorkArray) {
							workArray[wai + (8 * i) + ti] = (byte) ((bitplane[o + si + i] >> m) & mask);
						} else if (mode == ConversionMode.toBitplane) {
							(bitplane[o + si + i]) |= (workArray[wai + (8 * i) + ti] << m);
						}
					}
				}
			}
		}
	}
}
