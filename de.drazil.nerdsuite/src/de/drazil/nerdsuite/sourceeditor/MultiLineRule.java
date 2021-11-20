package de.drazil.nerdsuite.sourceeditor;

import de.drazil.nerdsuite.model.Range;

public class MultiLineRule extends BaseRule {

	public MultiLineRule(String prefix, String suffix, Token token) {
		super(prefix, suffix, Marker.NONE, token);
		setPriority(0);
	}

	@Override
	public Range hasMatch(String text, int offset) {

		return null;
	}

	@Override
	public int getPriority() {
		return 0;
	}
}
