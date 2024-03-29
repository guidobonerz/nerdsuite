package de.drazil.nerdsuite.json;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.stream.IntStream;

import com.fasterxml.jackson.databind.util.StdConverter;

public class IntArrayToStringConverter extends StdConverter<int[], String> {
	@Override
	public String convert(int[] ia) {
		ByteBuffer buf = ByteBuffer.allocate(ia.length);
		IntStream.of(ia).forEach(i -> buf.put((byte) (i & 0xff)));
		String result = Base64.getEncoder().encodeToString(buf.array());
		return result;
		// return Base64.getEncoder().encodeToString(result.getBytes());
	}
}
