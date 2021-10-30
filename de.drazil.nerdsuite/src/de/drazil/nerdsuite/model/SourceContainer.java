package de.drazil.nerdsuite.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SourceContainer {
	@JsonProperty(value = "metadata")
	private GraphicMetaData metadata = null;
	@JsonProperty(value = "source")
	private String source = null;
}
