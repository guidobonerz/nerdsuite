package de.drazil.nerdsuite.cpu.emulate;

public interface IPlatform extends Runnable {
	public void resetCold();

	public void resetWarm();

	public void load();

	public int getMemorySize();

	public int[] getRAM();

	public int[] getROM();

}
