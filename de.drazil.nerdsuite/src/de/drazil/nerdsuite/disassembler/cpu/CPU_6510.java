package de.drazil.nerdsuite.disassembler.cpu;

import java.util.HashMap;
import java.util.Map;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.disassembler.InstructionLine;
import de.drazil.nerdsuite.model.Address;
import de.drazil.nerdsuite.model.AddressingMode;
import de.drazil.nerdsuite.model.Opcode;
import de.drazil.nerdsuite.model.PlatformData;
import de.drazil.nerdsuite.model.Pointer;
import de.drazil.nerdsuite.model.Range;
import de.drazil.nerdsuite.model.ReferenceType;
import de.drazil.nerdsuite.model.DataType;
import de.drazil.nerdsuite.model.Value;
import de.drazil.nerdsuite.util.NumericConverter;

public class CPU_6510 extends AbstractCPU {
	private Map<String, Boolean> pointerTableRemindMap = null;
	private Map<String, Value> jumpMap = null;

	public CPU_6510() {
		pointerTableRemindMap = new HashMap<String, Boolean>();
		jumpMap = new HashMap<String, Value>();
	}

	@Override
	public int getWord(byte byteArray[], int offset) {
		return NumericConverter.getWordAsInt(byteArray, offset, Endianness.LittleEndian);
	}

	private void printDisassembly(InstructionLine instructionLine, byte byteArray[]) {
		if (instructionLine.getDataType() == DataType.AsmInstruction) {
			Opcode opcode = getOpcodeByIndex(byteArray, instructionLine.getRange().getOffset());
			Value value2 = getInstructionValue(byteArray, instructionLine.getRange());
			int len = opcode.getAddressingMode().getLen();
			String sv = "";
			if (len - 1 > 0) {
				sv = NumericConverter.toHexString(value2.getValue(), (len - 1) * 2);
			}
			System.out.println(instructionLine.getProgramCounter() + ":  " + opcode.getMnemonic() + " "
					+ opcode.getAddressingMode().getArgumentTemplate().replace("{value}", sv));
		}
	}

	@Override
	public void parseInstructions(byte[] byteArray, Value pc, InstructionLine instructionLine,
			PlatformData platformData, int stage) {
		InstructionLine currentLine = instructionLine;
		InstructionLine newLine = null;
		Value value = null;
		while (currentLine != null) {
			if (!currentLine.isPassed()) {
				Range range = currentLine.getRange();
				Opcode opcode = getOpcodeByIndex(byteArray, range.getOffset());

				String addressingMode = opcode.getAddressingMode().getId();
				String instructionType = opcode.getType();

				int len = opcode.getAddressingMode().getLen();
				value = getInstructionValue(byteArray, new Range(range.getOffset(), len));
				currentLine.setDataType(DataType.AsmInstruction);

				newLine = split(currentLine, pc, new Value(range.getOffset() + len));
				printDisassembly(currentLine, byteArray);
				if (currentLine.getProgramCounter().toString().equals("1f80")) {
					int a = 0;
				}
				if (newLine.getRange().getLen() < 0 || newLine.getRange().getLen() == 0) {
					System.out.println(newLine.getProgramCounter() + ": negative length or zero ..");
				}

				if (instructionType.equals("branch")) {
					boolean doSplitLine = true;
					// branches
					if (addressingMode.equals("rel")) {
						value = currentLine.getProgramCounter()
								.add(((value.getValue() & 0x80) == 0x80 ? -(((value.getValue() ^ 0xff) & 0xff) - 1)
										: value.add(2).getValue()));
					} else if (addressingMode.equals("ind")) {
						// detect jump table
						if (value.getValue() <= pc.getValue() + byteArray.length - 2) {
							int a = 0;
							// detectIndirectJumpTable(byteArray, pc,
							// currentLine, opcode,
							// value, platformData);
						}
					}

					if (doSplitLine) {
						InstructionLine jumpLine = getInstructionLineByPC(value);
						if (jumpLine != null && !jumpLine.getProgramCounter().matches(value)) {
							InstructionLine splitLine = split(jumpLine, pc, value.sub(pc));
							splitLine.setReferenceType(ReferenceType.JumpMark);
							splitLine.setDataType(DataType.AsmInstruction);
						}
					}

				} else if (addressingMode.equals("abs") || addressingMode.equals("absx")
						|| addressingMode.equals("absy")) {
					// direkt pointers
					InstructionLine dataLine = getInstructionLineByPC(value);
					if (dataLine != null && !dataLine.getProgramCounter().matches(value)) {
						InstructionLine splitLine = split(dataLine, pc, value.sub(pc));
						splitLine.setDataType(DataType.Data);
						splitLine.setReferenceType(ReferenceType.DataReference);
						splitLine.setPassed(true);
					}
				}

				// detectPointers(byteArray, pc, currentLine, platformData);
			}
			currentLine.setReferenceValue(value);
			currentLine.setPassed(true);
			currentLine = markEmptyBlockAsData(byteArray, pc, newLine);
			if (currentLine.getDataType() != DataType.Unspecified) {
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
				if ((specifiedLine = getInstructionLineList().get(rowIndex++)).getDataType() != DataType.Unspecified) {
					foundLine = true;
					break;
				}
			}
			newLine = split(currentLine, pc, new Value(specifiedLine.getRange().getOffset()));
			currentLine.setDataType(DataType.Data);
		}

		return newLine;
	}

