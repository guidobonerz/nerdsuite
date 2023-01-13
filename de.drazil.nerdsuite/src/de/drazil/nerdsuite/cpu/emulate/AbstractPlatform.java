package de.drazil.nerdsuite.cpu.emulate;

public abstract class AbstractPlatform implements IPlatform {

	protected int[] ram = new int[0xffff];
	protected int[] rom = new int[0xffff];

	private boolean debug = false;
	private boolean terminate = false;

	protected abstract void powerOn();

	@Override
	public void resetCold() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resetWarm() {
		// TODO Auto-generated method stub

	}

	@Override
	public void load() {
		// TODO Auto-generated method stub

	}

	@Override
	public void run(int startAdress, ICPU cpu) {
		run(startAdress, cpu, false);
	}

	@Override
	public void run(int startAdress, ICPU cpu, boolean debug) {
		int pc = startAdress;
		while (!terminate) {
			if (!cpu.getBreakpoint(pc).isEnabled()) {
				pc = cpu.execute(pc, ram, rom);
			}
		}
	}

	@Override
	public void terminate() {
		terminate = true;
	}
}
