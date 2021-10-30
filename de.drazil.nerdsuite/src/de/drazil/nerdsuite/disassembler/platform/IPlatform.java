package de.drazil.nerdsuite.disassembler.platform;

import de.drazil.nerdsuite.disassembler.cpu.ICPU;
import de.drazil.nerdsuite.disassembler.dialect.IDialect;
import de.drazil.nerdsuite.model.PlatformData;
import de.drazil.nerdsuite.model.Range;
import de.drazil.nerdsuite.model.Value;

public interface IPlatform {
	public IDialect getDialect();

	public void setDialect(IDialect dialect);

	public abstract boolean supportsSpecialStartSequence();

	public abstract void parseStartSequence(byte byteArray[], Value programCounter);

	public boolean isIgnoreStartAddressBytes();

	public void setIgnoreStartAddressBytes(boolean ignoreStartAddressBytes);

	public ICPU getCPU();

	public void setCPU(ICPU cpu);

	public void setProgrammCounter(Value pc);

	public Value getProgrammCounter();

	public void handlePlatformSpecific(byte byteArray[], int offset);

	public void init(byte byteArray[], Range range);

	public byte[] parseBinary(byte byteArray[], Range range);

	public PlatformData getPlatFormData();

	public int[] getCommonStartAddresses();

	public Value checkAdress(byte content[], int start);
}
