package de.drazil.nerdsuite.sourceeditor;

import de.drazil.nerdsuite.model.SourceRules;

public class ValueRule extends BaseRule {
	private int offset = 0;
	private String matchPattern;
	private int valueLength = 1;

	public ValueRule(SourceRules sourceRule) {
		super(sourceRule);

	}

	public ValueRule(String prefix, String type, int length, Token token) {
		super(prefix, null, Marker.NONE, token);
		String prefixItems[] = getPrefix().split(";");
		matchPattern = prefixItems[0];
		// valueLength = Integer.parseInt(prefixItems[1]);
	}

	@Override
	public boolean hasMatch(String text) {
		int matchIndex = text.indexOf(getPrefix(), offset);
		if (matchIndex != -1) {
			System.out.println("prefix found");
			getToken().setStart(matchIndex);

			int pos = matchIndex;
			while (pos < text.length()) {
				if (Character.isWhitespace(text.charAt(pos)))
					break;
				pos++;
			}
			getToken().setLength(pos - matchIndex);
			offset = pos;
			hasMatch = true;
		} else {
			hasMatch = false;
			offset = 0;
		}
		getToken().setValid(hasMatch);

		return hasMatch;
	}
}
