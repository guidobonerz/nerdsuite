package de.drazil.nerdsuite.sourceeditor;

import de.drazil.nerdsuite.model.Range;
import de.drazil.nerdsuite.model.RangeType;

public class MultiLineRule extends BaseRule {

	public MultiLineRule(String prefix, String suffix, Token token) {
		super(prefix, suffix, Marker.NONE, token);
		setPriority(0);
	}

	@Override
	public Range hasMatch(String text, int offset) {
		Range range = null;
		int matchPrefixIndex = text.indexOf(getPrefix(), offset);
		if (matchPrefixIndex != -1) {
			int len = getPrefix().length();
			int matchSuffixIndex = text.indexOf(getSuffix(), matchPrefixIndex + len);
			if (matchSuffixIndex != -1) {
				len = matchSuffixIndex + getSuffix().length() - matchPrefixIndex;
			} else {
				len = text.length() - matchPrefixIndex;
			}
			range = new Range(matchPrefixIndex, len, RangeType.Unspecified);
		}
		return range;
	}

	@Override
	public int getPriority() {
		return 0;
	}
}
