package de.drazil.nerdsuite.sourceeditor;

import lombok.Data;
import lombok.NonNull;

@Data
public class Token
{
	private int start;
	private int length;
	@NonNull
	private String key;
	private boolean valid;

	public Token(String key)
	{
		this.key = key;
		this.valid = false;
	}

	public static Token copy(Token token)
	{
		Token t = new Token(token.getKey());
		t.setStart(token.getStart());
		t.setLength(token.getLength());
		return t;
	}

}
