package de.drazil.nerdsuite.model;

import lombok.Data;

@Data
public class BasicInstruction
{
	private String command;
	private String token;
	private String description;
	private boolean openBrace;
}
