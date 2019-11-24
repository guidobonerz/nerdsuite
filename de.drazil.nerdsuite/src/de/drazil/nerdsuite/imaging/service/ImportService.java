package de.drazil.nerdsuite.imaging.service;

import java.io.File;

import de.drazil.nerdsuite.disassembler.BinaryFileHandler;
import de.drazil.nerdsuite.model.CustomSize;
import de.drazil.nerdsuite.model.GraphicFormat;
import de.drazil.nerdsuite.model.GraphicFormatVariant;
import lombok.Setter;

public class ImportService implements IService {
	@Setter
	String owner;

	public TileRepositoryService doImportGraphic(String owner, String fileName, GraphicFormat gf,
			GraphicFormatVariant variant, CustomSize customSize) {
		TileRepositoryService tileRepositoryService = ServiceFactory.getService(owner, TileRepositoryService.class);
		byte[] importableContent = new byte[] {};
		try {
			importableContent = BinaryFileHandler.readFile(new File(fileName), 0);
			int width = customSize == null ? gf.getWidth() : customSize.getWidth();
			int height = customSize == null ? gf.getHeight() : customSize.getHeight();
			int columns = customSize == null ? variant.getTileColumns() : customSize.getTileColumns();
			int rows = customSize == null ? variant.getTileRows() : customSize.getTileRows();
			int size = width / gf.getStorageEntity() * height * columns * rows * 255;
			System.out.println("do import...");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return tileRepositoryService;
	}
}
