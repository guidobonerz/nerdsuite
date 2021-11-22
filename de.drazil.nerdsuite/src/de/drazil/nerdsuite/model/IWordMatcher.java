package de.drazil.nerdsuite.model;

import de.drazil.nerdsuite.sourceeditor.DocumentPartition;

public interface IWordMatcher {

	public DocumentPartition hasMatch(String text, int offset);

	public int getTokenControl();
}
