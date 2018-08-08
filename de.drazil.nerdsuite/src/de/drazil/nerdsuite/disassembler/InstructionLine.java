package de.drazil.nerdsuite.disassembler;

import java.util.ArrayList;
import java.util.List;

import de.drazil.nerdsuite.model.Range;
import de.drazil.nerdsuite.model.ReferenceType;
import de.drazil.nerdsuite.model.Type;
import de.drazil.nerdsuite.model.Value;

public class InstructionLine
{

	private Value programCounter;
	private Range range;
	private Value refValue;
	private Type type;
	private ReferenceType referenceType;
	private boolean isPassed;
	private boolean endOfCode;
	private List<Value> callerList = null;

	public InstructionLine()
	{
	}

	public InstructionLine(Value programCounter, Range range)
	{
		this(programCounter, range, Type.Unspecified, ReferenceType.NoReference);
	}

	public InstructionLine(Value programCounter, Range range, Type type, ReferenceType referenceType)
	{
		this.callerList = new ArrayList<Value>();
		setProgramCounter(programCounter);
		setRange(range);
		setType(type);
		setReferenceType(referenceType);
		setPassed(false);
		setEndOfCode(false);
	}

	public void addCaller(Value caller)
	{
		this.callerList.add(caller);
	}

	public boolean isEndOfCode()
	{
		return endOfCode;
	}

	public void setEndOfCode(boolean endOfCode)
	{
		this.endOfCode = endOfCode;
	}

	public Range getRange()
	{
		return range;
	}

	public void setRange(Range range)
	{
		this.range = range;
	}

	public Value getProgramCounter()
	{
		return programCounter;
	}

	public void setProgramCounter(Value programCounter)
	{
		this.programCounter = programCounter;
	}

	public Value getRefValue()
	{
		return refValue;
	}

	public void setRefValue(Value refValue)
	{
		this.refValue = refValue;
	}

	public boolean hasValue()
	{
		return refValue != null;
	}

	public Type getType()
	{
		return type;
	}

	public void setType(Type type)
	{
		this.type = type;
	}

	public ReferenceType getReferenceType()
	{
		return referenceType;
	}

	public void setReferenceType(ReferenceType referenceType)
	{
		this.referenceType = referenceType;
	}

	public boolean isPassed()
	{
		return isPassed;
	}

	public void setPassed(boolean isPassed)
	{
		this.isPassed = isPassed;
	}

	@Override
	public String toString()
	{
		return type + "| pc:" + getProgramCounter() + ", " + range + "  refValue:" + getRefValue() + "    " + getReferenceType();
	}
}
