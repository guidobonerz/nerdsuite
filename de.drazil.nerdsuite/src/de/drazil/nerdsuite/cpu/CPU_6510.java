package de.drazil.nerdsuite.cpu;

import java.util.HashMap;
import java.util.Map;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.cpu.decode.MemorySnippet;
import de.drazil.nerdsuite.enums.ValueType;
import de.drazil.nerdsuite.model.Address;
import de.drazil.nerdsuite.model.AddressingMode;
import de.drazil.nerdsuite.model.MemoryBlock;
import de.drazil.nerdsuite.model.InstructionType;
import de.drazil.nerdsuite.model.Opcode;
import de.drazil.nerdsuite.model.PlatformData;
import de.drazil.nerdsuite.model.Pointer;
import de.drazil.nerdsuite.model.Range;
import de.drazil.nerdsuite.model.RangeType;
import de.drazil.nerdsuite.model.ReferenceType;
import de.drazil.nerdsuite.model.Value;
import de.drazil.nerdsuite.util.NumericConverter;
import de.drazil.nerdsuite.widget.IContentProvider;

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

	private void printDisassembly(MemorySnippet instructionLine, Opcode opcode, Value value, Address address) {
		if (instructionLine.getInstructionType() == InstructionType.Asm) {
			int len = opcode.getAddressingMode().getLen();
			String sv = "";
			if (len - 1 > 0) {
				sv = NumericConverter.toHexString(value.getValue(), (len - 1) * 2);
			}
			String pan = String.format("< %s >", (address != null ? address.getDescription() : ""));
			instructionLine.setUserObject(new Object[] { instructionLine.getProgramCounter(), opcode.getMnemonic(),
					opcode.getAddressingMode().getArgumentTemplate().replace("{value}", sv),
					address != null ? pan : "" });
		}
	}

	@Override
	public void decode(IContentProvider contentProvider, Value pc, PlatformData platformData,
			MemoryBlock decodableRange, int stage) {

		if (decodableRange.getRangeType() == RangeType.Code) {
			MemorySnippet currentLine = findInstructionLineByOffset(new Value(decodableRange.getRange().getOffset()));
			split(currentLine, pc, new Value(decodableRange.getRange().getOffset() + decodableRange.getRange().getLength()));

			MemorySnippet newLine = null;
			Value value = null;
			while (currentLine != null) {
				if (!currentLine.isPassed()) {

					Range range = currentLine.getRange();
					int offset = range.getOffset();
					String so = String.format("%04X", currentLine.getProgramCounter().getValue());
					int index = contentProvider.getContentAtOffset(offset) & 0xff;
					Opcode opcode = getOpcodeById(platformData.getPlatformId(), "", index);

					String addressingMode = opcode.getAddressingMode().getId();
					String instructionType = opcode.getType();

					int len = opcode.getAddressingMode().getLen();

					// if (offset + len > decodableRange.getOffset() + decodableRange.getLen()) {

					// no more code or data to parse
					if (offset + len > contentProvider.getContentLength()) {
						// setRangeInformation(pc, decodableRange, true);
						break;
					}

					if (len > decodableRange.getRange().getLength()) {
						currentLine.setInstructionType(InstructionType.Data);
						setDataRangeInformation(pc, decodableRange, true);
						newLine = split(currentLine, pc, new Value(offset + decodableRange.getRange().getLength()));
						currentLine = newLine;
						break;
					} else {

						value = getInstructionValue(contentProvider.getContentArray(),
								new Range(contentProvider.getContentOffset() + offset, len));
						currentLine.setInstructionType(InstructionType.Asm);

						if ("branch".equals(instructionType)) {
							boolean doSplitLine = true;
							// branches
							if ("rel".equals(addressingMode)) {
								value = currentLine.getProgramCounter()
										.add(((value.getValue() & 0x80) == 0x80
												? -(((value.getValue() ^ 0xff) & 0xff) - 1)
												: value.add(2).getValue()));
							} else if ("ind".equals(addressingMode)) {
								// detect jump table
								// if (value.getValue() <= pc.getValue() + byteArray.length - 2) {

								// }
							}
							currentLine.setReferenceValue(value);
						} else if ("abs".equals(addressingMode) || "absx".equals(addressingMode)
								|| "absy".equals(addressingMode)) {
							// direkt pointers

						}

						String byteString = "";
						for (int i = 0; i < 4; i++) {
							if (i < opcode.getAddressingMode().getLen()) {
								byteString += String.format("%02X ", contentProvider.getContentAtOffset(offset + i));
							} else {
								byteString += "   ";
							}
						}

						Address address = null;
						if ("mod".equals(instructionType) || "branch".equals(instructionType)) {
							int v = value.getValue();
							address = platformData.getPlatformAddressList().stream()
									.filter(p -> p.getAddressValue() == v).findFirst().orElse(null);
						}

						// printDisassembly(currentLine, opcode, value, address);
						String sv = "";
						if (len - 1 > 0) {
							sv = NumericConverter.toHexString(value.getValue(), (len - 1) * 2);
						}
						String addressingModeString = opcode.getAddressingMode().getArgumentTemplate()
								.replace("{value}", sv);

						if(currentLine.getRange().getOffset()==0x132a)
						{
							int f=0;
						}
						
						currentLine.setUserObject(new Object[] { decodableRange.getRangeType().toString(), so, "",
								byteString, opcode.getMnemonic(), addressingModeString,
								address != null ? address.getConstName() : "" });
						currentLine.setReferenceValue(value);
						currentLine.setPassed(true);

						long currentEnd = currentLine.getRange().getOffset() + len;
						long decodeEnd = decodableRange.getRange().getOffset() + decodableRange.getRange().getLength();

						if (currentEnd < decodeEnd) {
							newLine = split(currentLine, pc, new Value(offset + len));

							if (newLine.getRange().getLength() < 0 || newLine.getRange().getLength() == 0) {
								System.out.println(newLine.getProgramCounter() + ": negative length or zero ..");
							}
							currentLine = newLine;
						} else {
							break;
						}

						// detectPointers(byteArray, pc, currentLine, platformData);

						// currentLine = markEmptyBlockAsData(byteArray, pc, newLine);

						/*
						 * if (currentLine.getInstructionType() != InstructionType.Asm) { currentLine =
						 * getNextUnspecifiedLine(currentLine); }
						 */
					}
				}
			}

		} else if (decodableRange.getRangeType() == RangeType.Binary) {
			setDataRangeInformation(pc, decodableRange, false);
		} else {
			// Unspecified Data Block
		}

	}

	private void setDataRangeInformation(Value pc, MemoryBlock range, boolean isIncomplete) {
		MemorySnippet currentLine = findInstructionLineByOffset(new Value(range.getRange().getOffset()));
		int from = pc.getValue() + range.getRange().getOffset();
		int till = pc.getValue() + range.getRange().getOffset() + range.getRange().getLength() - 1;

		String soFrom = String.format("%04X", from);
		String soTill = String.format("%04X", till);
		if (isIncomplete) {
			currentLine.setUserObject(new Object[] { range.getRangeType().toString(), soFrom, "",
					"INCOMPLETE OPCODE VISIULIZATION BLOCK from :" + soFrom + " to " + soTill });
		} else {
			currentLine.setUserObject(new Object[] { range.getRangeType().toString(), soFrom, "",
					"DATA BLOCK from :" + soFrom + " to " + soTill });
		}
		split(currentLine, pc, new Value(range.getRange().getOffset() + range.getRange().getLength()));
	}

	private MemorySnippet markEmptyBlockAsData(byte byteArray[], Value pc, MemorySnippet currentLine) {
		int rowIndex = 0;
		MemorySnippet newLine = null;
		MemorySnippet specifiedLine = null;
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

	private MemorySnippet getNextUnspecifiedLine(MemorySnippet currentLine) {
		MemorySnippet nextLine = currentLine;
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

	private MemorySnippet split(MemorySnippet instructionLine, Value pc, Value offset) {

		MemorySnippet newLine = splitInstructionLine(instructionLine, pc, offset);
		if (newLine == null) {
			int index = getInstructionLineList().indexOf(instructionLine) + 1;
			if (index < getInstructionLineList().size()) {
				newLine = getInstructionLineList().get(index);
			}
		}
		return newLine;
	}

	private void detectIndirectJumpTable(byte byteArray[], Value pc, MemorySnippet instructionLine, Opcode opcode,
			Value value, PlatformData platformData) {
		System.out.println("jumptable detection");
		MemorySnippet lowByteLine = null;
		Value matchValue = new Value(0);
		int index = getInstructionLineList().indexOf(instructionLine) - 1;
		// if (index == -1)
		// return index;

		Opcode lookupOpcode = null;
		AddressingMode lookupAddressingMode = null;
		while (!matchValue.matches(value)) {
			lowByteLine = getInstructionLineList().get(index);
			lookupOpcode = getOpcodeByIndex("", "", byteArray, lowByteLine.getRange().getOffset());
			lookupAddressingMode = lookupOpcode.getAddressingMode();
			if (lookupAddressingMode.getId().startsWith("abs") || lookupAddressingMode.getId().startsWith("zp")) {
				matchValue = getInstructionValue(byteArray,
						new Range(lowByteLine.getRange().getOffset(), lowByteLine.getRange().getLength()));
			} else {
				matchValue.clear();
			}
			index--;
		}

		MemorySnippet lowAddressLine = getInstructionLineList().get(index);
		MemorySnippet highAddressLine = getInstructionLineList().get(index + 2);
		MemorySnippet lowTableLine = findInstructionLineByPC(lowAddressLine.getReferenceValue());
		MemorySnippet highTableLine = findInstructionLineByPC(highAddressLine.getReferenceValue());

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

				MemorySnippet jmpLine = findInstructionLineByPC(jumpMark);
				// parseInstructions(byteArray, pc, jmpLine, platformData,
				// Type.AsmInstruction, ReferenceType.JumpMark, inSubroutine);

				lowTableLine.setReferenceValue(new Value(jumpMark, ValueType.LOWBYTE));
				lowTableLine.setInstructionType(InstructionType.Data);
				lowTableLine.setReferenceType(ReferenceType.DataReference);

				highTableLine.setReferenceValue(new Value(jumpMark, ValueType.HIGHBYTE));
				highTableLine.setInstructionType(InstructionType.Data);
				highTableLine.setReferenceType(ReferenceType.DataReference);
			}
		}
		System.out.println("ready table detection");

		// return
		// getInstructionLineList().indexOf(getNextInstructionLine(instructionLine));
	}

	private void detectPointers(byte byteArray[], Value pc, MemorySnippet instructionLine,
			PlatformData platformData) {
		int checkIndex = getInstructionLineList().indexOf(instructionLine);
		MemorySnippet checkLineA = getInstructionLineList().get(checkIndex);
		Pointer resultPointer = null;
		Value valueA = new Value(0);
		Value valueB = new Value(0);

		Opcode opcodeA = getOpcodeByIndex("C64", "", byteArray, checkLineA.getRange().getOffset());
		valueA = getInstructionValue(byteArray,
				new Range(checkLineA.getRange().getOffset(), checkLineA.getRange().getLength()));

		if (opcodeA != null && isStoreInstruction(opcodeA.getMnemonic()) && !isDataAddress(valueA, platformData)) {
			MemorySnippet checkLineB = getInstructionLineList().get(checkIndex - 2);
			Opcode opcodeB = getOpcodeByIndex("C64", "", byteArray, checkLineB.getRange().getOffset());
			if (opcodeB != null) {
				valueB = getInstructionValue(byteArray,
						new Range(checkLineB.getRange().getOffset(), checkLineB.getRange().getLength()));
				if (isStoreInstruction(opcodeB.getMnemonic()) && !isDataAddress(valueA, platformData)) {
					if (Math.abs(valueB.getValue() - valueA.getValue()) == 1) {
						MemorySnippet pointerA = getInstructionLineList().get(checkIndex - 1);
						Opcode pointerAopcode = getOpcodeByIndex("C64", "", byteArray, pointerA.getRange().getOffset());
						MemorySnippet pointerB = getInstructionLineList().get(checkIndex - 3);
						Opcode pointerBopcode = getOpcodeByIndex("C64", "", byteArray, pointerB.getRange().getOffset());
						if (pointerAopcode.getAddressingMode().getId().equals("imm")
								&& pointerBopcode.getAddressingMode().getId().equals("imm")) {
							int lowByte = getByte(byteArray, pointerB.getRange().getOffset() + 1);
							int highByte = getByte(byteArray, pointerA.getRange().getOffset() + 1);
							Value reference = new Value((int) (highByte << 8 | lowByte));

							Boolean checked = pointerTableRemindMap.get(String.valueOf(reference));
							if (checked != null) {
								pointerTableRemindMap.put(String.valueOf(reference), Boolean.TRUE);
								MemorySnippet pointerLine = findInstructionLineByPC(reference);
								if (pointerLine == null) {
									pointerLine = findInstructionLineByProgrammCounter(reference);
									if (pointerLine != null) {
										// pointerLine = split(pointerLine, pc,
										// getOffset(pc,
										// reference));

										for (Pointer pointer : platformData.getPlatformPointerList()) {
											if (pointer.matches(
													new Value(Math.min(valueA.getValue(), valueB.getValue())))) {
												pointer.setType(RangeType.Code);
												pointer.setReferenceType(ReferenceType.JumpMark);
												resultPointer = pointer;
												break;
											}
										}
										if (resultPointer == null) {
											resultPointer = new Pointer(reference, RangeType.Binary,
													ReferenceType.DataReference);
										}
										pointerLine.setReferenceValue(resultPointer.getAddress());

										pointerA.setReferenceValue(new Value(reference.getValue(), ValueType.LOWBYTE));
										pointerB.setReferenceValue(new Value(reference.getValue(), ValueType.HIGHBYTE));
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
		MemorySnippet currentLine = null;

		while (index < getInstructionLineList().size() - 1) {
			currentLine = getInstructionLineList().get(index);
			if (currentLine.getReferenceType() == ReferenceType.DataReference) {
				int nextIndex = index + 1;
				for (;;) {
					if (nextIndex > getInstructionLineList().size() - 1)
						break;
					MemorySnippet nextLine = getInstructionLineList().get(nextIndex);
					if (nextLine.getReferenceType() == ReferenceType.DataReference
							|| nextLine.getInstructionType() == InstructionType.Asm)
						break;
					int length = currentLine.getRange().getLength();
					// range.setLen(range.getLen() + nextLine.getLength());
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
