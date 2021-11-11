package de.drazil.nerdsuite.model;

import de.drazil.nerdsuite.sourceeditor.Token;

public interface IWordMatcher {
	boolean hasMatch(String value, Token token);

}
