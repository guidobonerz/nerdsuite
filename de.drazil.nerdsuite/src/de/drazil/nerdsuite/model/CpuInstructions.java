package de.drazil.nerdsuite.model;

import java.util.Collection;

import lombok.Data;

@Data
public class CpuInstructions {
	private Collection<String> prefixList;
	private Collection<CpuInstruction> cpuInstructionList;
	private Collection<AddressingMode> addressingModeList;
}
