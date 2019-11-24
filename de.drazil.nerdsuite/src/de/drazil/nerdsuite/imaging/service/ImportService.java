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

	public TileRepositoryService doImportGraphic(String owner, File file, GraphicFormat gf,
			GraphicFormatVariant variant, CustomSize customSize) {
		TileRepositoryService tileRepositoryService = ServiceFactory.getService(owner, TileRepositoryService.class);
		byte[] importableContent = new byte[] {};
		try {
			importableContent = BinaryFileHandler.readFile(file, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("do import...");

		return tileRepositoryService;
	}
}
