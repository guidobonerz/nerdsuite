package de.drazil.nerdsuite.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasicSourceMetadata {
	private String id;
	private String platform;
	private String type;
	private String variant;
	private boolean labelMode;
	private boolean mixedMode;
	private boolean showMacros;
}
