package de.drazil.nerdsuite.sourceeditor;

public class MultiLineRule extends BaseRule {

	public MultiLineRule(String prefix, String suffix, Token token) {
		super(prefix, suffix, Marker.NONE, token);
		setPriority(0);
	}

	@Override
	public boolean hasMatch(String text, int offset) {
		boolean hasMatch = false;
		setOffset(offset);
		int matchIndex = text.indexOf(getPrefix(), offset);
		if (matchIndex != -1) {
			System.out.println("prefix found");
			getToken().setStart(matchIndex);
			matchIndex = text.indexOf(getSuffix(), matchIndex + getPrefix().length());
			if (matchIndex != -1) {
				System.out.println("suffix found");
				setOffset(matchIndex + getSuffix().length());
				getToken().setLength(offset - getToken().getStart());
				hasMatch = true;
			}
		}
		getToken().setValid(hasMatch);
		return hasMatch;
	}

	@Override
	public int getPriority() {
		return 0;
	}
}
