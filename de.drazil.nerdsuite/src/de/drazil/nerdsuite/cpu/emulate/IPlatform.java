package de.drazil.nerdsuite.cpu.emulate;

public interface IPlatform {
	public void resetCold();

	public void resetWarm();

	public void load();

	public void run(int startAdress, ICPU cpu);

	public void run(int startAdress, ICPU cpu, boolean debug);

	public void terminate();
}
