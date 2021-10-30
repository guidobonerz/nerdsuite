package de.drazil.nerdsuite.sourceeditor;

import org.eclipse.swt.custom.StyleRange;

public interface IDocument
{
	public String getText();

	public int getCurrentCharOffset();

	public int getVisibleLineCount();

	public int getFirstVisibleLineOffset();

	public String getLineAtIndex(int index);

	public int getLineCount();

	public int getCurrentLineIndex();

	public int getLineAtOffset(int offset);

	public int getCharOffsetAtLine(int line);

	public int getCharOffsetAtCurrentLine();

	public void setStyleRanges(StyleRange styleRanges[]);

	public void addOrReplaceStyleRanges(int start, int length, StyleRange styleRanges[]);

	public void redraw();
	
	

}
