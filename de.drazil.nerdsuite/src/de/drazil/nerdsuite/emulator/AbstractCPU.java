package de.drazil.nerdsuite.emulator;

import java.util.Stack;

public abstract class AbstractCPU implements ICPU {
	protected int pc = 0;
	protected int[] registers = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	protected Stack<Byte> stack = new Stack<Byte>();

	@Override
	public long getProgramCounter() {
		return pc;
	}

	@Override
	public long getStatus() {
		return registers[REG_FLAGS];
	}

	@Override
	public void setFlag(int flag) {
		registers[REG_FLAGS] = registers[REG_FLAGS] | flag;
	}

	@Override
	public boolean hasFlag(int flag) {
		return (registers[REG_FLAGS] & flag) == flag;
	}
}
