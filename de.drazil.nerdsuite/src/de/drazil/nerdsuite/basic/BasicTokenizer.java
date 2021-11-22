package de.drazil.nerdsuite.basic;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.List;
import java.util.stream.Collectors;

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
			case READ_INSTRUCTION: {
				if (ch == '\n') {
					basicCommentFound = false;
					result = ArrayUtil.grow(result, buffer.toString().getBytes());
					result = ArrayUtil.grow(result, (byte) 0);
					result = ArrayUtil.update(result, NumericConverter.getWord(result.length + 1), 0);
					buffer = new StringBuilder();
					readMode = Mode.READ_LINENUMBER;
					break;
				} else if (ch == '\"') {
					readMode = Mode.READ_STRING;
					System.out.println(ch);
					break;
				} else if (Character.isWhitespace(ch)
						&& (lastRead == LastRead.NONE || lastRead == LastRead.WHITESPACE)) {
					lastRead = LastRead.WHITESPACE;
				} else if (!Character.isWhitespace(ch)) {
					buffer.append(ch);
					if (!basicCommentFound) {
						List<BasicInstruction> list = findToken(buffer.toString(),
								basicInstructions.getBasicInstructionList());

						if (list.size() == 1 && list.get(0).getInstruction().equalsIgnoreCase(buffer.toString())) {
							BasicInstruction firstMatch = list.get(0);
							basicCommentFound = firstMatch.isComment();
							byte b = (byte) (Integer.parseInt(firstMatch.getToken(), 16) & 0xff);
							result = ArrayUtil.grow(result, b);
							buffer = new StringBuilder();
						}
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
		result = ArrayUtil.grow(result, new byte[] { 0, 0 });
		return result;
	}

	private static List<BasicInstruction> findToken(String command, List<BasicInstruction> basicInructionList) {
		return basicInructionList.stream()
				.filter(i -> i.getInstruction().toLowerCase().startsWith(command.toLowerCase()))
				.collect(Collectors.toList());

	}
}
