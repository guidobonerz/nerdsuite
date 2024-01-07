package de.drazil.nerdsuite.imaging.service;

import java.io.File;
import java.util.Map;

import de.drazil.nerdsuite.model.GraphicMetadata;
import de.drazil.nerdsuite.util.BinaryFileHandler;
import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;

public class ExportService extends IOBaseService {

	public final static int NATIVE = 1;
	public final static int PNG = 2;
	public final static int GIF = 3;
	public final static int ANIMATED_GIF = 4;
	public final static int SVG = 5;
	public final static int MODE_CHARSET = 1;
	public final static int MODE_SPRITESET = 2;
	public final static int MODE_SCREENSET = 3;
	public final static int MODE_TILESET = 4;

	public final static int ENABLE_SCANLINES = 128;

	private boolean isTelevisionModeEnabled = false;

	public void doExportGraphic(Map<String, Object> config) {
		String fileName = (String) config.get("fileName");
		TileRepositoryService repository = (TileRepositoryService) config.get("repository");

		GraphicMetadata metadata = repository.getMetadata();

		int size = repository.getSize() * 8;
		byte[] content = new byte[size];
		try {
			convert(content, 0, repository, ConversionMode.toBitplane);
			BinaryFileHandler.write(new File(fileName, "exported_font.c64"), content, 0, content.length, true);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
