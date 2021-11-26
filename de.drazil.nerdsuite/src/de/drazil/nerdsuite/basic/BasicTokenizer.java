package de.drazil.nerdsuite.basic;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.List;

import de.drazil.nerdsuite.model.BasicInstruction;
import de.drazil.nerdsuite.model.BasicInstructions;
import de.drazil.nerdsuite.model.CharMap;
import de.drazil.nerdsuite.model.CharObject;
import de.drazil.nerdsuite.util.ArrayUtil;
import de.drazil.nerdsuite.util.NumericConverter;

public class BasicTokenizer {

	enum Mode {
		READ_LINENUMBER, READ_INSTRUCTIONS, READ_STRING, READ_BLOCK_COMMENT, READ_LINE_COMMENT;
	};

	enum LastRead {
		NUMERIC, ALPHANUMERIC, LETTER, WHITESPACE, NONE;
	}

	private static Mode readMode = Mode.READ_LINENUMBER;
	private static Mode lastReadMode = readMode;
	private static LastRead lastRead = LastRead.NONE;

	public BasicTokenizer() {

	}

	public static byte[] tokenize(String content, BasicInstructions basicInstructions, List<CharObject> charMap) {
		long startTime = System.currentTimeMillis();
		boolean doNotScan = false;
		byte[] result = new byte[] {};
		int offset = 0;
		char quote = basicInstructions.getStringQuote().charAt(0);

		CharacterIterator ci = new StringCharacterIterator(content);
		StringBuilder buffer = new StringBuilder();
		for (char ch = ci.first(); ch != CharacterIterator.DONE; ch = ci.next()) {

			if (content.indexOf(basicInstructions.getBlockComment()[0], ci.getIndex()) == ci.getIndex()) {
				System.out.printf("block start index at %s\n", ci.getIndex());
				ci.setIndex(ci.getIndex() + 1);
				lastReadMode = readMode;
				readMode = Mode.READ_BLOCK_COMMENT;
			}
			if (content.indexOf(basicInstructions.getSingleLineComment(), ci.getIndex()) == ci.getIndex()
					&& readMode != Mode.READ_BLOCK_COMMENT) {
				lastReadMode = readMode;
				readMode = Mode.READ_LINE_COMMENT;
			}

			switch (readMode) {
			case READ_BLOCK_COMMENT: {
				int match = 0;
				if ((match = content.indexOf(basicInstructions.getBlockComment()[1], ci.getIndex())) != -1) {
					ci.setIndex(match + 1);
					readMode = lastReadMode;
				}
				break;
			}
			case READ_LINE_COMMENT: {
				if (content.indexOf("\n", ci.getIndex()) == ci.getIndex()) {
					readMode = lastReadMode;
				}
				break;
			}
			case READ_LINENUMBER: {
				if ((ch != '\n' && ch != '\r' && Character.isWhitespace(ch) || Character.isAlphabetic(ch))
						&& lastRead == LastRead.NUMERIC) {
					readMode = Mode.READ_INSTRUCTIONS;
					byte[] ba = NumericConverter.getWord(Integer.valueOf(buffer.toString()));
					result = ArrayUtil.grow(result, new byte[] { 0, 0 });
					result = ArrayUtil.grow(result, ba);
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
			case READ_INSTRUCTIONS: {
				boolean found = false;
				if (!doNotScan) {
					int start = ci.getIndex();
					for (BasicInstruction instruction : basicInstructions.getBasicInstructionList()) {
						if (content.indexOf(instruction.getInstruction().toUpperCase(), start) == start) {
							doNotScan = instruction.isComment();
							result = ArrayUtil.grow(result, buffer.toString().getBytes());
							byte b = (byte) (Integer.parseInt(instruction.getToken(), 16) & 0xff);
							result = ArrayUtil.grow(result, b);
							ci.setIndex(start + (instruction.getInstruction().length() - 1));
							buffer = new StringBuilder();
							found = true;
							break;
						}
					}
					if (!found) {
						if (ch != '\r' && ch != '\n') {
							buffer.append(ch);
						}
					}
				} else {
					if (ch != '\r' && ch != '\n') {
						buffer.append(ch);
					}
				}

				if (ch == quote) {
					readMode = Mode.READ_STRING;
					break;
				} else if (ch == '\n') {
					doNotScan = false;
					result = ArrayUtil.grow(result, buffer.toString().getBytes());
					result = ArrayUtil.grow(result, (byte) 0);
					int len = result.length;
					result = ArrayUtil.update(result, NumericConverter.getWord(2049 + len), offset);
					offset = len;
					buffer = new StringBuilder();
					readMode = Mode.READ_LINENUMBER;
					break;
				}
				break;
			}
			case READ_STRING: {
				buffer.append(ch);
				if (ch == quote) {
					byte[] ba = mapUniCodeCharacters(buffer, charMap);
					result = ArrayUtil.grow(result, ba);
					buffer = new StringBuilder();
					readMode = Mode.READ_INSTRUCTIONS;
				}
				break;
			}
			}
		}
		result = ArrayUtil.grow(result, new byte[] { 0, 0 });
		float diff = (System.currentTimeMillis() - startTime) / 1000f;
		System.out.printf("time to build:%f seconds\n", diff);
		return result;
	}

	private static byte[] mapUniCodeCharacters(StringBuilder sb, List<CharObject> charMap) {

		byte[] ba = new byte[sb.length()];
		CharacterIterator ci = new StringCharacterIterator(sb.toString());
		for (char ch = ci.first(); ch != CharacterIterator.DONE; ch = ci.next()) {
			boolean found = false;
			for (CharObject cm : charMap) {
				int i = Character.getType(ch);
				if (ch == cm.getUnicode() && i == Character.PRIVATE_USE) {
					ba[ci.getIndex()] = (byte) cm.getId();
					found = true;
					break;
				}
			}
			if (!found) {
				ba[ci.getIndex()] = (byte) ch;
			}
		}
		return ba;
	}

}
