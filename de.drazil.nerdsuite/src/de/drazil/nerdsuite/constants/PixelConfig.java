package de.drazil.nerdsuite.constants;

public enum PixelConfig {
	BC8("MultiColor256", 8, 0, 256, 1, 1), BC2("MultiColor4", 2, 3, 3, 4, 2), BC1("MonoColor", 1, 3, 1, 8, 1);

	public final String name;
	public final int bitCount;
	public final int shift;
	public final int mask;
	public final int mul;
	public final int pixmul;

	PixelConfig(String name, int bitCount, int shift, int mask, int mul, int pixmul) {
		this.name = name;
		this.bitCount = bitCount;
		this.shift = shift;
		this.mask = mask;
		this.mul = mul;
		this.pixmul = pixmul;
	}
}
