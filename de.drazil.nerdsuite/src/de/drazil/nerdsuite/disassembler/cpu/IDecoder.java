package de.drazil.nerdsuite.disassembler.cpu;

import de.drazil.nerdsuite.disassembler.InstructionLine;
import de.drazil.nerdsuite.model.PlatformData;
import de.drazil.nerdsuite.model.DisassemblingRange;
import de.drazil.nerdsuite.model.Value;

public interface IDecoder {
	public void decode(byte byteArray[], Value pc, InstructionLine instructionLine, PlatformData platformData,
			DisassemblingRange discoverableRange, int stage);
}
