package de.drazil.nerdsuite.sourceeditor;

import de.drazil.nerdsuite.model.Range;

public class SingleLineRule extends BaseRule {

	public SingleLineRule(String prefix, Token token) {
		super(prefix, (String) null, Marker.WHITE_SPACE, token, false);
		setPriority(20);
	}

	public SingleLineRule(String prefix, Marker marker, Token token) {
		super(prefix, null, marker, token, false);
		setPriority(marker == Marker.EOL ? 10 : marker == Marker.WHITE_SPACE ? 20 : 99);
	}

	public SingleLineRule(String prefix, String suffix, Token token, boolean skipSurroundings) {
		super(prefix, suffix, Marker.PARTITION, token, skipSurroundings);
		setPriority(30);
	}

	@Override
	public Range hasMatch(String text, int offset) {
		Range range = null;
		if (getMarker() == Marker.EOL) {
			int matchIndex = text.indexOf(getPrefix(), offset);
			if (offset == matchIndex) {
				int len = text.length() - matchIndex;
				range = new Range(offset, len);
			}
		} else if (getMarker() == Marker.PARTITION) {
			int matchPrefixIndex = text.indexOf(getPrefix(), offset);
			if (matchPrefixIndex != -1) {
				int len = getPrefix().length();
				int matchSuffixIndex = text.indexOf(getSuffix(), matchPrefixIndex + len);
				if (matchSuffixIndex != -1) {
					len = matchSuffixIndex + getSuffix().length() - matchPrefixIndex;
				} else {
					len = text.length() - matchPrefixIndex;
				}
				range = new Range(matchPrefixIndex, len);
			}
		} else {
		}

		return range;
	}
}
