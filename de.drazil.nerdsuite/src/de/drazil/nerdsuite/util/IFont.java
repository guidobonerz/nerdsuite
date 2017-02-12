package de.drazil.nerdsuite.util;

import org.eclipse.swt.graphics.Font;

public interface IFont
{

	public void setUpperCase(boolean mode);

	public int getUnicodePrefix();

	public Font getFont();

	public boolean showPrintableCharactersOnly();
}
