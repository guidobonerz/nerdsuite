package de.drazil.nerdsuite.util;

import org.eclipse.swt.graphics.Font;

import de.drazil.nerdsuite.Constants;

public class AtariFont extends FontBase
{
	public AtariFont()
	{
		super();
	}

	public AtariFont(boolean upperCase)
	{
		super(upperCase);
	}

	@Override
	public Font getFont()
	{
		return Constants.Atari_Classic_FONT;
	}

	@Override
	public int getUnicodePrefix()
	{
		return 0xe000;
	}

}
