package de.drazil.nerdsuite.cpu.emulate;

public class C64 extends AbstractPlatform {

	@Override
	protected void powerOn() {
		getRAM()[0] = 0b00101111;
		getRAM()[1] = 0b00110111;
	}

	@Override
	public int getMemorySize() {
		return 0xffff;
	}
}
