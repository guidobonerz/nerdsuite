package de.drazil.nerdsuite.disassembler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class BinaryFileReader
{
	public static byte[] readFile(File file)
	{
		byte[] result = new byte[(int) file.length()];
		InputStream input = null;
		try
		{
			int totalBytesRead = 0;
			input = new BufferedInputStream(new FileInputStream(file));
			while (totalBytesRead < result.length)
			{
				int bytesRemaining = result.length - totalBytesRead;
				int bytesRead = input.read(result, totalBytesRead, bytesRemaining);
				if (bytesRead > 0)
				{
					totalBytesRead = totalBytesRead + bytesRead;
				}
			}
		}
		catch (FileNotFoundException ex)
		{
		}
		catch (IOException ex)
		{
		}
		finally
		{
			try
			{
				if (input != null)
				{
					input.close();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return result;
	}
}
