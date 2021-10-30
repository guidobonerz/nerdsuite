package de.drazil.nerdsuite.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class SourceRules
{
	private String prefix;
	private String suffix;
	private String token;
	private String type;
	private String content = "string";

}
