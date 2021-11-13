package de.drazil.nerdsuite.model;

import de.drazil.nerdsuite.sourceeditor.Token;

public interface IWordMatcher {
	public boolean hasMatch(String value, Token token, int offset);

	public int getOffset();
}
