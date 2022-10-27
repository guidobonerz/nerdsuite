package de.drazil.nerdsuite.basic;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.drazil.nerdsuite.log.Console;
import de.drazil.nerdsuite.model.BasicInstruction;
import de.drazil.nerdsuite.model.BasicInstructions;
import de.drazil.nerdsuite.model.CharObject;
import de.drazil.nerdsuite.util.ArrayUtil;
import de.drazil.nerdsuite.util.NumericConverter;

public class CbmBasicTokenizer {

	enum Mode {
		READ_LINENUMBER, READ_INSTRUCTIONS, READ_STRING, READ_BLOCK_COMMENT, READ_LINE_COMMENT, READ_DEBUG_BLOCK;
	};

	enum LastRead {
		NUMERIC, ALPHANUMERIC, LETTER, WHITESPACE, NONE;
	}

	private static Pattern directivePattern = Pattern.compile("(@[a-zA-Z]*)\\s*([a-zA-Z]*)?");
	private static CharacterIterator ci;
	private static boolean isInDataLine = false;
	private static boolean isInDebugMode = false;
	private static boolean isInIfBlock = false;
	private static boolean isInElseBlock = false;

	public CbmBasicTokenizer() {

	}

	public static byte[] tokenize(String content, BasicInstructions basicInstructions, List<CharObject> charMap,
			boolean debug) {
		isInDebugMode = debug;
		boolean doNotScan = false;
		byte[] result = new byte[] {};

		int offset = 0;
		char quote = basicInstructions.getStringQuote().charAt(0);
		ci = new StringCharacterIterator(content);
		StringBuilder buffer = new StringBuilder();
		char ch = 0;
		Mode readMode = Mode.READ_LINENUMBER;
		Mode lastReadMode = readMode;
		LastRead lastRead = LastRead.NONE;
		ch = ci.first();
		while (ch != CharacterIterator.DONE) {
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

			processDirective(content, ci.getIndex(), true);
			ch = ci.current();

			switch (readMode) {
			case READ_BLOCK_COMMENT: {
				int match = 0;
				while ((match = content.indexOf(basicInstructions.getBlockComment()[1], ci.getIndex())) != -1) {
					ch = ci.next();
				}
				ch = ci.next();
				readMode = lastReadMode;
				break;
			}
			case READ_LINE_COMMENT: {
				if (content.indexOf("\n", ci.getIndex()) == ci.getIndex()) {
					readMode = lastReadMode;
				}
				break;
			}
			case READ_LINENUMBER: {
				if (Character.isLetter(ch) || ch == ':') {
					isInDataLine = false;
					readMode = Mode.READ_INSTRUCTIONS;
					byte[] ba = NumericConverter.getWord(Integer.valueOf(buffer.toString()));
					result = ArrayUtil.grow(result, new byte[] { 0, 0 });
					result = ArrayUtil.grow(result, ba);
					buffer = new StringBuilder();
				}
				while (Character.isWhitespace(ch)) {
					ch = ci.next();
				}
				while (Character.isDigit(ch)) {
					buffer.append(ch);
					ch = ci.next();
				}
				break;
			}
			case READ_INSTRUCTIONS: {
				boolean found = false;
				if (!doNotScan) {
					int start = ci.getIndex();
					for (BasicInstruction instruction : basicInstructions.getBasicInstructionList()) {
						if (content.indexOf(instruction.getInstruction().toUpperCase(), start) == start
								&& !instruction.getPurpose().equals("R")) {
							if (instruction.getInstruction().equalsIgnoreCase("data") && !isInDataLine) {
								isInDataLine = true;
							}

							doNotScan = instruction.isComment();
							result = ArrayUtil.grow(result, buffer.toString().getBytes());

							int index = instruction.getSelectedTokenIndex();
							String token = instruction.getTokens().get(index).getToken();
							byte b = (byte) (Integer.parseInt(token, 16) & 0xff);

							if (isInDataLine) {
								if (instruction.getInstruction().equals("-")) {
									b = 0x2d;
								}
							}
							result = ArrayUtil.grow(result, b);

							ci.setIndex(start + (instruction.getInstruction().length() - 1));
							buffer = new StringBuilder();
							found = true;
							break;
						}
					}
					if (!found) {
						if (!Character.isWhitespace(ch)) {
							buffer.append(ch);
						}
					}
				} else {
					if (!Character.isWhitespace(ch)) {
						buffer.append(ch);
					}
				}

				if (ch == quote) {
					readMode = Mode.READ_STRING;
				} else if (ch == '\n' || ci.getIndex() == content.length() - 1) {
					doNotScan = false;
					result = ArrayUtil.grow(result, buffer.toString().getBytes());
					result = ArrayUtil.grow(result, (byte) 0);
					int len = result.length;
					result = ArrayUtil.update(result, NumericConverter.getWord(2049 + len), offset);
					offset = len;
					buffer = new StringBuilder();
					readMode = Mode.READ_LINENUMBER;
				}
				ch = ci.next();
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
				ch = ci.next();
				break;
			}
			}
		}

		result = ArrayUtil.grow(result, new byte[] { 0, 0 });

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

	private static void processDirective(String content, int startIndex, boolean lineOnly) {

		Matcher matcher = directivePattern.matcher(content);
		if (lineOnly) {
			int end = content.indexOf('\r', startIndex);
			matcher.region(startIndex, end != -1 ? end : content.length() - 1);
		} else {
			matcher.region(startIndex, content.length());
		}
		if (matcher.find()) {
			int count = matcher.groupCount();
			String m1 = matcher.group(1);
			String m2 = matcher.group(2);
			if (count == 2 && m2.equals("")) {
				if (matcher.group(1).equalsIgnoreCase("@end")) {
					ci.setIndex(matcher.start() + matcher.group(1).length() + 1);
					if (!isInIfBlock) {
						Console.println("@END without @IF");
						return;
					} else {
						isInIfBlock = false;
						isInElseBlock = false;
					}
				}
				if (matcher.group(1).equalsIgnoreCase("@else")) {
					ci.setIndex(matcher.start() + matcher.group(1).length() + 1);
					if (!isInIfBlock) {
						Console.println("@ELSE without @IF");
						return;
					} else {
						isInElseBlock = true;
						if (isInDebugMode) {
							processDirective(content, ci.getIndex(), false);
						}
					}
				}
			} else if (count == 2 && !m1.equals(matcher) && !m2.equals(matcher)) {
				if (matcher.group(1).equalsIgnoreCase("@if") && matcher.group(2).equalsIgnoreCase("debug")) {
					isInIfBlock = true;
					ci.setIndex(startIndex + matcher.group().length() + 1);
					if (!isInDebugMode) {
						processDirective(content, ci.getIndex(), false);
					}
				}
			}
		}

	}
}
