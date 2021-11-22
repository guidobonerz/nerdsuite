package de.drazil.nerdsuite.sourceeditor;

import de.drazil.nerdsuite.model.IWordMatcher;
import de.drazil.nerdsuite.model.Range;

public class WordRule extends BaseRule {
	private IWordMatcher wordMatcher = null;

	public WordRule(IWordMatcher matcher, Token token) {
		super(null, null, Marker.NONE, token, false);
		wordMatcher = matcher;
		setPriority(30);
	}

	@Override
	public Range hasMatch(String text, int offset) {
		return wordMatcher.hasMatch(text, offset);
	}

	@Override
	public int getTokenControl() {
		return wordMatcher.getTokenControl();
	}
}
