package de.drazil.nerdsuite.model;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SourceMetadata {
	private String id;
	private String platform;
	private String type;
	private String variant;

	private Map<String, Object> additionalAttributes;
}
