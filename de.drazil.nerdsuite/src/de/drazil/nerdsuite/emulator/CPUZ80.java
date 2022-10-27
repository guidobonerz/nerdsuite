package de.drazil.nerdsuite.emulator;

public class CPUZ80 extends AbstractCPU {

	public static final int FLAG_CARRY = 0;
	public static final int FLAG_ADD_OR_SUB = 1;
	public static final int FLAG_PARITY_OVERFLOW = 2;
	public static final int FLAG_NOT_USED1 = 4;
	public static final int FLAG_HALF_CARRY = 8;
	public static final int FLAG_NOT_USED2 = 16;
	public static final int FLAG_ZERO = 32;
	public static final int FLAG_SIGN = 64;
	public static final int FLAG_IFF1 = 128;
	public static final int FLAG_IFF2 = 256;

	public static final int REG_FLAGS = 0;
	public static final int REG_SP = 1;
	public static final int REG_PC = 2;
	public static final int REG_A = 3;
	public static final int REG_B = 4;
	public static final int REG_C = 5;
	public static final int REG_D = 6;
	public static final int REG_E = 7;
	public static final int REG_H = 8;
	public static final int REG_L = 9;
	public static final int REG_IX = 10;
	public static final int REG_IY = 11;
	public static final int REG_I = 12;
	public static final int REG_R = 13;

	@Override
	public int execute(int pc, int[] ram, int[] rom) {
		// TODO Auto-generated method stub
		return 0;
	}

}
