package de.drazil.nerdsuite.model;

import java.util.List;

import lombok.Data;

@Data
public class BasicInstruction {
	private String instruction;
	private String token;
	private String description;
	private boolean includesOpenBrace;
	private boolean isFunction;
	private boolean isCommand;
	private int minParameters;
	private int maxParameters;
	private List<String> inParameter;
	private String outValue;
	private int currentParameter = 0;

	public void reset() {
		currentParameter = 0;
	}
}
