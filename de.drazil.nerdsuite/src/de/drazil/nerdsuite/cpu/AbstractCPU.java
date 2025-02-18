package de.drazil.nerdsuite.cpu;

import java.util.ArrayList;
import java.util.List;

import de.drazil.nerdsuite.cpu.decode.MemorySnippet;
import de.drazil.nerdsuite.model.Address;
import de.drazil.nerdsuite.model.MemoryBlock;
import de.drazil.nerdsuite.model.Opcode;
import de.drazil.nerdsuite.model.PlatformData;
import de.drazil.nerdsuite.model.Range;
import de.drazil.nerdsuite.model.RangeType;
import de.drazil.nerdsuite.model.ReferenceType;
import de.drazil.nerdsuite.model.Value;
import de.drazil.nerdsuite.util.NumericConverter;

public abstract class AbstractCPU implements ICPU {

    protected int line;
    private static ICPU cpu = null;
    private static byte byteArray0[] = null;
    private List<MemorySnippet> instructionLineList = null;

    public AbstractCPU() {
        cpu = this;
        instructionLineList = new ArrayList<MemorySnippet>();
    }

    public static ICPU getCPU() {
        return cpu;
    }

    @Override
    public int getLine() {
        return line;
    }

    @Override
    public void resetLine() {
        line = 0;

    }

    public static void setByteArray(byte byteArray[]) {
        byteArray0 = byteArray;
    }

    public static byte[] getByteArray() {
        return byteArray0;
    }

    @Override
    public int getByte(byte byteArray[], int offset) {
        return NumericConverter.getByteAsInt(byteArray, offset);
    }

    @Override
    public Opcode getOpcodeByIndex(String platformId, String prefix, byte byteArray[], int offset) {
        return getOpcodeById(platformId, prefix, NumericConverter.toInt(byteArray[(int) offset]));
    }

    @Override
    public Opcode getOpcodeById(String platformId, String prefix, int opcode) {
        return InstructionSet.getOpcodeList(platformId, prefix).get(opcode);
    }

    @Override
    public void clear() {
        instructionLineList = new ArrayList<MemorySnippet>();
    }

    @Override
    public void addInstructionLine(MemorySnippet instructionLine) {
        instructionLineList.add(instructionLine);
    }

    @Override
    public MemorySnippet splitInstructionLine(MemorySnippet instructionLine, Value basePc, Value len) {
        return splitInstructionLine(instructionLine, basePc, len, RangeType.Unspecified, ReferenceType.NoReference);
    }

    @Override
    public MemorySnippet splitInstructionLine(MemorySnippet instructionLine, Value basePc, Value offset,
            RangeType rangeType, ReferenceType referenceType) {
        Range range = instructionLine.getRange();
        int oldLen = range.getLength();
        int newLen = offset.sub(range.getOffset()).getValue();
        if (oldLen == newLen) {
            return null;
        }

        range.setLength(newLen);

        MemorySnippet newInstructionLine = new MemorySnippet(basePc.add(range.getOffset() + newLen),
                new Range(range.getOffset() + newLen, oldLen - newLen));

        newInstructionLine.setReferenceType(referenceType);
        instructionLineList.add(instructionLineList.indexOf(instructionLine) + 1, newInstructionLine);
        return newInstructionLine;
    }

    @Override
    public MemorySnippet findInstructionLineByProgrammCounter(Value programmCounter) {
        MemorySnippet instructionLine = instructionLineList
                .stream().filter(il -> programmCounter.getValue() >= il.getProgramCounter().getValue()
                        && programmCounter
                                .getValue() <= (il.getProgramCounter().getValue() + il.getRange().getLength() - 1))
                .findFirst().orElse(null);
        return instructionLine;
    }

    public int getIndexOf(MemorySnippet line) {
        return instructionLineList.indexOf(line);
    }

    @Override
    public MemorySnippet findInstructionLineByOffset(Value offset) {
        MemorySnippet instructionLine = instructionLineList.stream()
                .filter(il -> offset.getValue() >= il.getRange().getOffset()
                        && offset.getValue() <= (il.getRange().getOffset() + il.getRange().getLength() - 1))
                .findFirst().orElse(null);
        return instructionLine;
    }

    @Override
    public MemorySnippet getLastInstructionLine() {
        return instructionLineList.get(instructionLineList.size() - 1);
    }

    @Override
    public List<MemorySnippet> getInstructionLineList() {
        return instructionLineList;
    }

    @Override
    public Value getInstructionValue(byte[] byteArray, Range range) {
        int value = 0;
        int len = range.getLength() - 1;
        switch (len) {
            case 1: {
                value = getByte(byteArray, range.getOffset() + 1);
                break;
            }
            case 2: {
                value = getWord(byteArray, range.getOffset() + 1);
                break;
            }
        }

        return new Value(value);
    }

    @Override
    public MemorySnippet findInstructionLineByPC(Value programCounter) {
        if (programCounter == null)
            return null;
        return findInstructionLineByPC(programCounter.getValue());
    }

    @Override
    public MemorySnippet findInstructionLineByPC(int programCounter) {
        MemorySnippet instructionLine = instructionLineList.stream()
                .filter(il -> il.getProgramCounter().getValue() == programCounter
                        || programCounter >= il.getProgramCounter().getValue()
                                && programCounter < il.getProgramCounter().getValue() + il.getRange().getLength())
                .findFirst().orElse(null);
        return instructionLine;
    }

    @Override
    public MemorySnippet findInstructionLineByRef(Value reference) {
        return findInstructionLineByRef(reference.getValue());
    }

    protected boolean isPlatFormAddress(PlatformData platformData, int value) {
        boolean found = false;
        for (Address address : platformData.getPlatformAddressList()) {
            if (address.getAddressValue() == value) {
                found = true;
                break;
            }
        }
        return found;
    }

    @Override
    public MemorySnippet findInstructionLineByRef(int reference) {
        MemorySnippet instructionLine = instructionLineList.stream()
                .filter(il -> il.hasReferenceValue() && il.getReferenceValue().getValue() == reference).findFirst()
                .orElse(null);
        return instructionLine;
    }

    @Override
    public void packInstructionLines(MemorySnippet instructionLine, int len) {
        int lineIndex = getInstructionLineList().indexOf(instructionLine) + 1;
        int i = 0;
        while (i < len - 1) {
            getInstructionLineList().remove(lineIndex);
            i++;
        }
        instructionLine.getRange().setLength(len);
    }

    public static String getMnemonicArgument(Opcode opcode, MemoryBlock range, byte byteArray[]) {
        int len = range.getRange().getLength() - 1;
        int offset = range.getRange().getOffset() + 1;
        int value = 0;

        String argument = opcode.getAddressingMode().getArgumentTemplate();

        if (len > 0) {
            if (len == 2) {
                value = getCPU().getWord(byteArray, offset);
            } else {
                value = getCPU().getByte(byteArray, offset);
            }
            argument = argument.replace("{value}", "$" + NumericConverter.toHexString(value, (len * 2)));
            if (!argument.endsWith(",X") && !argument.endsWith(",Y") && !argument.endsWith(",X)")) {
                argument = argument + "  ";
            }
        }
        return argument;
    }

}
