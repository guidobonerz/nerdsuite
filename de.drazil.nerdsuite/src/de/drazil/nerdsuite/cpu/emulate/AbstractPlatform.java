package de.drazil.nerdsuite.cpu.emulate;

public abstract class AbstractPlatform implements IPlatform {

	private int pc = 0;
	private int[] ram;
	private int[] rom;
	protected ICPU cpu;
	private Thread lifeCycleThread = null;
	private long lastCycle = 0;

	public AbstractPlatform() {
		ram = new int[getMemorySize()];
		rom = new int[getMemorySize()];
	}

	public abstract int getMemorySize();

	public int[] getRAM() {
		return ram;
	}

	public int[] getROM() {
		return rom;
	}

	protected abstract void init();

	public void powerOn() {
		init();
		lifeCycleThread = new Thread(this);
		lifeCycleThread.start();
		lastCycle = System.nanoTime();

	}

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
	public void run() {
		for (;;) {
			long ct = System.nanoTime();
			if (lastCycle + 1024444 > System.nanoTime()) {
				lastCycle = System.nanoTime();
				System.out.println(System.currentTimeMillis());
				// pc = cpu.execute(pc, false);
			}
		}
	}
}
