package de.drazil.nerdsuite.basic.encode;

import java.util.List;

import de.drazil.nerdsuite.model.BasicInstructions;
import de.drazil.nerdsuite.model.CharObject;

public interface ITokenEncoder {
	public byte[] encode(String content, BasicInstructions basicInstructions, List<CharObject> charMap, boolean debug);

}
