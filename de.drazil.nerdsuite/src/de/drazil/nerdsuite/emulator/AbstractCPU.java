package de.drazil.nerdsuite.emulator;

public abstract class AbstractCPU implements ICPU {
	private long pc = 0;

	@Override
	public long getProgramCounter() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getStatus() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasFlag(int flag) {
		// TODO Auto-generated method stub
		return false;
	}
}
