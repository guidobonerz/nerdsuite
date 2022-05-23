package de.drazil.nerdsuite.emulator;

public abstract class AbstractPlatform implements IPlatform {

	protected byte[] ram = new byte[0xffff];
	protected byte[] rom = new byte[0xffff];

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
			pc = cpu.execute(pc, ram, rom);
		}
	}
}
