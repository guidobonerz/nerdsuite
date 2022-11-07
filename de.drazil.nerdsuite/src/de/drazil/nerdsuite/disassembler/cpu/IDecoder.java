package de.drazil.nerdsuite.disassembler.cpu;

import de.drazil.nerdsuite.disassembler.InstructionLine;
import de.drazil.nerdsuite.model.PlatformData;
import de.drazil.nerdsuite.model.DisassemblingRange;
import de.drazil.nerdsuite.model.Value;
import de.drazil.nerdsuite.widget.IContentProvider;

public interface IDecoder {
    public void decode(IContentProvider contentProvider, Value pc, InstructionLine instructionLine,
            PlatformData platformData,
            DisassemblingRange discoverableRange, int stage);
}
