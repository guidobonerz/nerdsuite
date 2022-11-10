package de.drazil.nerdsuite.basic;

import java.util.Map;

import de.drazil.nerdsuite.disassembler.cpu.ICPU;
import de.drazil.nerdsuite.model.BasicInstruction;
import de.drazil.nerdsuite.model.Value;
import de.drazil.nerdsuite.util.NumericConverter;

public class BasicParser
{
	private ICPU cpu;
	private Value basicStart;
	private Map<String, BasicInstruction> basicTokenMap;
	private int openBraces = 0;
	private int closedBraces = 0;
	private int openQuotes = 0;
	private int closedQuotes = 0;
	private boolean insideQuotes = false;

	public BasicParser(Value basicStart, ICPU cpu, Map<String, BasicInstruction> basicTokenMap)
	{
		this.basicStart = basicStart;
		this.cpu = cpu;
		this.basicTokenMap = basicTokenMap;
	}

	public ICPU getCPU()
	{
		return cpu;
	}

	public Value getLastBasicLineAddress(byte byteArray[],int offset)
	{
		int arrayIndex = offset;
		Value nextBasicLineAddress = null;
		while ((nextBasicLineAddress = new Value(getCPU().getWord(byteArray, arrayIndex))).getValue() != 0)
		{
			arrayIndex = nextBasicLineAddress.sub(basicStart).getValue();
		}
		return basicStart.add(arrayIndex);
	}

	public String start(byte byteArray[], Value startAdress)
	{
		StringBuffer sb = new StringBuffer();
		int nextLineAdress = -1;
		int lineNumberAddressOffset = 0;
		int offset = basicStart.sub(startAdress).getValue();
		while ((nextLineAdress = new Value(getCPU().getWord(byteArray, offset)).getValue()) != 0)
		{
			lineNumberAddressOffset = offset + 2;
			sb.append(scanLine(byteArray, lineNumberAddressOffset) + "\n");
			offset = nextLineAdress - basicStart.getValue();
		}

		return sb.toString();
	}

	private StringBuffer scanLine(byte byteArray[], int lineNumberAddressOffset)
	{
		insideQuotes = false;
		byte currentByte;
		int lineNumber = getCPU().getWord(byteArray, lineNumberAddressOffset);
		int linePos = lineNumberAddressOffset + 2;

		StringBuffer lineBuffer = new StringBuffer(lineNumber + " ");

		while ((currentByte = byteArray[linePos]) != 0)
		{
			switch (currentByte)
			{
				case '"':
				{
					insideQuotes = !insideQuotes;
				}
			}
			BasicInstruction instruction = null;
			if (!insideQuotes)
			{
				instruction = basicTokenMap.get(NumericConverter.toHexString(getCPU().getByte(byteArray, linePos), 2));
				if (instruction != null)
				{
					lineBuffer.append(instruction.getInstruction().toUpperCase());
				}
			}
			if (instruction == null)
			{
				lineBuffer.append((char) currentByte);
			}
			linePos++;
		}
		return lineBuffer;
	}
}
