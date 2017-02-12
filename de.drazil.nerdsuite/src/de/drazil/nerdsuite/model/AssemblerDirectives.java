package de.drazil.nerdsuite.model;

import java.util.List;

import lombok.Data;

@Data
public class AssemblerDirectives
{
	private List<AssemblerDirective> directives;
	private List<SourceRules> sourceRules;
}
