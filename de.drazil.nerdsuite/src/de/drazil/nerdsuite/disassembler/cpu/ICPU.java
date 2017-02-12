package de.drazil.nerdsuite.disassembler.cpu;

import java.util.List;

import de.drazil.nerdsuite.disassembler.InstructionLine;
import de.drazil.nerdsuite.disassembler.Range;
import de.drazil.nerdsuite.disassembler.ReferenceType;
import de.drazil.nerdsuite.disassembler.Type;
import de.drazil.nerdsuite.disassembler.Value;
import de.drazil.nerdsuite.model.Opcode;
import de.drazil.nerdsuite.model.PlatformData;

public interface ICPU
{
	public final static int JUMP_MODE = 1;
	public final static int BRANCH_MODE = 2;
	public final static int SUBROUTINE_MODE = 4;

	public int getWord(byte byteArray[], int offset);

	public int getByte(byte byteArray[], int offset);

	public Opcode getOpcodeByIndex(byte byteArray[], int offset);

	public Opcode getOpcodeById(int opcode);

	public void addInstructionLine(InstructionLine instructionLine);

	public InstructionLine splitInstructionLine(InstructionLine instructionLine, Value basePc, Value offset);

	public InstructionLine splitInstructionLine(InstructionLine instructionLine, Value basePc, Value offset, Type type, ReferenceType referenceType);

	public InstructionLine findInstructionLine(Value value);

	public InstructionLine findInstructionLineByOffset(Value offset);

	public List<InstructionLine> getInstructionLineList();

	public InstructionLine getLastInstructionLine();

	public InstructionLine getInstructionLineByPC(Value programCounter);

	public InstructionLine getInstructionLineByRef(Value reference);

	public InstructionLine getInstructionLineByPC(int programCounter);

	public InstructionLine getInstructionLineByRef(int reference);

	public Value getInstructionValue(byte byteArray[], Range range);

	public int getInstructionLength(byte byteArray[], int offset);

	public void compressRanges();

	public void parseInstructions(byte byteArray[], Value pc, InstructionLine instructionLine, PlatformData platformData,  int stage);

	public void packInstructionLines(InstructionLine instructionLine, int len);
}
