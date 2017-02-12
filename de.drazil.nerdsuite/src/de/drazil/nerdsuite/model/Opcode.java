package de.drazil.nerdsuite.model;

import lombok.Data;

@Data
public class Opcode
{
	private String mnemonic;
	private String addressingModeId;
	private String hex;
	private int cycles;
	private String flags;
	private String type;
	private AddressingMode addressingMode;
}
