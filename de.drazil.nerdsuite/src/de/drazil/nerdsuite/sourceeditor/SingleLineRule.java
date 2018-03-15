package de.drazil.nerdsuite.sourceeditor;

public class SingleLineRule extends BaseRule {
	private int offset = 0;

	public SingleLineRule(String prefix, Token token) {
		super(prefix, (String) null, Marker.WHITE_SPACE, token);
		setPriority(20);
	}

	public SingleLineRule(String prefix, Marker marker, Token token) {
		super(prefix, null, marker, token);
		setPriority(marker == Marker.EOL ? 10 : marker == Marker.WHITE_SPACE ? 20 : 99);
	}

	public SingleLineRule(String prefix, String suffix, Token token) {
		super(prefix, suffix, Marker.NONE, token);
		setPriority(20);
	}

	@Override
	public boolean hasMatch(String text) {

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

				offset = matchIndex + 1;
				hasMatch = true;
			} else {
				hasMatch = false;
				offset = 0;
			}

		} else {
			int matchIndex = text.indexOf(getPrefix(), offset);
			if (matchIndex != -1) {
				System.out.println("prefix found");
				getToken().setStart(matchIndex);
				matchIndex = text.indexOf(getSuffix(), matchIndex + getPrefix().length());
				if (matchIndex != -1) {
					System.out.println("suffix found");

					offset = matchIndex + getSuffix().length();
					getToken().setLength(offset - getToken().getStart());
					hasMatch = true;
				} else {
					hasMatch = false;
					offset = 0;
				}
			} else {
				hasMatch = false;
				offset = 0;
			}
		}
		getToken().setValid(hasMatch);
		return hasMatch;
	}

}
