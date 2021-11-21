package de.drazil.nerdsuite.model;

public interface IWordMatcher {

	public Range hasMatch(String text, int offset);

	public int getTokenControl();
}
