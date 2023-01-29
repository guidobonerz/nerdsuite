package de.drazil.nerdsuite.cpu.emulate;

public abstract class AbstractPlatform implements IPlatform {

	private int pc = 0;
	private int[] ram;
	private int[] rom;
	protected ICPU cpu;
	private Thread lifeCycleThread = null;

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

	protected void powerOn() {
		lifeCycleThread = new Thread(this);
		lifeCycleThread.start();
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
		while (cpu.getExecutionState() != ExecutionState.TERMINATE) {
			if (cpu.getExecutionState() == ExecutionState.RUN) {
				pc = cpu.execute(pc, false);
			} else {

			}
		}
	}
}
