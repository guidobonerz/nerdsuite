package de.drazil.nerdsuite.disassembler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class BinaryFileReader {
	public static byte[] readFile(File file, int bytesToSkip) {
		byte[] result = new byte[(int) file.length()];
		byte[] resultReduced = new byte[(int) file.length() - bytesToSkip];
		InputStream input = null;
		try {
			int totalBytesRead = 0;
			input = new BufferedInputStream(new FileInputStream(file));
			while (totalBytesRead < result.length) {
				int bytesRemaining = result.length - totalBytesRead;
				int bytesRead = input.read(result, totalBytesRead, bytesRemaining);
				if (bytesRead > 0) {
					totalBytesRead = totalBytesRead + bytesRead;
				}
			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (input != null) {
					input.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.arraycopy(result, 2, resultReduced, 0, resultReduced.length);
		return resultReduced;
	}
}
