package de.drazil.nerdsuite.sourceeditor;

import de.drazil.nerdsuite.model.Range;
import de.drazil.nerdsuite.model.SourceRules;

public class ValueRule extends BaseRule {
	private String matchPattern;
	private int valueLength = 1;

	public ValueRule(SourceRules sourceRule) {
		super(sourceRule);
	}

	public ValueRule(String prefix, String type, int length, Token token) {
		super(prefix, null, Marker.NONE, token,false);
		String prefixItems[] = getPrefix().split(";");
		matchPattern = prefixItems[0];
		// valueLength = Integer.parseInt(prefixItems[1]);
	}

	@Override
	public Range hasMatch(String text, int offset) {

		return null;
	}
}
