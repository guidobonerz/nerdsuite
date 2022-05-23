package de.drazil.nerdsuite.emulator;

public interface ICPU {
	public static final int REG_FLAGS = 0;

	public long getProgramCounter();

	public long getStatus();

	public void setFlag(int flag);

	public boolean hasFlag(int flag);

	public int execute(int pc, int[] ram, int[] rom);

}
