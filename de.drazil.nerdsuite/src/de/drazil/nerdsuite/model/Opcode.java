package de.drazil.nerdsuite.model;

import lombok.Data;

@Data
public class Opcode {
	private String mnemonic;
	private String addressingModeId;
	private String prefix;
	private String hex;
	private int valueStartPos;
	private int cycles;
	private String flags;
	private String type;
	private AddressingMode addressingMode;
}
