package de.drazil.nerdsuite.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SourceContainer {
	@JsonProperty(value = "metadata")
	private SourceMetadata metadata = null;
	@JsonProperty(value = "content")
	private String content = null;
}
