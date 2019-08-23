package de.drazil.nerdsuite.disassembler;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

public class BinaryFileHandler {

	public static byte[] readFile(File file, int bytesToSkip) throws Exception {

		return readFile(new BufferedInputStream(new FileInputStream(file)), bytesToSkip);
	}

	public static byte[] readFile(InputStream is, int bytesToSkip) throws Exception {

		byte[] result = null;
		byte[] resultReduced = null;
		try {
			result = IOUtils.toByteArray(is);
			resultReduced = new byte[(int) result.length - bytesToSkip];
		} catch (IOException e) {

			e.printStackTrace();
		}

		System.arraycopy(result, bytesToSkip, resultReduced, 0, resultReduced.length);
		return resultReduced;
	}

	public static void write(File file, byte[] content) throws Exception {
		write(new FileOutputStream(file), content);
	}

	public static void write(OutputStream f, byte[] content) throws Exception {
		f.write(content);
		f.close();
	}
	/*
	 * public static byte[] readFile(File file, int bytesToSkip) { byte[] result =
	 * new byte[(int) file.length()]; byte[] resultReduced = new byte[(int)
	 * file.length() - bytesToSkip]; InputStream input = null; try { int
	 * totalBytesRead = 0; input = new BufferedInputStream(new
	 * FileInputStream(file)); while (totalBytesRead < result.length) { int
	 * bytesRemaining = result.length - totalBytesRead; int bytesRead =
	 * input.read(result, totalBytesRead, bytesRemaining); if (bytesRead > 0) {
	 * totalBytesRead = totalBytesRead + bytesRead; } } } catch
	 * (FileNotFoundException ex) { ex.printStackTrace(); } catch (IOException ex) {
	 * ex.printStackTrace(); } finally { try { if (input != null) { input.close(); }
	 * } catch (IOException e) { e.printStackTrace(); } } System.arraycopy(result,
	 * bytesToSkip, resultReduced, 0, resultReduced.length); return resultReduced; }
	 */
}
