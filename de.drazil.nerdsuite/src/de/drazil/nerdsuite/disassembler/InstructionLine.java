package de.drazil.nerdsuite.disassembler;

import java.util.ArrayList;
import java.util.List;

import de.drazil.nerdsuite.model.Range;
import de.drazil.nerdsuite.model.ReferenceType;
import de.drazil.nerdsuite.model.DataType;
import de.drazil.nerdsuite.model.Value;
import lombok.Data;

@Data
public class InstructionLine {

	private Value programCounter;
	private Range range;
	private Value referenceValue;
	private DataType dataType;
	private ReferenceType referenceType;
	private boolean isPassed;
	private boolean endOfCode;
	private Object userObject;
	private boolean isRenderable;
	private List<Value> callerList = null;

	public InstructionLine() {
	}

	public InstructionLine(Value programCounter, Range range) {
		this(programCounter, range, DataType.Unspecified, ReferenceType.NoReference);
	}

	public InstructionLine(Value programCounter, Range range, DataType dataType, ReferenceType referenceType) {
		this.callerList = new ArrayList<Value>();
		this.programCounter = programCounter;
		this.range = range;
		this.dataType = dataType;
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
		return dataType + "| pc:" + getProgramCounter() + ", " + range + "  refValue:" + getReferenceValue() + "    "
				+ getReferenceType();
	}
}
