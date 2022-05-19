package de.drazil.nerdsuite.emulator;

public class CPU6510 extends AbstractCPU {
	public static final int FLAG_CARRY = 0;
	public static final int FLAG_ZERO = 1;
	public static final int FLAG_INTERRUPT = 2;
	public static final int FLAG_DECIMAL = 4;
	public static final int FLAG_BREAK = 8;
	public static final int FLAG_EXPANSION = 16;
	public static final int FLAG_OVERFLOW = 32;
	public static final int FLAG_NEGATIVE = 64;

	public static final int REG_FLAGS = 0;
	public static final int REG_SP = 1;
	public static final int REG_PC = 2;
	public static final int REG_A = 3;
	public static final int REG_X = 4;
	public static final int REG_Y = 5;
	public static final int REG_PP = 6;
	public static final int REG_DD = 7;
	private long[] registers = new long[] { 0, 0, 0, 0, 0, 0, 0, 0 };
}
