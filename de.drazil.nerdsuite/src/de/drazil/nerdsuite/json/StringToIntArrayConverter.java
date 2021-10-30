package de.drazil.nerdsuite.json;

import java.util.Base64;

import com.fasterxml.jackson.databind.util.StdConverter;

public class StringToIntArrayConverter extends StdConverter<String, int[]> {

	@Override
	public int[] convert(String s) {

		byte[] ba = Base64.getDecoder().decode(s.getBytes());
		int[] ia = new int[ba.length];
		for (int i = 0; i < ba.length; i++) {
			ia[i] = ba[i] & 0xff;
		}
		return ia;
	}
}
