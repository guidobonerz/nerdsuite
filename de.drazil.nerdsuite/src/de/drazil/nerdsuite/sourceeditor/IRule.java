package de.drazil.nerdsuite.sourceeditor;

public interface IRule {
	public String getPrefix();

	public String getSuffix();

	public Token getToken();

	public Marker getMarker();

	public DocumentPartition hasMatch(String text, int offset);

	public int getOffset();

	public void reset();

	public int getPriority();

	public boolean skipSurroundings();

	public int getTokenControl();
}
