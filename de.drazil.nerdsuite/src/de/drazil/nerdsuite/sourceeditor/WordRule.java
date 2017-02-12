package de.drazil.nerdsuite.sourceeditor;

public class WordRule extends BaseRule
{
	private int offset;

	public WordRule(String word, Token token)
	{
		super(word, null, token);
	}

	@Override
	public boolean hasMatch(String text)
	{
		int matchIndex = text.indexOf(getPrefix(), offset);
		if (matchIndex != -1)
		{
			getToken().setStart(matchIndex);
			int length = getPrefix().length();
			offset = matchIndex + length+1;
			getToken().setLength(length);
			hasMatch = true;

		}
		else
		{
			hasMatch = false;
			offset=0;
		}
		
		getToken().setValid(hasMatch);

		return hasMatch;
	}
}
