package de.drazil.nerdsuite.disassembler.cpu;

import de.drazil.nerdsuite.disassembler.InstructionLine;
import de.drazil.nerdsuite.model.Address;
import de.drazil.nerdsuite.model.InstructionType;
import de.drazil.nerdsuite.model.Opcode;
import de.drazil.nerdsuite.model.PlatformData;
import de.drazil.nerdsuite.model.Range;
import de.drazil.nerdsuite.model.RangeType;
import de.drazil.nerdsuite.model.ReferenceType;
import de.drazil.nerdsuite.model.Value;
import de.drazil.nerdsuite.util.NumericConverter;

public class CPU_Z80 extends AbstractCPU {

	public CPU_Z80() {

	}

	@Override
	public int getWord(byte byteArray[], int offset) {
		return NumericConverter.getWordAsInt(byteArray, offset, Endianness.LittleEndian);
	}

	private void printDisassembly(InstructionLine instructionLine, Opcode opcode, String addressingMode,
			Address address) {
		/*
		 * if (instructionLine.getInstructionType() == InstructionType.Asm) { int len =
		 * opcode.getAddressingMode().getLen(); String sv = ""; if (len - 1 > 0) { sv =
		 * NumericConverter.toHexString(value.getValue(), (len - 1) * 2); } String pan =
		 * String.format("< %s >", (address != null ? address.getDescription() : ""));
		 * instructionLine.setUserObject(new Object[] {
		 * instructionLine.getProgramCounter(), opcode.getMnemonic(),
		 * opcode.getAddressingMode().getArgumentTemplate().replace("{value}", sv),
		 * address != null ? pan : "" }); }
		 */

	}

	@Override
	public void parseInstructions(byte[] byteArray, Value pc, InstructionLine instructionLine,
			PlatformData platformData, Range discoverableRange, int stage) {
		InstructionLine currentLine = instructionLine;
		InstructionLine newLine = null;
		Value value = null;
		line = 1;
		while (currentLine != null) {
			if (!currentLine.isPassed()) {
				Range range = currentLine.getRange();
				int offset = range.getOffset();
				String so = String.format("%04X", offset);
				String prefix1 = String.format("%02X", NumericConverter.toInt(byteArray[(int) offset]));
				String prefix2 = String.format("%02X", NumericConverter.toInt(byteArray[(int) offset + 1]));
				String prefix = prefix1 + prefix2;

				Opcode opcode = null;
				int addLen = 0;
				if (prefix.equals("DDCB") || prefix.equals("DCCB")) {
					addLen = 2;
					opcode = getOpcodeByIndex("cpc6128", prefix, byteArray, offset + 2);
				} else if (prefix1.equals("CB") || prefix1.equals("ED") || prefix1.equals("DD")
						|| prefix1.equals("FD")) {
					addLen = 1;
					opcode = getOpcodeByIndex("cpc6128", prefix1, byteArray, offset + 1);
				} else {
					opcode = getOpcodeByIndex("cpc6128", "", byteArray, offset);
				}

				String addressingMode = opcode.getAddressingMode().getId();
				String addressingModeTemplate = opcode.getAddressingMode().getArgumentTemplate();
				String instructionType = opcode.getType();

				int len = opcode.getAddressingMode().getLen() + addLen;
				String addressingModeString = addressingModeTemplate;
				if (addressingModeTemplate.contains("{WORD}")) {
					int x = getWord(byteArray, offset + opcode.getValueStartPos());
					addressingModeString = addressingModeTemplate.replace("{WORD}", String.format("%04X", x));
					value = new Value(x);
				} else if (addressingModeTemplate.contains("{BYTE}")) {
					int x = getByte(byteArray, offset + opcode.getValueStartPos());
					addressingModeString = addressingModeTemplate.replace("{BYTE}", String.format("%02X", x));
					value = new Value(x);
				}

				String instruction = String.format("%s: %s %s\n", so, opcode.getMnemonic(), addressingModeString);

				System.out.printf(instruction);

				if (offset + len > discoverableRange.getOffset() + discoverableRange.getLen()) {
					break;
				}

				currentLine.setInstructionType(InstructionType.Asm);

				int v = value.getValue();
				/*
				 * Address address = platformData.getPlatformAddressList().stream().filter(p ->
				 * p.getAddressValue() == v) .findFirst().orElse(null);
				 */
				instructionLine.setUserObject(new Object[] { instructionLine.getProgramCounter(), opcode.getMnemonic(),
						addressingModeString, "" });
				// printDisassembly(currentLine, opcode, value, null/* address */);
				newLine = split(currentLine, pc, new Value(offset + len));
				if (newLine == null) {
					break;
				}

				if (newLine.getRange().getLen() < 0 || newLine.getRange().getLen() == 0) {
					System.out.println(newLine.getProgramCounter() + ": negative length or zero ..");
				}

				line++;
			}
			currentLine.setReferenceValue(value);
			currentLine.setPassed(true);
			currentLine = markEmptyBlockAsData(byteArray, pc, newLine);
			if (currentLine.getInstructionType() != InstructionType.Asm) {
				currentLine = getNextUnspecifiedLine(currentLine);
			}

		}
	}