	private InstructionLine getNextUnspecifiedLine(InstructionLine currentLine) {
		InstructionLine nextLine = currentLine;
		if (currentLine != null && currentLine.getDataType() != DataType.Unspecified) {
			int nextIndex = getInstructionLineList().indexOf(currentLine) + 1;
			if (nextIndex < getInstructionLineList().size()) {
				nextLine = getNextUnspecifiedLine(getInstructionLineList().get(nextIndex));
			} else {
				nextLine = null;
			}
		}
		return nextLine;
	}

	/*
	 * private InstructionLine getNextLine(InstructionLine instructionLine, Value
	 * pc, Value offset, int value) { return getNextLine(instructionLine, pc,
	 * offset, value, instructionLine.getType(),
	 * instructionLine.getReferenceType()); }
	 * 
	 * private InstructionLine getNextLine(InstructionLine instructionLine, Value
	 * pc, Value offset, int value, Type type, ReferenceType referenceType) {
	 * InstructionLine nextLine = null; if (instructionLine.getRange().getOffset() >
	 * offset.getValue()) { InstructionLine refLine = getInstructionLineByPC(value);
	 * if (refLine != null) { refLine.setReferenceType(referenceType); } nextLine =
	 * instructionLine; } else { InstructionLine refLine =
	 * getInstructionLineByPC(value); if (refLine == null) { InstructionLine
	 * matchLine = findInstructionLineByOffset(offset); nextLine = split(matchLine,
	 * pc, offset, type, referenceType); } else { nextLine = refLine.getType() ==
	 * Type.AsmInstruction ? instructionLine : refLine; } } return type == Type.Data
	 * ? instructionLine : nextLine; }
	 */
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

	private void detectIndirectJumpTable(byte byteArray[], Value pc, InstructionLine instructionLine, Opcode opcode,
			Value value, PlatformData platformData) {
		System.out.println("jumptable detection");
		InstructionLine lowByteLine = null;
		Value matchValue = new Value(0);
		int index = getInstructionLineList().indexOf(instructionLine) - 1;
		// if (index == -1)
		// return index;

		Opcode lookupOpcode = null;
		AddressingMode lookupAddressingMode = null;
		while (!matchValue.matches(value)) {
			lowByteLine = getInstructionLineList().get(index);
			lookupOpcode = getOpcodeByIndex(byteArray, lowByteLine.getRange().getOffset());
			lookupAddressingMode = lookupOpcode.getAddressingMode();
			if (lookupAddressingMode.getId().startsWith("abs") || lookupAddressingMode.getId().startsWith("zp")) {
				matchValue = getInstructionValue(byteArray, lowByteLine.getRange());
			} else {
				matchValue.clear();
			}
			index--;
		}

		InstructionLine lowAddressLine = getInstructionLineList().get(index);
		InstructionLine highAddressLine = getInstructionLineList().get(index + 2);
		InstructionLine lowTableLine = getInstructionLineByPC(lowAddressLine.getReferenceValue());
		InstructionLine highTableLine = getInstructionLineByPC(highAddressLine.getReferenceValue());

		String jumpTableId = lowAddressLine.getReferenceValue() + "|" + highAddressLine.getReferenceValue();

		Boolean tableChecked = pointerTableRemindMap.get(jumpTableId);

		if (tableChecked == null) {
			pointerTableRemindMap.put(jumpTableId, true);
			int jumpTableSize = Math.abs(
					lowAddressLine.getReferenceValue().getValue() - highAddressLine.getReferenceValue().getValue());

			for (int i = 0; i < jumpTableSize; i++) {
				int lowByte = getByte(byteArray, lowTableLine.getRange().getOffset() + i);
				int highByte = getByte(byteArray, highTableLine.getRange().getOffset() + i);
				int jumpMark = (int) (highByte << 8 | lowByte);

				InstructionLine jmpLine = getInstructionLineByPC(jumpMark);
				// parseInstructions(byteArray, pc, jmpLine, platformData,
				// Type.AsmInstruction, ReferenceType.JumpMark, inSubroutine);

				lowTableLine.setReferenceValue(new Value(jumpMark, Value.LOWBYTE));
				lowTableLine.setDataType(DataType.Data);
				lowTableLine.setReferenceType(ReferenceType.DataReference);

				highTableLine.setReferenceValue(new Value(jumpMark, Value.HIGHBYTE));
				highTableLine.setDataType(DataType.Data);
				highTableLine.setReferenceType(ReferenceType.DataReference);
			}
		}
		System.out.println("ready table detection");

		// return
		// getInstructionLineList().indexOf(getNextInstructionLine(instructionLine));
	}

