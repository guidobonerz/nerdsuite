package de.drazil.nerdsuite.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SourceContainer<SM> {
	@JsonProperty(value = "metadata")
	private SM metadata = null;
	@JsonProperty(value = "content")
	private String content = null;
}
