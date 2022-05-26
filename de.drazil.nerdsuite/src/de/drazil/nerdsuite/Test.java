package de.drazil.nerdsuite;

public class Test {

	public static void main(String argv[]) {
		int lo = 9;
		int hi = 5;
		int lo_a = 2;
		int hi_a = 7;
		int lo_over = 0;
		int lo_result = lo_a + lo;
		if (lo_result > 9) {
			lo_over = lo_result % 10;
			lo_result = lo_over;
		}
		
		int hi_over = 0;
		int hi_result = hi_a + hi + lo_over;
		if (hi_result > 9) {
			hi_over = hi_result % 10;
			hi_result = hi_over;
		}
		
		
		System.out.printf("%d + %d = %d rest(%d)\n", lo + hi * 10, lo_a + hi_a * 10, lo_result + hi_result * 10, hi_over);

	}
}
