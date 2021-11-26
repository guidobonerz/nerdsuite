package de.drazil.nerdsuite.json;

import com.fasterxml.jackson.databind.util.StdConverter;

public class StringToUnicode extends StdConverter<String, Character> {

	@Override
	public Character convert(String s) {
		if (s.equals("")) {
			return ' ';
		} else {
			return s.charAt(0);
		}
	}
}
