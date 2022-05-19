package de.drazil.nerdsuite.emulator;

public interface ICPU {

	public long getProgramCounter();

	public long getStatus();

	public boolean hasFlag(int flag);
}
