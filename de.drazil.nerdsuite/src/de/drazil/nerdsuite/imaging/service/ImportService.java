package de.drazil.nerdsuite.imaging.service;

import java.io.File;
import java.util.Map;

import de.drazil.nerdsuite.disassembler.BinaryFileHandler;

public class ImportService extends IOBaseService {

	public void doImportGraphic(Map<String, Object> config) {
		String fileName = (String) config.get("fileName");
		TileRepositoryService repository = ServiceFactory.getService((String)config.get("repositoryOwner"), TileRepositoryService.class);
		repository.init(1);
		int bytesToSkip = (Integer) config.get("bytesToSkip");

		byte[] importableContent = new byte[] {};
		try {
			importableContent = BinaryFileHandler.readFile(new File(fileName), bytesToSkip);
			convert(importableContent, bytesToSkip, repository, ConversionMode.toWorkArray);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
