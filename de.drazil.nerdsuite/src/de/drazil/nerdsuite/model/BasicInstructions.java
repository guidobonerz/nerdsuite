package de.drazil.nerdsuite.model;

import java.util.List;

import lombok.Data;

@Data
public class BasicInstructions {
	private String[] blockComment;
	private String singleLineComment;
	private String stringQuote;
	private String instructionSeparator;
	private String parameterSeparator;
	private String defaultVersion;
	private List<BasicInstruction> basicInstructionList;
}
