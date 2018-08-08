package de.drazil.nerdsuite.disassembler.platform;

import de.drazil.nerdsuite.disassembler.cpu.ICPU;
import de.drazil.nerdsuite.disassembler.dialect.IDialect;
import de.drazil.nerdsuite.model.PlatformData;
import de.drazil.nerdsuite.model.Value;

public interface IPlatform
{
	public IDialect getDialect();

	public void setDialect(IDialect dialect);

	public abstract boolean supportsSpecialStartSequence();

	public abstract void parseStartSequence(byte byteArray[], Value programCounter);

	public boolean isIgnoreStartAddressBytes();

	public void setIgnoreStartAddressBytes(boolean ignoreStartAddressBytes);

	public ICPU getCPU();

	public void setCPU(ICPU cpu);

	public void handlePlatformSpecific(byte byteArray[], int offset);

	public void init(byte byteArray[], Value programCounter, int offset);

	public byte[] parseBinary(byte byteArray[]);

	public PlatformData getPlatFormData();

}
