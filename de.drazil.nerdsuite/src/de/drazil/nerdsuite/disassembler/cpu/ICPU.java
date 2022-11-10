package de.drazil.nerdsuite.disassembler.cpu;

import java.util.List;

import de.drazil.nerdsuite.disassembler.InstructionLine;
import de.drazil.nerdsuite.model.Opcode;
import de.drazil.nerdsuite.model.Range;
import de.drazil.nerdsuite.model.RangeType;
import de.drazil.nerdsuite.model.ReferenceType;
import de.drazil.nerdsuite.model.Value;

public interface ICPU extends IDecoder {
    public final static int JUMP_MODE = 1;
    public final static int BRANCH_MODE = 2;
    public final static int SUBROUTINE_MODE = 4;

    public int getWord(byte byteArray[], int offset);

    public int getByte(byte byteArray[], int offset);

    public Opcode getOpcodeByIndex(String platformId, String prefix, byte byteArray[], int offset);

    public Opcode getOpcodeById(String platformId, String prefix, int opcode);

    public void clear();

    public void resetLine();

    public int getLine();

    public int getIndexOf(InstructionLine line);

    public void addInstructionLine(InstructionLine instructionLine);

    public InstructionLine splitInstructionLine(InstructionLine instructionLine, Value basePc, Value offset);

    public InstructionLine splitInstructionLine(InstructionLine instructionLine, Value basePc, Value offset,
            RangeType type, ReferenceType referenceType);

    public InstructionLine findInstructionLineByProgrammCounter(Value value);

    public InstructionLine findInstructionLineByOffset(Value offset);

    public InstructionLine findInstructionLineByPC(Value programCounter);

    public InstructionLine findInstructionLineByRef(Value reference);

    public InstructionLine findInstructionLineByPC(int programCounter);

    public InstructionLine findInstructionLineByRef(int reference);

    public List<InstructionLine> getInstructionLineList();

    public InstructionLine getLastInstructionLine();

    public Value getInstructionValue(byte byteArray[], Range range);

    // public int getInstructionLength(byte byteArray[], int offset);

    public void compressRanges();

    public void packInstructionLines(InstructionLine instructionLine, int len);
}
