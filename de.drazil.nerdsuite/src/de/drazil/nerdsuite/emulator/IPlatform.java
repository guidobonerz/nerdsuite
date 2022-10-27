package de.drazil.nerdsuite.emulator;

public interface IPlatform {
	public void resetCold();

	public void resetWarm();

	public void load();

	public void runAt(int startAdress, ICPU cpu);
}
