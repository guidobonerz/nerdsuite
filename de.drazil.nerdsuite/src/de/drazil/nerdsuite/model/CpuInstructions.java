package de.drazil.nerdsuite.model;

import java.util.List;

import lombok.Data;

@Data
public class CpuInstructions
{
	private List<CpuInstruction> cpuInstructionList;
	private List<AddressingMode> addressingModeList;
}
