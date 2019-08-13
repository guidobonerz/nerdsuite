package de.drazil.nerdsuite.model;

import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(includeFieldNames = false, of = { "name" })
public class TargetPlatform {
	private String id;
	private String name;
	private String source;
	private boolean enabled;
	private List<SimpleEntity> supportedGraphicTypes;
	private List<SimpleEntity> supportedCodingLanguages;

}
