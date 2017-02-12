package de.drazil.nerdsuite.util;

public abstract class FontBase implements IFont
{
	protected boolean upperCase = true;

	public FontBase()
	{
		this(true);
	}

	public FontBase(boolean upperCase)
	{
		setUpperCase(upperCase);
	}

	@Override
	public void setUpperCase(boolean upperCase)
	{
		this.upperCase = upperCase;
	}

	@Override
	public boolean showPrintableCharactersOnly()
	{
		return false;
	}
	
	
}
