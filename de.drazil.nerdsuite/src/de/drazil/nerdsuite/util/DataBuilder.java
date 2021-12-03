package de.drazil.nerdsuite.util;

import java.io.File;

public class DataBuilder {

	public DataBuilder() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String argv[]) {

		try {
			byte[] ba = BinaryFileHandler.readFile(new File("c:/Users/drazil/git/basic/xmas2019/src/mind-the-crip.prg"),
					2);
			int lineStart = 630;
			int lineStep = 10;
			int lineNum = lineStart;
			StringBuilder sb = new StringBuilder();
			StringBuilder sbValue = new StringBuilder();
			for (int i = 0; i < ba.length; i++) {
				int v = (int) (ba[i] & 0xff);
				String vs = String.valueOf(v);
				if ((sbValue.length() + vs.length() + 1) < 70) {
					sbValue.append(vs);
					sbValue.append(",");
				} else {
					sb.append(String.valueOf(lineNum) + "DATA");
					sb.append(sbValue);
					sb.deleteCharAt(sb.length() - 1);
					sb.append("\n");
					sbValue = new StringBuilder();
					sbValue.append(vs);
					sbValue.append(",");
					lineNum += lineStep;
				}
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append(",-1\n");
			System.out.println(sb);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
