package de.drazil.nerdsuite.imaging.service;

import java.io.File;
import java.util.Map;

import de.drazil.nerdsuite.disassembler.BinaryFileHandler;
import de.drazil.nerdsuite.model.ProjectMetaData;

public class ExportService extends IOBaseService {

	public final static int WRITE_PNG = 1;
	public final static int WRITE_GIF = 2;
	public final static int WRITE_ANIMATED_GIF = 4;
	public final static int WRITE_SVG = 8;
	public final static int ENABLE_TELEVISION_MODE = 128;

	private boolean isTelevisionModeEnabled = false;

	public void doExportGraphic(Map<String, Object> config) {
		String fileName = (String) config.get("fileName");
		TileRepositoryService repository = (TileRepositoryService) config.get("repository");

		ProjectMetaData metadata = repository.getMetadata();

		byte[] content = new byte[repository.getSize() * metadata.getTileSize() / metadata.getStorageEntity()];
		try {
			convert(content, 0, repository, ConversionMode.toBitplane);
			BinaryFileHandler.write(new File(fileName, "exported_font"), content, 0, content.length,
					true);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
