package de.drazil.nerdsuite.disassembler.cpu;

import java.util.ArrayList;
import java.util.List;

import de.drazil.nerdsuite.assembler.InstructionSet;
import de.drazil.nerdsuite.disassembler.InstructionLine;
import de.drazil.nerdsuite.model.Address;
import de.drazil.nerdsuite.model.Opcode;
import de.drazil.nerdsuite.model.PlatformData;
import de.drazil.nerdsuite.model.DisassemblingRange;
import de.drazil.nerdsuite.model.RangeType;
import de.drazil.nerdsuite.model.ReferenceType;
import de.drazil.nerdsuite.model.Value;
import de.drazil.nerdsuite.util.NumericConverter;

public abstract class AbstractCPU implements ICPU {

    protected int line;
    private static ICPU cpu = null;
    private static byte byteArray0[] = null;
    private List<InstructionLine> instructionLineList = null;

    public AbstractCPU() {
        cpu = this;
        instructionLineList = new ArrayList<InstructionLine>();
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
        instructionLineList = new ArrayList<InstructionLine>();
    }

    @Override
    public void addInstructionLine(InstructionLine instructionLine) {
        instructionLineList.add(instructionLine);
    }

    @Override
    public InstructionLine splitInstructionLine(InstructionLine instructionLine, Value basePc, Value len) {
        return splitInstructionLine(instructionLine, basePc, len, RangeType.Unspecified, ReferenceType.NoReference);
    }

    @Override
    public InstructionLine splitInstructionLine(InstructionLine instructionLine, Value basePc, Value offset,
            RangeType rangeType, ReferenceType referenceType) {
        DisassemblingRange range = instructionLine.getRange();
        int oldLen = range.getLen();
        int newLen = offset.sub(range.getOffset()).getValue();
        if (oldLen == newLen) {
            return null;
        }

        range.setLen(newLen);

        InstructionLine newInstructionLine = new InstructionLine(basePc.add(range.getOffset() + newLen),
                new DisassemblingRange(range.getOffset() + newLen, oldLen - newLen, false, rangeType));

        newInstructionLine.setReferenceType(referenceType);
        instructionLineList.add(instructionLineList.indexOf(instructionLine) + 1, newInstructionLine);
        return newInstructionLine;
    }

    @Override
    public InstructionLine findInstructionLineByProgrammCounter(Value programmCounter) {
        InstructionLine instructionLine = null;
        for (InstructionLine il : instructionLineList) {
            if (programmCounter.getValue() >= il.getProgramCounter().getValue()
                    && programmCounter.getValue() <= (il.getProgramCounter().getValue() + il.getRange().getLen() - 1)) {
                instructionLine = il;
                break;
            }
        }
        return instructionLine;
    }

    public int getIndexOf(InstructionLine line) {
        return instructionLineList.indexOf(line);
    }

    @Override
    public InstructionLine findInstructionLineByOffset(Value offset) {
        InstructionLine instructionLine = null;
        for (InstructionLine il : instructionLineList) {
            if (offset.getValue() >= il.getRange().getOffset()
                    && offset.getValue() <= (il.getRange().getOffset() + il.getRange().getLen() - 1)) {
                instructionLine = il;
                break;
            }
        }
        return instructionLine;
    }

    @Override
    public InstructionLine getLastInstructionLine() {
        return instructionLineList.get(instructionLineList.size() - 1);
    }

    @Override
    public List<InstructionLine> getInstructionLineList() {
        return instructionLineList;
    }

    @Override
    public Value getInstructionValue(byte[] byteArray, DisassemblingRange range) {
        int value = 0;
        int len = range.getLen() - 1;
        int offset = range.getOffset() + 1;
        switch (len) {
            case 1: {
                value = getByte(byteArray, offset);
                break;
            }
            case 2: {
                value = getWord(byteArray, offset);
                break;
            }
        }

        return new Value(value);
    }

    @Override
    public InstructionLine getInstructionLineByPC(Value programCounter) {
        if (programCounter == null)
            return null;
        return getInstructionLineByPC(programCounter.getValue());
    }

    @Override
    public InstructionLine getInstructionLineByPC(int programCounter) {
        InstructionLine il = null;
        for (InstructionLine il1 : instructionLineList) {
            if (il1.getProgramCounter().getValue() == programCounter
                    || programCounter >= il1.getProgramCounter().getValue()
                            && programCounter < il1.getProgramCounter().getValue() + il1.getRange().getLen()) {
                il = il1;
                break;
            }
        }
        return il;
    }

    @Override
    public InstructionLine getInstructionLineByRef(Value reference) {
        return getInstructionLineByRef(reference.getValue());
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
    public InstructionLine getInstructionLineByRef(int reference) {
        InstructionLine il = null;
        for (InstructionLine il1 : instructionLineList) {
            if (il1.hasReferenceValue()) {
                if (il1.getReferenceValue().getValue() == reference) {
                    il = il1;
                    break;
                }
            }
        }
        return il;
    }

    @Override
    public void packInstructionLines(InstructionLine instructionLine, int len) {
        int lineIndex = getInstructionLineList().indexOf(instructionLine) + 1;
        int i = 0;
        while (i < len - 1) {
            getInstructionLineList().remove(lineIndex);
            i++;
        }
        instructionLine.getRange().setLen(len);
    }

    public static String getMnemonicArgument(Opcode opcode, DisassemblingRange range, byte byteArray[]) {
        int len = range.getLen() - 1;
        int offset = range.getOffset() + 1;
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
