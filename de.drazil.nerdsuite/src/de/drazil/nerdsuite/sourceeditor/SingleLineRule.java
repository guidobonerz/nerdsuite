package de.drazil.nerdsuite.sourceeditor;

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
	public boolean hasMatch(String text, int offset) {
		boolean hasMatch = false;
		setOffset(offset);
		if (getPrefix() == null && getSuffix() != null) {
			int matchIndex = text.indexOf(getSuffix(), offset);
			if (matchIndex != -1) {
				System.out.println("suffix found");

				int pos = matchIndex;
				while (pos > 0) {
					if (Character.isWhitespace(text.charAt(pos)))
						break;
					pos--;
				}
				getToken().setStart(pos);
				getToken().setLength(matchIndex - pos + 1);
				setOffset(matchIndex + 1);
				hasMatch = true;
			}
		} else {
			int matchIndex = text.indexOf(getPrefix(), offset);
			if (matchIndex != -1) {
				System.out.println("prefix found");
				getToken().setStart(matchIndex);

				if (getMarker() == Marker.EOL) {
					getToken().setLength(text.length());
					setOffset(matchIndex + text.length());
					hasMatch = true;
				} else {
					String s = getSuffix() == null ? "" : getSuffix();
					matchIndex = text.indexOf(s, matchIndex + getPrefix().length());
					if (matchIndex != -1) {
						System.out.println("suffix found");
						setOffset(matchIndex + s.length());
						getToken().setLength(offset - getToken().getStart());
						hasMatch = true;
					}
				}
			}
		}
		getToken().setValid(hasMatch);
		return hasMatch;
	}

}
