package de.drazil.nerdsuite.json;

import org.apache.commons.text.StringEscapeUtils;

import com.fasterxml.jackson.databind.util.StdConverter;

public class StringToUnicode extends StdConverter<String, Character> {

	@Override
	public Character convert(String s) {
		return StringEscapeUtils.unescapeJson(s).charAt(0);
	}
}
