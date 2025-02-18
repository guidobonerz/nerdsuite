package de.drazil.nerdsuite.cpu;

import java.util.List;

import de.drazil.nerdsuite.cpu.decode.MemorySnippet;
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

    public int getIndexOf(MemorySnippet line);

    public void addInstructionLine(MemorySnippet instructionLine);

    public MemorySnippet splitInstructionLine(MemorySnippet instructionLine, Value basePc, Value offset);

    public MemorySnippet splitInstructionLine(MemorySnippet instructionLine, Value basePc, Value offset,
            RangeType type, ReferenceType referenceType);

    public MemorySnippet findInstructionLineByProgrammCounter(Value value);

    public MemorySnippet findInstructionLineByOffset(Value offset);

    public MemorySnippet findInstructionLineByPC(Value programCounter);

    public MemorySnippet findInstructionLineByRef(Value reference);

    public MemorySnippet findInstructionLineByPC(int programCounter);

    public MemorySnippet findInstructionLineByRef(int reference);

    public List<MemorySnippet> getInstructionLineList();

    public MemorySnippet getLastInstructionLine();

    public Value getInstructionValue(byte byteArray[], Range range);

    // public int getInstructionLength(byte byteArray[], int offset);

    public void compressRanges();

    public void packInstructionLines(MemorySnippet instructionLine, int len);
}
