package de.drazil.test;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class FontList
{

	public static void main(String[] args)
	{
		Display display = new Display();
		Shell shell = new Shell(display);
		displayAllLoadedFonts(shell);
		loadFont(shell, "fonts/C64_Pro_Mono-STYLE.ttf", 8);
	}

	private static Font loadFont(Shell shell, String fontFileName, int fontSize)
	{
		boolean isFontLoaded = shell.getDisplay().loadFont(fontFileName);
		if (isFontLoaded)
		{
			displayAllLoadedFonts(shell);
			// font name found by inspecting the font list..
			return new Font(shell.getDisplay(), "C64 Pro Mono", fontSize, SWT.NORMAL);
		}
		return null;
	}

	private static void displayAllLoadedFonts(Shell shell)
	{
		// display all scalable fonts in the system
		FontData[] fd = shell.getDisplay().getFontList(null, true);
		for (int i = 0; i < fd.length; i++)
		{
			System.out.println(fd[i].getName());
		}
		// and the non-scalable ones
		fd = shell.getDisplay().getFontList(null, false);
		for (int i = 0; i < fd.length; i++)
		{
			System.out.println(fd[i].getName());
		}
	}
}