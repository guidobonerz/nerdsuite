package de.drazil.nerdsuite.disassembler.cpu;

import de.drazil.nerdsuite.disassembler.InstructionLine;
import de.drazil.nerdsuite.model.PlatformData;
import de.drazil.nerdsuite.model.Range;
import de.drazil.nerdsuite.model.Value;

public interface IDecoder {
	public void decode(byte byteArray[], Value pc, InstructionLine instructionLine, PlatformData platformData,
			Range discoverableRange, int stage);
}
