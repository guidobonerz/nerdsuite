package de.drazil.nerdsuite.cpu.emulate;

public class C64 extends AbstractPlatform {

	// C64 PAL: 0,9852486 MHz     , NTSC: 1,0227273 MHz
	//		1014.9722618231 ns	977.7777517037 ns
	@Override
	protected void init() {
		getRAM()[0] = 0b00101111;
		getRAM()[1] = 0b00110111;
	}

	@Override
	public int getMemorySize() {
		return 0xffff;
	}
	
	public static void main(String argv[]) {
		CPU6510 cpu = new  CPU6510(new C64());
		cpu.getPlatform().powerOn();
		
	}
}
