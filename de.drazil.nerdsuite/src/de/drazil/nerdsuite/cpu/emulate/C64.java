package de.drazil.nerdsuite.cpu.emulate;

public class C64 extends AbstractPlatform {

	@Override
	protected void powerOn() {
		ram[0] = 0b00101111;
		ram[1] = 0b00110111;
	}
}
