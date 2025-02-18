package de.drazil.nerdsuite.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class SourceRules
{
	private String prefix;
	private String suffix;
	private String token;
	private String type;
	private String content = "string";

}