	private void detectPointers(byte byteArray[], Value pc, InstructionLine instructionLine,
			PlatformData platformData) {
		int checkIndex = getInstructionLineList().indexOf(instructionLine);
		InstructionLine checkLineA = getInstructionLineList().get(checkIndex);
		Pointer resultPointer = null;
		Value valueA = new Value(0);
		Value valueB = new Value(0);

		Opcode opcodeA = getOpcodeByIndex(byteArray, checkLineA.getRange().getOffset());
		valueA = getInstructionValue(byteArray, checkLineA.getRange());

		if (opcodeA != null && isStoreInstruction(opcodeA.getMnemonic()) && !isDataAddress(valueA, platformData)) {
			InstructionLine checkLineB = getInstructionLineList().get(checkIndex - 2);
			Opcode opcodeB = getOpcodeByIndex(byteArray, checkLineB.getRange().getOffset());
			if (opcodeB != null) {
				valueB = getInstructionValue(byteArray, checkLineB.getRange());
				if (isStoreInstruction(opcodeB.getMnemonic()) && !isDataAddress(valueA, platformData)) {
					if (Math.abs(valueB.getValue() - valueA.getValue()) == 1) {
						InstructionLine pointerA = getInstructionLineList().get(checkIndex - 1);
						Opcode pointerAopcode = getOpcodeByIndex(byteArray, pointerA.getRange().getOffset());
						InstructionLine pointerB = getInstructionLineList().get(checkIndex - 3);
						Opcode pointerBopcode = getOpcodeByIndex(byteArray, pointerB.getRange().getOffset());
						if (pointerAopcode.getAddressingMode().getId().equals("imm")
								&& pointerBopcode.getAddressingMode().getId().equals("imm")) {
							int lowByte = getByte(byteArray, pointerB.getRange().getOffset() + 1);
							int highByte = getByte(byteArray, pointerA.getRange().getOffset() + 1);
							Value reference = new Value((int) (highByte << 8 | lowByte));

							Boolean checked = pointerTableRemindMap.get(String.valueOf(reference));
							if (checked != null) {
								pointerTableRemindMap.put(String.valueOf(reference), new Boolean(true));
								InstructionLine pointerLine = getInstructionLineByPC(reference);
								if (pointerLine == null) {
									pointerLine = findInstructionLine(reference);
									if (pointerLine != null) {
										// pointerLine = split(pointerLine, pc,
										// getOffset(pc,
										// reference));

										for (Pointer pointer : platformData.getPlatformPointerList()) {
											if (pointer.matches(
													new Value(Math.min(valueA.getValue(), valueB.getValue())))) {
												pointer.setType(DataType.AsmInstruction);
												pointer.setReferenceType(ReferenceType.JumpMark);
												resultPointer = pointer;
												break;
											}
										}
										if (resultPointer == null) {
											resultPointer = new Pointer(reference, DataType.Data,
													ReferenceType.DataReference);
										}
										pointerLine.setReferenceValue(resultPointer.getAddress());

										pointerA.setReferenceValue(new Value(reference.getValue(), Value.LOWBYTE));
										pointerB.setReferenceValue(new Value(reference.getValue(), Value.HIGHBYTE));
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private boolean isDataAddress(Value value, PlatformData platformData) {
		boolean isPlatFormAddress = false;
		for (Address address : platformData.getPlatformAddressList()) {
			if (isPlatFormAddress = address.matches(value.getValue())) {
				break;
			}
		}
		return isPlatFormAddress;
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
							|| nextLine.getDataType() == DataType.AsmInstruction)
						break;
					Range range = currentLine.getRange();
					range.setLen(range.getLen() + nextLine.getRange().getLen());
					getInstructionLineList().remove(nextLine);
				}
			}
			index++;
		}
	}

	private boolean isStoreInstruction(String instruction) {
		return instruction.equals("sta") || instruction.equals("stx") || instruction.equals("sty");
	}

	public static String getInstructionTokenKey(boolean illegalOpcode, boolean unstableOpcode) {
		if (illegalOpcode && unstableOpcode)
			return Constants.T_UNSTABLE_ILLEGAL_OPCODE;
		else if (illegalOpcode && !unstableOpcode)
			return Constants.T_ILLEGAL_OPCODE;
		else
			return Constants.T_OPCODE;
	}
}
