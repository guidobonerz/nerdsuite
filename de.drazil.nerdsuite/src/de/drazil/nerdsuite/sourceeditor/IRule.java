package de.drazil.nerdsuite.sourceeditor;

import de.drazil.nerdsuite.model.Range;

public interface IRule {
	public String getPrefix();

	public String getSuffix();

	public Token getToken();

	public Marker getMarker();

	public Range hasMatch(String text, int offset);

	public int getOffset();

	public void reset();

	public int getPriority();
}
