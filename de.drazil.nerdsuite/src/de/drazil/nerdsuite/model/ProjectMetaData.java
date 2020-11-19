package de.drazil.nerdsuite.model;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.drazil.nerdsuite.widget.ImagingWidgetConfiguration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
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
	@JsonIgnore
	private String referenceRepositoryId;
	@JsonIgnore
	private int tileSize;
	@JsonIgnore
	@Getter
	private HashMap<String, ImagingWidgetConfiguration> viewerConfig;

	public ProjectMetaData() {
		viewerConfig = new HashMap<String, ImagingWidgetConfiguration>();
	}

	@JsonIgnore
	public ImagingWidgetConfiguration addViewerConfig(String name) {
		ImagingWidgetConfiguration conf = new ImagingWidgetConfiguration();
		viewerConfig.put(name, conf);
		return conf;
	}

	@JsonIgnore
	public void computeDimensions() {
		tileSize = width * height * rows * columns;
		for (ImagingWidgetConfiguration conf : viewerConfig.values()) {
			conf.computeDimensions();
		}
		int a = 0;
	}

}
