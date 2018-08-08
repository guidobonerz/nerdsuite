package de.drazil.nerdsuite.disassembler.platform;

import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.drazil.nerdsuite.disassembler.InstructionLine;
import de.drazil.nerdsuite.disassembler.cpu.ICPU;
import de.drazil.nerdsuite.disassembler.dialect.IDialect;
import de.drazil.nerdsuite.model.PlatformData;
import de.drazil.nerdsuite.model.Range;
import de.drazil.nerdsuite.model.ReferenceType;
import de.drazil.nerdsuite.model.Type;
import de.drazil.nerdsuite.model.Value;

public abstract class AbstractPlatform implements IPlatform
{
	private IDialect dialect;
	private boolean ignoreStartAddressBytes = false;
	private ICPU cpu;
	private PlatformData platformData;

	public AbstractPlatform(IDialect dialect, ICPU cpu, boolean ignoreStartAddressBytes, String addressFileName)
	{
		setDialect(dialect);
		setCPU(cpu);
		setIgnoreStartAddressBytes(ignoreStartAddressBytes);
		readAddresses(addressFileName);
	}

	public IDialect getDialect()
	{
		return dialect;
	}

	public void setDialect(IDialect dialect)
	{
		this.dialect = dialect;
	}

	public boolean isIgnoreStartAddressBytes()
	{
		return ignoreStartAddressBytes;
	}

	public void setIgnoreStartAddressBytes(boolean ignoreStartAddressBytes)
	{
		this.ignoreStartAddressBytes = ignoreStartAddressBytes;
	}

	public ICPU getCPU()
	{
		return cpu;
	}

	public void setCPU(ICPU cpu)
	{
		this.cpu = cpu;
	}

	public void init(byte byteArray[], Value programCounter, int offset)
	{
		getCPU().addInstructionLine(new InstructionLine(programCounter, new Range(offset, byteArray.length), Type.Unspecified, ReferenceType.NoReference));
		/*
		 * for (int i = 0; i < byteArray.length; i++) {
		 * getCPU().addInstructionLine(new InstructionLine(programCounter.add(i),
		 * new Range(offset + i, 1), Type.Unspecified, ReferenceType.NoReference));
		 * }
		 */
	}

	@Override
	public PlatformData getPlatFormData()
	{
		return platformData;
	}

	private void readAddresses(String fileName)
	{
		ObjectMapper mapper = new ObjectMapper();

		try
		{
			platformData = mapper.readValue(new File(fileName), PlatformData.class);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
