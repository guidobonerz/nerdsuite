package de.drazil.nerdsuite.util;

import org.eclipse.swt.graphics.Font;

import de.drazil.nerdsuite.Constants;

public class C64Font extends FontBase
{
	public C64Font()
	{
		super();
	}

	public C64Font(boolean upperCase)
	{
		super(upperCase);
	}

	@Override
	public Font getFont()
	{
		return Constants.C64_FONT;
	}

	@Override
	public int getUnicodePrefix()
	{
		return upperCase ? 0xee00 : 0xef00;
	}

}
