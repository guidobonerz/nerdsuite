package de.drazil.nerdsuite.disassembler;

import java.util.ArrayList;
import java.util.List;

import de.drazil.nerdsuite.model.InstructionType;
import de.drazil.nerdsuite.model.DisassemblingRange;
import de.drazil.nerdsuite.model.ReferenceType;
import de.drazil.nerdsuite.model.Value;
import lombok.Data;

@Data
public class InstructionLine {

    private String name;
    private Value programCounter;
    private int offset;
    private int length;
    private Value referenceValue;
    private InstructionType instructionType;
    private ReferenceType referenceType;
    private boolean isPassed;
    private boolean endOfCode;
    private Object userObject;
    private boolean isRenderable;
    private String labelName = "";
    private List<Value> callerList = null;

    public InstructionLine() {
    }

    public InstructionLine(Value programCounter, int offset, int length) {
        this(programCounter, offset, length, InstructionType.Asm, ReferenceType.NoReference);
    }

    public InstructionLine(Value programCounter, int offset, int length, InstructionType instructionType,
            ReferenceType referenceType) {
        this.callerList = new ArrayList<Value>();
        this.programCounter = programCounter;
        this.offset = offset;
        this.length = length;
        this.instructionType = instructionType;
        this.referenceType = referenceType;
        this.isPassed = false;
        this.endOfCode = false;
        this.isRenderable = false;
    }

    public void addCaller(Value caller) {
        this.callerList.add(caller);
    }

    public boolean hasReferenceValue() {
        return referenceValue != null;
    }

    @Override
    public String toString() {
        return instructionType + "| pc:" + getProgramCounter() + ", " + range + "  refValue:" + getReferenceValue()
                + "    " + getReferenceType();
    }
}
