package de.drazil.nerdsuite.model;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMetaData {
	public final static String PAINTER_CONFIG = "PAINTER_CONFIG";
	public final static String REPOSITORY_CONFIG = "REPOSITORY_CONFIG";
	public final static String REFERENCE_REPOSITORY_CONFIG = "REFERENCE_REPOSITORY_CONFIG";
	private String id;
	private String platform;
	private String type;
	private String variant;
	private Integer width;
	private Integer height;
	private Integer columns;
	private Integer rows;
	private Integer storageEntity;
	private Integer blankValue;
	private String referenceId;
	@JsonIgnore
	private int tileSize;
	@JsonIgnore
	@Getter
	private HashMap<String, ImagingWidgetConfiguration> viewerConfig;

	public ProjectMetaData(GraphicFormat gf, GraphicFormatVariant gfv) {
		viewerConfig = new HashMap<String, ImagingWidgetConfiguration>();
	}

	public void init(GraphicFormat gf, GraphicFormatVariant gfv) {
		_init(getViewerConfig(PAINTER_CONFIG), gf, gfv, 16);
		_init(getViewerConfig(REPOSITORY_CONFIG), gf, gfv, 16);
		_init(getViewerConfig(REFERENCE_REPOSITORY_CONFIG), gf, gfv, 2);
	}

	private void _init(ImagingWidgetConfiguration conf, GraphicFormat gf, GraphicFormatVariant gfv, int pixelSize) {
		if (gfv.getId().equals("CUSTOM")) {
			conf.width = width;
			conf.height = height;
			conf.tileColumns = columns;
			conf.tileRows = rows;
		} else {
			conf.width = gf.getWidth();
			conf.height = gf.getHeight();
			conf.tileColumns = gfv.getTileColumns();
			conf.tileRows = gfv.getTileRows();
		}
		/*
		 * int s = gfv.getPixelSize(); if
		 * (getViewerConfigName().equals(ProjectMetaData.REFERENCE_REPOSITORY_CONFIG)) {
		 * s = 2; }
		 * 
		 * if (tileRepositoryReferenceService != null) { s = 16; }
		 */
		conf.pixelSize = pixelSize;

		conf.storageSize = gf.getStorageSize();
		computeDimensions();
	}

	@JsonIgnore
	public ImagingWidgetConfiguration getViewerConfig(String name) {
		ImagingWidgetConfiguration conf = viewerConfig.get(name);
		if (conf == null) {
			conf = new ImagingWidgetConfiguration();
			viewerConfig.put(name, conf);
		}
		return conf;
	}

	@JsonIgnore
	public void computeDimensions() {
		tileSize = width * height * rows * columns;
		for (ImagingWidgetConfiguration conf : viewerConfig.values()) {
			conf.computeDimensions();
		}
	}
}
