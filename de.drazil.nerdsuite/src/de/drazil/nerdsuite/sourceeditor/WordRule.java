package de.drazil.nerdsuite.sourceeditor;

import de.drazil.nerdsuite.model.IWordMatcher;

public class WordRule extends BaseRule {
	private IWordMatcher matcher;

	public WordRule(IWordMatcher matcher, Token token) {
		super(null, null, Marker.NONE, token);
		this.matcher = matcher;
		setPriority(30);
	}

	@Override
	public boolean hasMatch(String text, int offset) {
		boolean hasMatch = matcher.hasMatch(text, getToken(), offset);
		setOffset(matcher.getOffset());
		return hasMatch;
	}
}