	private InstructionLine markEmptyBlockAsData(byte byteArray[], Value pc, InstructionLine currentLine) {
		int rowIndex = 0;
		InstructionLine newLine = null;
		InstructionLine specifiedLine = null;
		int brkCount = 0;
		for (int i = 0; i < 2; i++) {
			if (byteArray[currentLine.getRange().getOffset() + i] == 0) {
				brkCount++;
			}
		}
		boolean foundLine = false;
		newLine = currentLine;
		if (brkCount == 2) {
			rowIndex = getInstructionLineList().indexOf(currentLine);

			while (!foundLine) {
				if ((specifiedLine = getInstructionLineList().get(rowIndex++))
						.getInstructionType() != InstructionType.Data) {
					foundLine = true;
					break;
				}
			}
			newLine = split(currentLine, pc, new Value(specifiedLine.getRange().getOffset()));
			currentLine.setInstructionType(InstructionType.Data);
		}

		return newLine;
	}

	private InstructionLine getNextUnspecifiedLine(InstructionLine currentLine) {
		InstructionLine nextLine = currentLine;
		if (currentLine != null && currentLine.getInstructionType() != InstructionType.Data) {
			int nextIndex = getInstructionLineList().indexOf(currentLine) + 1;
			if (nextIndex < getInstructionLineList().size()) {
				nextLine = getNextUnspecifiedLine(getInstructionLineList().get(nextIndex));
			} else {
				nextLine = null;
			}
		}
		return nextLine;
	}

	private InstructionLine split(InstructionLine instructionLine, Value pc, Value offset) {
		InstructionLine newLine = splitInstructionLine(instructionLine, pc, offset);
		if (newLine == null) {
			int index = getInstructionLineList().indexOf(instructionLine) + 1;
			if (index < getInstructionLineList().size()) {
				newLine = getInstructionLineList().get(index);
			}
		}
		return newLine;
	}

	@Override
	public void compressRanges() {
		int index = 0;
		InstructionLine currentLine = null;

		while (index < getInstructionLineList().size() - 1) {
			currentLine = getInstructionLineList().get(index);
			if (currentLine.getReferenceType() == ReferenceType.DataReference) {
				int nextIndex = index + 1;
				for (;;) {
					if (nextIndex > getInstructionLineList().size() - 1)
						break;
					InstructionLine nextLine = getInstructionLineList().get(nextIndex);
					if (nextLine.getReferenceType() == ReferenceType.DataReference
							|| nextLine.getInstructionType() == InstructionType.Asm)
						break;
					Range range = currentLine.getRange();
					range.setLen(range.getLen() + nextLine.getRange().getLen());
					getInstructionLineList().remove(nextLine);
				}
			}
			index++;
		}
	}
}
