package de.drazil.nerdsuite.sourceeditor;

import de.drazil.nerdsuite.model.Range;

public class SingleLineRule extends BaseRule {

	public SingleLineRule(String prefix, Token token) {
		super(prefix, (String) null, Marker.WHITE_SPACE, token);
		setPriority(20);
	}

	public SingleLineRule(String prefix, Marker marker, Token token) {
		super(prefix, null, marker, token);
		setPriority(marker == Marker.EOL ? 10 : marker == Marker.WHITE_SPACE ? 20 : 99);
	}

	public SingleLineRule(String prefix, String suffix, Token token) {
		super(prefix, suffix, Marker.PARTITION, token);
		setPriority(30);
	}

	@Override
	public Range hasMatch(String text, int offset) {
		return null;
	}
}
