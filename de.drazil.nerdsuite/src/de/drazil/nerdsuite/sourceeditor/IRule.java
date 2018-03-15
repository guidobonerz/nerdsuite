package de.drazil.nerdsuite.sourceeditor;


public interface IRule
{
	public String getPrefix();

	public String getSuffix();

	public Token getToken();

	public boolean hasMatch(String text);

	public void reset();
	
	public int getPriority();
}
