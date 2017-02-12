package de.drazil.nerdsuite.disassembler;

import java.io.File;

public class BinaryViewer
{
	public BinaryViewer()
	{
	}

	public void run()
	{
		byte bin[] = new BinaryFileReader().readFile(new File("/Users/drazil/Downloads/mx25l4006e-pollin120916.bin"));

		int width = 6;
		for (int i = 75270; i < 96720 - width; i+=width)
		{
			System.out.println(i +"/"+NumericConverter.toHexString(i, 6) + ": "+NumericConverter.toBinaryString(bin, i, width).replace('0', ' ').replace('1', 'O'));
		}
	}

	public static void main(String args[])
	{
		new BinaryViewer().run();
	}
}
