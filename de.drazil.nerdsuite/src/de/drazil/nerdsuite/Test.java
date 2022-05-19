package de.drazil.nerdsuite;

public class Test {
	
	
	public static void main(String argv[]) {
		StringBuilder sb = new StringBuilder();
		int x = 100;
		for (int i = 0; i < 256; i++) {

			if (i % 16 == 0) {
				sb.append("\n" + x + "DATA");
				x += 10;
			}
			if (i < 16) {
				sb.append(String.valueOf((char) (i + 35)));
			} else {
				char h = (char) (((i >> 4) & 0x0f) + 65);
				char l = (char) ((i & 0x0f) + 65);
				sb.append(h + "" + l);
			}
			if (i > 0 && i % 16 == 0) {
				System.out.println(sb.toString());
				sb = new StringBuilder();
			}
		}
	}
}
