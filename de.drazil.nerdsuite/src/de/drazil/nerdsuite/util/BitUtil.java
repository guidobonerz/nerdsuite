package de.drazil.nerdsuite.util;

public class BitUtil {
	public static int rol(int value, int bits, int maxBits) {
		return (value << bits) | (value >> maxBits - bits);
	}

	public static int ror(int value, int bits, int maxBits) {
		return (value >> bits) | (value << maxBits - bits);
	}

	public static int reverse(int value, int maxBits) {
		int result = 0;
		int b = 0;
		for (int i = 0; i < maxBits; i++) {
			b = 1 << i;
			if ((value & b) == b)
				result |= 1 << ((maxBits - 1) - i);
		}
		return result;
	}
}
