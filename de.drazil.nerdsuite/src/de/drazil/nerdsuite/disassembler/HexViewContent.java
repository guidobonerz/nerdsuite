package de.drazil.nerdsuite.disassembler;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.custom.StyledTextContent;
import org.eclipse.swt.custom.TextChangeListener;
import org.eclipse.swt.custom.TextChangedEvent;

public class HexViewContent implements StyledTextContent {

	private List<TextChangeListener> listenerList;
	private StringBuilder content;

	public HexViewContent() {
		listenerList = new ArrayList<TextChangeListener>();
		content = new StringBuilder();
	}

	@Override
	public void addTextChangeListener(TextChangeListener listener) {
		listenerList.add(listener);
	}

	@Override
	public int getCharCount() {
		return content.length();
	}

	@Override
	public String getLine(int lineIndex) {
		if (content.length() == 0) {
			return "";
		}
		int start = getOffsetAtLine(lineIndex);
		String s = getTextRange(start, 16 * 3);
		return s;
	}

	@Override
	public int getLineAtOffset(int offset) {
		int result = offset / (16 * 3);
		if (result >= getLineCount())
			return getLineCount() - 1;

		return result;
	}

	@Override
	public int getLineCount() {
		return content.length() - 1 / ((3 * 16) + 1);
	}

	@Override
	public String getLineDelimiter() {
		return "";
	}

	@Override
	public int getOffsetAtLine(int lineIndex) {
		return lineIndex * 3 * 16;
	}

	@Override
	public String getTextRange(int start, int length) {
		return content.substring(start, start + length);
	}

	@Override
	public void removeTextChangeListener(TextChangeListener listener) {
		listenerList.remove(listener);
	}

	@Override
	public void setText(String text) {
		content.append(text);
		fireSetText();
	}

	private void fireSetText() {
		TextChangedEvent changedEvent = new TextChangedEvent(this);
		for (TextChangeListener listener : listenerList) {
			listener.textSet(changedEvent);
		}
	}

	@Override
	public void replaceTextRange(int start, int replaceLength, String text) {
		// TODO Auto-generated method stub

	}

}
