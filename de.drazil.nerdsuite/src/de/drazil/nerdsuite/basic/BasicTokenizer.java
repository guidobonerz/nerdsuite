package de.drazil.nerdsuite.basic;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.List;

import de.drazil.nerdsuite.model.BasicInstruction;
import de.drazil.nerdsuite.model.BasicInstructions;
import de.drazil.nerdsuite.util.ArrayUtil;
import de.drazil.nerdsuite.util.NumericConverter;

public class BasicTokenizer {

	enum Mode {
		READ_LINENUMBER, READ_INSTRUCTION, READ_STRING;
	};

	enum LastRead {
		NUMERIC, ALPHANUMERIC, WHITESPACE, NONE;
	}

	private static Mode readMode = Mode.READ_LINENUMBER;
	private static LastRead lastRead = LastRead.NONE;

	public BasicTokenizer() {

	}

	public static byte[] tokenize(String content, BasicInstructions basicInstructions) {
		boolean basicCommentFound = false;
		byte[] result = new byte[] {};
		CharacterIterator ci = new StringCharacterIterator(content);
		StringBuilder buffer = new StringBuilder();
		for (char ch = ci.first(); ch != CharacterIterator.DONE; ch = ci.next()) {
			switch (readMode) {
			case READ_LINENUMBER: {
				if (Character.isWhitespace(ch) && (lastRead == LastRead.NONE || lastRead == LastRead.WHITESPACE)) {
					lastRead = LastRead.WHITESPACE;
				} else if ((Character.isWhitespace(ch) || Character.isAlphabetic(ch)) && lastRead == LastRead.NUMERIC) {
					lastRead = LastRead.WHITESPACE;
					readMode = Mode.READ_INSTRUCTION;
					System.out.println(buffer);
					byte[] ba = NumericConverter.getWord(Integer.valueOf(buffer.toString()));
					result = ArrayUtil.grow(result, ba);
					result = ArrayUtil.grow(result, new byte[] { 0, 0 });
					buffer = new StringBuilder();
					if (!Character.isWhitespace(ch)) {
						buffer.append(ch);
					}
				} else if (Character.isDigit(ch)) {
					lastRead = LastRead.NUMERIC;
					buffer.append(ch);
				}
				break;
			}
			case READ_INSTRUCTION: {
				if (ch == '\n') {
					readMode = Mode.READ_LINENUMBER;
				} else if (ch == '\"') {
					readMode = Mode.READ_STRING;
					System.out.println(ch);
				} else if (Character.isWhitespace(ch)
						&& (lastRead == LastRead.NONE || lastRead == LastRead.WHITESPACE)) {
					lastRead = LastRead.WHITESPACE;
				} else if (!Character.isDigit(ch) && !Character.isWhitespace(ch)) {
					buffer.append(ch);
					BasicInstruction bi = findToken(buffer.toString(), basicInstructions.getBasicInstructionList());
					if (bi != null) {
						basicCommentFound = bi.isComment();
						byte b = (byte) (Integer.parseInt(bi.getToken(), 16) & 0xff);
						result = ArrayUtil.grow(result, b);
						buffer = new StringBuilder();
					}
				} else {
					System.out.println(ch);
				}
				break;
			}
			case READ_STRING: {
				System.out.println(ch);
				if (ch == '\"') {
					readMode = Mode.READ_INSTRUCTION;
				}
				break;
			}

			}
		}
		return null;
	}

	private static BasicInstruction findToken(String command, List<BasicInstruction> basicInructionList) {
		return basicInructionList.stream().filter(i -> i.getInstruction().equalsIgnoreCase(command)).findFirst()
				.orElse(null);
	}
}
