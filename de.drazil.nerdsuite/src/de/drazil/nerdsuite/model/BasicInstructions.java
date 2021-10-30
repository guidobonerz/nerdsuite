package de.drazil.nerdsuite.model;

import java.util.List;

import lombok.Data;

@Data
public class BasicInstructions
{
	private String stringQuote;
	private String instructionSeparator;
	private String parameterSeparator;
	private List<BasicInstruction> basicInstructionList;
}
