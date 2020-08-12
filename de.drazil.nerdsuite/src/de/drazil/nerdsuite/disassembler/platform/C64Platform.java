package de.drazil.nerdsuite.disassembler.platform;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.drazil.nerdsuite.basic.BasicParser;
import de.drazil.nerdsuite.disassembler.InstructionLine;
import de.drazil.nerdsuite.disassembler.cpu.CPU_6510;
import de.drazil.nerdsuite.disassembler.dialect.IDialect;
import de.drazil.nerdsuite.model.BasicInstruction;
import de.drazil.nerdsuite.model.BasicInstructions;
import de.drazil.nerdsuite.model.ReferenceType;
import de.drazil.nerdsuite.model.DataType;
import de.drazil.nerdsuite.model.Value;

public class C64Platform extends AbstractPlatform
{

	private BasicInstructions basicInstructions;

	public C64Platform(IDialect dialect, boolean ignoreStartAddressBytes)
	{
		super(dialect, new CPU_6510(), ignoreStartAddressBytes, "/configuration/platform/c64_platform.json");
	}

	@Override
	public boolean supportsSpecialStartSequence()
	{
		return true;
	}

	@Override
	public void handlePlatformSpecific(byte[] byteArray, int offset)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void parseStartSequence(byte byteArray[], Value programCounter)
	{
		// is basic start/
		if (programCounter.getValue() == 2049)
		{
			ObjectMapper mapper = new ObjectMapper();
			Map<String, BasicInstruction> basicTokenMap = new HashMap<String, BasicInstruction>();
			try
			{
				System.out.println("read BasicV2 instructions");
				basicInstructions = mapper.readValue(new File("/Users/drazil/Documents/workspace/rcp/de.drazil.NerdSuite/config/basic_v2.json"), BasicInstructions.class);
				for (BasicInstruction bs : basicInstructions.getBasicInstructionList())
				{
					basicTokenMap.put(bs.getToken(), bs);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			BasicParser basicParser = new BasicParser(programCounter, getCPU(), basicTokenMap);
			String basicCode = basicParser.start(byteArray, programCounter);
			System.out.println(basicCode);
			Value asmStart = basicParser.getLastBasicLineAddress(byteArray).add(2);
			InstructionLine instructionLine = getCPU().getInstructionLineList().get(0);
			instructionLine.setPassed(true);
			instructionLine.setDataType(DataType.BasicInstruction);
			getCPU().splitInstructionLine(instructionLine, programCounter, asmStart.sub(programCounter).add(2), DataType.Unspecified, ReferenceType.NoReference);
		}
	}

	@Override
	public byte[] parseBinary(byte[] byteArray)
	{
		byte newByteArray[] = byteArray;
		Value programCounter = new Value(0);
		if (!isIgnoreStartAddressBytes())
		{
			programCounter = new Value(getCPU().getWord(byteArray, 0));
			newByteArray = new byte[byteArray.length - 2];
			System.arraycopy(byteArray, 2, newByteArray, 0, newByteArray.length);
			byteArray = newByteArray;
		}

		System.out.println("init   : build memory map");
		init(newByteArray, programCounter, 0);
		System.out.println("stage 1: parse header information");
		parseStartSequence(newByteArray, programCounter);
		System.out.println("stage 2: parse instructions");
		getCPU().parseInstructions(newByteArray, programCounter, getCPU().getInstructionLineList().get(1), getPlatFormData(), 1);
		// System.out.println("stage 3: compress ranges");
		// getCPU().compressRanges();
		System.out.println("ready.");
		return newByteArray;
	}
}
