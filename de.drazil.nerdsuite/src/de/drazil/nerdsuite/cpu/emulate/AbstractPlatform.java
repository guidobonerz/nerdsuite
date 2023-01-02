package de.drazil.nerdsuite.cpu.emulate;

public abstract class AbstractPlatform implements IPlatform {

	protected int[] ram = new int[0xffff];
	protected int[] rom = new int[0xffff];

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
	public void runAt(int startAdress, ICPU cpu) {
		int pc = startAdress;
		for (;;) {
			if (!cpu.getBreakpoint(pc).isEnabled()) {
				pc = cpu.execute(pc, ram, rom);
			}
		}
	}
}
