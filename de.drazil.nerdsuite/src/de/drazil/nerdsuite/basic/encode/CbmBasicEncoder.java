package de.drazil.nerdsuite.basic.encode;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.List;

import de.drazil.nerdsuite.model.BasicInstruction;
import de.drazil.nerdsuite.model.BasicInstructions;
import de.drazil.nerdsuite.model.CharObject;
import de.drazil.nerdsuite.util.ArrayUtil;
import de.drazil.nerdsuite.util.NumericConverter;

public class CbmBasicEncoder extends AbstractTokenEncoder {

	public CbmBasicEncoder() {

	}

	public byte[] encode(String content, BasicInstructions basicInstructions, List<CharObject> charMap, boolean debug) {
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

			processMetaDirective(content, ci.getIndex(), true);
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

}
